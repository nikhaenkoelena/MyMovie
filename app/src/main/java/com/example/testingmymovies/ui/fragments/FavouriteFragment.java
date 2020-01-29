package com.example.testingmymovies.ui.fragments;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.example.testingmymovies.ui.adapters.MovieAdapter;
import com.example.testingmymovies.viewmodels.FavouriteViewModel;
import com.example.testingmymovies.repository.pojo.FavouriteMovie;
import com.example.testingmymovies.repository.pojo.Movie;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FavouriteFragment extends Fragment {

    @BindView(R.id.recyclerViewFavouriteMovies) RecyclerView recyclerView;
    private MovieAdapter adapter;
    private FavouriteViewModel viewModel;
    private NavController navController;

    private Unbinder unbinder;

    private static final int WIDTHOFPOSTER = 185;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favourite_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        Objects.requireNonNull(getActivity()).setTitle(R.string.menu_option_favourite);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), getColumCount()));
        adapter = new MovieAdapter();
        recyclerView.setAdapter(adapter);
        viewModel = ViewModelProviders.of(this).get(FavouriteViewModel.class);
        setOnPosterClickListener();
        getFavouriteMovie();
    }

    private int getColumCount() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels / displayMetrics.density);
        return width / WIDTHOFPOSTER > 2 ? width / WIDTHOFPOSTER : 2;
    }

    private void getFavouriteMovie() {
        final LiveData<List<FavouriteMovie>> favouritemovies = viewModel.getFavouriteMovies();
        favouritemovies.observe(this, new Observer<List<FavouriteMovie>>() {
            @Override
            public void onChanged(List<FavouriteMovie> favouriteMovies) {
                List<Movie> movies = new ArrayList<>();
                if (favouriteMovies != null) {
                    movies.addAll(favouriteMovies);
                    adapter.setMovies(movies);
                }
            }
        });
    }

    private void setOnPosterClickListener() {
        adapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Movie movie = adapter.getMovies().get(position);
                Bundle bundle = new Bundle();
                bundle.putInt("id", movie.getId());
                navController.navigate(R.id.action_favouriteFragment_to_detailFragment, bundle);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
