package com.example.testingmymovies.screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testingmymovies.MovieViewModel;
import com.example.testingmymovies.R;
import com.example.testingmymovies.pojo.FavouriteMovie;
import com.example.testingmymovies.pojo.Movie;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView textViewTitle;
    private TextView textViewOriginTitle;
    private TextView textViewReleaseDate;
    private TextView textViewOverview;
    private MovieViewModel viewModel;
    private ImageView imageViewAddToFavourite;
    private int id;
    private Movie movie;
    private FavouriteMovie favouriteMovie;

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
        setContentView(R.layout.activity_detail);
        imageView = findViewById(R.id.imageViewBigPoster);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewOriginTitle = findViewById(R.id.textViewOriginTitle);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        textViewOverview = findViewById(R.id.textViewOverView);
        imageViewAddToFavourite = findViewById(R.id.imageViewAddToFavourite);
        viewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id")) {
            id = intent.getIntExtra("id", -1);
            movie = viewModel.getMovieById(id);
            Picasso.get().load(movie.getPoster_path_big()).into(imageView);
            textViewTitle.setText(movie.getTitle());
            textViewOriginTitle.setText(movie.getOriginal_title());
            textViewReleaseDate.setText(movie.getRelease_date());
            textViewOverview.setText(movie.getOverview());
        } else {
            Toast.makeText(this, R.string.loading_error, Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent1);
        }
        setFavourite();
    }

    public void onClickAddToFavourite(View view) {
        if (favouriteMovie == null) {
            viewModel.insertFavouriteMovie(new FavouriteMovie(movie));
            Toast.makeText(this, R.string.added_to_favourite, Toast.LENGTH_SHORT).show();
        } else {
            viewModel.deleteFavouriteMovie(favouriteMovie);
            Toast.makeText(this, R.string.removed_from_favourite, Toast.LENGTH_SHORT).show();
        }
        setFavourite();
    }

    private void setFavourite () {
        favouriteMovie = viewModel.getFavouriteMovieById(id);
        if (favouriteMovie == null) {
            Picasso.get().load(R.drawable.favourite_add_to).into(imageViewAddToFavourite);
        } else {
            Picasso.get().load(R.drawable.favourite_remove).into(imageViewAddToFavourite);
        }
    }
}
