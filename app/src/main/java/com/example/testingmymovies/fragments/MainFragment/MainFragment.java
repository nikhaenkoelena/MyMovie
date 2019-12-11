package com.example.testingmymovies.fragments.MainFragment;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testingmymovies.R;
import com.example.testingmymovies.adapters.MovieAdapter;
import com.example.testingmymovies.pojo.Movie;

import java.util.List;
import java.util.Locale;

import io.reactivex.disposables.CompositeDisposable;


public class MainFragment extends Fragment {

    NavController navController = null;
    private CompositeDisposable compositeDisposable;
    private LiveData<List<Movie>> movies;
    private MovieViewModel viewModel;
    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private Switch switchSortBy;
    private TextView popularity;
    private TextView topRated;
    private ProgressBar progressBar;

    private static int methodOfSort;
    private int page;
    private static String lang;

    private static boolean isLoading;
    private static final int WIDTHOFPOSTER = 185;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        getActivity().setTitle(R.string.app_title);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        recyclerView = view.findViewById(R.id.recyclerViewMovies);
        switchSortBy = view.findViewById(R.id.switch1);
        switchSortBy.setChecked(false);
        methodOfSort = 0;
        page = 1;
        isLoading = false;
        lang = Locale.getDefault().getLanguage();
        popularity = view.findViewById(R.id.textViewPopularity);
        topRated = view.findViewById(R.id.textViewVoted);
        setOnClickListenerPopularity();
        setOnClickListenerTopRated();
        progressBar = view.findViewById(R.id.progressBar);
        popularity.setTextColor(getResources().getColor(R.color.colorAccent));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), getColumCount()));
        adapter = new MovieAdapter();
        recyclerView.setAdapter(adapter);
        viewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        setOnPosterClickListener();
        setOnCheckedChangeListener();
        setOnReachEndListener();
        getMovies();
        viewModel.loadData(lang, methodOfSort, page);
    }

    private int getColumCount () {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels / displayMetrics.density);
        return width / WIDTHOFPOSTER > 2 ? width / WIDTHOFPOSTER : 2;
    }

    private void setOnPosterClickListener () {
        adapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Movie movie = adapter.getMovies().get(position);
                Bundle bundle = new Bundle();
                bundle.putInt("id", movie.getId());
                navController.navigate(R.id.action_mainFragment_to_detailFragment, bundle);
            }
        });
    }

    private void setOnCheckedChangeListener () {
        switchSortBy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                page = 1;
                setMethodOfSort(isChecked);
            }
        });
    }

    private void setOnReachEndListener () {
        adapter.setOnReachEndListener(new MovieAdapter.OnReachEndListener() {
            @Override
            public void onReachEnd() {
                if (!isLoading) {
                    viewModel.loadData(lang, methodOfSort, page);
                    progressBar.setVisibility(View.VISIBLE);
                    isLoading = true;
                }
            }
        });
    }

    private void getMovies () {
        movies = viewModel.getMovies();
        movies.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                if (movies != null && !movies.isEmpty()) {
                    adapter.setMovies(movies);
                    isLoading =false;
                    progressBar.setVisibility(View.INVISIBLE);
                    page++;
                }
            }
        });
    }

    public void setOnClickListenerTopRated() {
        topRated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMethodOfSort(true);
                switchSortBy.setChecked(true);
            }
        });
    }

    public void setOnClickListenerPopularity() {
        popularity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMethodOfSort(false);
                switchSortBy.setChecked(false);
            }
        });
    }

    private void setMethodOfSort (boolean isTopRated) {
        if (isTopRated) {
            popularity.setTextColor(getResources().getColor(R.color.white_color));
            topRated.setTextColor(getResources().getColor(R.color.colorAccent));
            methodOfSort = 1;
        } else {
            popularity.setTextColor(getResources().getColor(R.color.colorAccent));
            topRated.setTextColor(getResources().getColor(R.color.white_color));
            methodOfSort = 0;
        }
        viewModel.loadData(lang, methodOfSort, page);
    }
}
