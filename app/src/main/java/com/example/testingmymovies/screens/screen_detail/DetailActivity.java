package com.example.testingmymovies.screens.screen_detail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testingmymovies.R;
import com.example.testingmymovies.adapters.ReviewsAdapter;
import com.example.testingmymovies.adapters.TrailersAdapter;
import com.example.testingmymovies.pojo.FavouriteMovie;
import com.example.testingmymovies.pojo.Movie;
import com.example.testingmymovies.pojo.Review;
import com.example.testingmymovies.pojo.Trailer;
import com.example.testingmymovies.screens.screen_favourite.FavouriteActivity;
import com.example.testingmymovies.screens.screen_favourite.FavouriteViewModel;
import com.example.testingmymovies.screens.screen_main.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView textViewTitle;
    private TextView textViewOriginTitle;
    private TextView textViewReleaseDate;
    private TextView textViewOverview;
    private DetailViewModel detailViewModel;
    private FavouriteViewModel favouriteViewModel;
    private ImageView imageViewAddToFavourite;
    private int id;
    private Movie movie;
    private FavouriteMovie favouriteMovie;
    private String lang;

    private TextView textViewLabelTrailers;
    private TextView textViewLabelReviews;

    private RecyclerView recyclerViewTrailers;
    private RecyclerView recyclerViewReviews;
    private TrailersAdapter trailersAdapter;
    private ReviewsAdapter reviewsAdapter;

    private LiveData<List<Trailer>> trailers;
    private LiveData<List<Review>> reviews;

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
        textViewLabelTrailers = findViewById(R.id.textViewLabelTrailers);
        textViewLabelReviews = findViewById(R.id.textViewLabelReviews);
        lang = Locale.getDefault().getLanguage();
        detailViewModel = ViewModelProviders.of(this).get(DetailViewModel.class);
        favouriteViewModel = ViewModelProviders.of(this).get(FavouriteViewModel.class);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id")) {
            id = intent.getIntExtra("id", -1);
            movie = detailViewModel.getMovieById(id);
            Picasso.get().load(movie.getPoster_path_big()).placeholder(R.drawable.placeholder).into(imageView);
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
        recyclerViewTrailers = findViewById(R.id.recyclerViewTrailers);
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
        trailersAdapter =  new TrailersAdapter();
        reviewsAdapter = new ReviewsAdapter();
        recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTrailers.setAdapter(trailersAdapter);
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReviews.setAdapter(reviewsAdapter);
        detailViewModel.deleteAllTrailers();
        detailViewModel.deleteAllReviews();
        detailViewModel.loadTrailers(id, lang);
        trailers = detailViewModel.getTrailers();
        trailers.observe(this, new Observer<List<Trailer>>() {
            @Override
            public void onChanged(List<Trailer> trailers) {
                trailersAdapter.setTrailers(trailers);
                if(trailers.isEmpty()) {
                    textViewLabelTrailers.setVisibility(View.INVISIBLE);
                } else {textViewLabelTrailers.setVisibility(View.VISIBLE);}
            }
        });
        trailersAdapter.setOnTrailerClickListener(new TrailersAdapter.OnTrailerClickListener() {
            @Override
            public void onTrailerClick(String url) {
                Intent intentToTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intentToTrailer);
            }
        });
        detailViewModel.loadTrailers(id, lang);
        reviews = detailViewModel.getReviews();
        reviews.observe(this, new Observer<List<Review>>() {
            @Override
            public void onChanged(List<Review> reviews) {
                reviewsAdapter.setReviews(reviews);
                if (reviews.isEmpty()) {
                    textViewLabelReviews.setVisibility(View.INVISIBLE);
                } else {textViewLabelReviews.setVisibility(View.VISIBLE);}
            }
        });
        detailViewModel.loadReviews(id, lang);
        detailViewModel.getErrors().observe(this, new Observer<Throwable>() {
            @Override
            public void onChanged(Throwable throwable) {
                if(throwable != null) {
                    Toast.makeText(DetailActivity.this, R.string.loading_error, Toast.LENGTH_SHORT).show();
                    detailViewModel.clearError();
                }
            }
        });
    }

    public void onClickAddToFavourite(View view) {
        if (favouriteMovie == null) {
            favouriteViewModel.insertFavouriteMovie(new FavouriteMovie(movie));
            Toast.makeText(this, R.string.added_to_favourite, Toast.LENGTH_SHORT).show();
        } else {
            favouriteViewModel.deleteFavouriteMovie(favouriteMovie);
            Toast.makeText(this, R.string.removed_from_favourite, Toast.LENGTH_SHORT).show();
        }
        setFavourite();
    }

    private void setFavourite () {
        favouriteMovie = favouriteViewModel.getFavouriteMovieById(id);
        if (favouriteMovie == null) {
            Picasso.get().load(R.drawable.favourite_add_to).into(imageViewAddToFavourite);
        } else {
            Picasso.get().load(R.drawable.favourite_remove).into(imageViewAddToFavourite);
        }
    }
}
