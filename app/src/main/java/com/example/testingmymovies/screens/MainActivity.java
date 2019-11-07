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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testingmymovies.MovieViewModel;
import com.example.testingmymovies.R;
import com.example.testingmymovies.adapters.MovieAdapter;
import com.example.testingmymovies.pojo.Movie;

import java.util.List;

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
    private static int methodOfSort;
    private int page;

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
        popularity = findViewById(R.id.textViewPopularity);
        topRated = findViewById(R.id.textViewVoted);
        popularity.setTextColor(getResources().getColor(R.color.colorAccent));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new MovieAdapter();
        recyclerView.setAdapter(adapter);
        viewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        adapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra("id", position);
                startActivity(intent);
            }
        });
        switchSortBy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setMethodOfSort(isChecked);
            }
        });
        adapter.setOnReachEndListener(new MovieAdapter.OnReachEndListener() {
            @Override
            public void onReachEnd() {
                viewModel.loadData(methodOfSort, page);
                Toast.makeText(MainActivity.this, "Конец списка", Toast.LENGTH_SHORT).show();
                Log.i("страница", Integer.toString(page));
            }
        });
        movies = viewModel.getMovies();
        movies.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                if (movies != null && !movies.isEmpty()) {
                    adapter.setMovies(movies);
                    page++;
                }
            }
        });
        viewModel.loadData(methodOfSort, page);
    }

    public void onClickTopRated(View view) {
        setMethodOfSort(true);
        methodOfSort = 1;
        viewModel.loadData(methodOfSort, page);
    }

    public void onClickPopularity(View view) {
        setMethodOfSort(false);
        methodOfSort = 0;
        viewModel.loadData(methodOfSort, page);
    }

    private void setMethodOfSort (boolean isTopRated) {
        if (isTopRated) {
            switchSortBy.setChecked(true);
            popularity.setTextColor(getResources().getColor(R.color.white_color));
            topRated.setTextColor(getResources().getColor(R.color.colorAccent));
        } else {
            switchSortBy.setChecked(false);
            popularity.setTextColor(getResources().getColor(R.color.colorAccent));
            topRated.setTextColor(getResources().getColor(R.color.white_color));
        }

    }

}
