package com.example.testingmymovies.screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testingmymovies.MovieViewModel;
import com.example.testingmymovies.R;
import com.example.testingmymovies.adapters.MovieAdapter;
import com.example.testingmymovies.pojo.Movie;

import java.util.List;
import java.util.Locale;

import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.itemMenuMain:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                break;
            case R.id.itemMenuFavourite:
                Intent intent1 = new Intent(getApplicationContext(), FavouriteActivity.class);
                startActivity(intent1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerViewMovies);
        switchSortBy = findViewById(R.id.switch1);
        switchSortBy.setChecked(false);
        methodOfSort = 0;
        page = 1;
        isLoading = false;
        lang = Locale.getDefault().getLanguage();
        popularity = findViewById(R.id.textViewPopularity);
        topRated = findViewById(R.id.textViewVoted);
        progressBar = findViewById(R.id.progressBar);
        popularity.setTextColor(getResources().getColor(R.color.colorAccent));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new MovieAdapter();
        recyclerView.setAdapter(adapter);
        viewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        adapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Movie movie = adapter.getMovies().get(position);
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra("id", movie.getId());
                startActivity(intent);
            }
        });
        switchSortBy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                page = 1;
                setMethodOfSort(isChecked);
            }
        });
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
        viewModel.loadData(lang, methodOfSort, page);
    }

    public void onClickTopRated(View view) {
        setMethodOfSort(true);
        switchSortBy.setChecked(true);
    }

    public void onClickPopularity(View view) {
        setMethodOfSort(false);
        switchSortBy.setChecked(false);
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
