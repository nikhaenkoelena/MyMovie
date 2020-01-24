package com.example.testingmymovies.fragments.DetailFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testingmymovies.R;
import com.example.testingmymovies.adapters.ReviewsAdapter;
import com.example.testingmymovies.adapters.TrailersAdapter;
import com.example.testingmymovies.fragments.FavouriteFragment.FavouriteViewModel;
import com.example.testingmymovies.pojo.FavouriteMovie;
import com.example.testingmymovies.pojo.Movie;
import com.example.testingmymovies.pojo.Review;
import com.example.testingmymovies.pojo.Trailer;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DetailFragment extends Fragment {

    @BindView(R.id.imageViewBigPoster) ImageView imageView;
    @BindView(R.id.textViewTitle) TextView textViewTitle;
    @BindView(R.id.textViewOriginTitle) TextView textViewOriginTitle;
    @BindView(R.id.textViewReleaseDate) TextView textViewReleaseDate;
    @BindView(R.id.textViewOverView) TextView textViewOverview;
    @BindView(R.id.imageViewAddToFavourite) ImageView imageViewAddToFavourite;
    @BindView(R.id.textViewLabelTrailers) TextView textViewLabelTrailers;
    @BindView(R.id.textViewLabelReviews) TextView textViewLabelReviews;

    private DetailViewModel detailViewModel;
    private FavouriteViewModel favouriteViewModel;

    private int id;
    private Movie movie;
    private FavouriteMovie favouriteMovie;
    private String lang;

    @BindView(R.id.recyclerViewTrailers) RecyclerView recyclerViewTrailers;
    @BindView(R.id.recyclerViewReviews) RecyclerView recyclerViewReviews;
    private TrailersAdapter trailersAdapter;
    private ReviewsAdapter reviewsAdapter;

    private LiveData<List<Trailer>> trailers;
    private LiveData<List<Review>> reviews;

    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        lang = Locale.getDefault().getLanguage();
        setOnClickListenerAddToFavourite();
        detailViewModel = ViewModelProviders.of(this).get(DetailViewModel.class);
        favouriteViewModel = ViewModelProviders.of(this).get(FavouriteViewModel.class);
        if (getArguments() != null) {
            id = getArguments().getInt("id");
        }
        movie = detailViewModel.getMovieById(id);
        Objects.requireNonNull(getActivity()).setTitle(movie.getTitle());
        Picasso.get().load(movie.getPoster_path_big()).placeholder(R.drawable.placeholder).into(imageView);
        textViewTitle.setText(movie.getTitle());
        textViewOriginTitle.setText(movie.getOriginal_title());
        textViewReleaseDate.setText(movie.getRelease_date());
        textViewOverview.setText(movie.getOverview());
        setFavourite();
        trailersAdapter =  new TrailersAdapter();
        reviewsAdapter = new ReviewsAdapter();
        recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewTrailers.setAdapter(trailersAdapter);
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewReviews.setAdapter(reviewsAdapter);
        detailViewModel.deleteAllTrailers();
        detailViewModel.deleteAllReviews();
        detailViewModel.loadTrailers(id, lang);
        getTrailers();
        setOnClickListenerTrailers();
        detailViewModel.loadTrailers(id, lang);
        getReviews();
        detailViewModel.loadReviews(id, lang);
        getErrors();
        return view;
    }

    private void setOnClickListenerTrailers () {
        trailersAdapter.setOnTrailerClickListener(new TrailersAdapter.OnTrailerClickListener() {
            @Override
            public void onTrailerClick(String url) {
                Intent intentToTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intentToTrailer);
            }
        });
    }

    private void getTrailers () {
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
    }

    private void getReviews() {
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
    }

    private void getErrors() {
        detailViewModel.getErrors().observe(this, new Observer<Throwable>() {
            @Override
            public void onChanged(Throwable throwable) {
                if(throwable != null) {
                    Toast.makeText(getContext(), R.string.loading_error, Toast.LENGTH_SHORT).show();
                    detailViewModel.clearError();
                }
            }
        });
    }

    private void setOnClickListenerAddToFavourite() {
        imageViewAddToFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favouriteMovie == null) {
                    favouriteViewModel.insertFavouriteMovie(new FavouriteMovie(movie));
                    Toast.makeText(getContext(), R.string.added_to_favourite, Toast.LENGTH_SHORT).show();
                } else {
                    favouriteViewModel.deleteFavouriteMovie(favouriteMovie);
                    Toast.makeText(getContext(), R.string.removed_from_favourite, Toast.LENGTH_SHORT).show();
                }
                setFavourite();
            }
        });
    }

    private void setFavourite () {
        favouriteMovie = favouriteViewModel.getFavouriteMovieById(id);
        if (favouriteMovie == null) {
            Picasso.get().load(R.drawable.favourite_add_to).into(imageViewAddToFavourite);
        } else {
            Picasso.get().load(R.drawable.favourite_remove).into(imageViewAddToFavourite);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
