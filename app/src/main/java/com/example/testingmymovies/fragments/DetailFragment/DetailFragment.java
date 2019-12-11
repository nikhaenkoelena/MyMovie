package com.example.testingmymovies.fragments.DetailFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

public class DetailFragment extends Fragment {

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_fragment, container, false);
        imageView = view.findViewById(R.id.imageViewBigPoster);
        textViewTitle = view.findViewById(R.id.textViewTitle);
        textViewOriginTitle = view.findViewById(R.id.textViewOriginTitle);
        textViewReleaseDate = view.findViewById(R.id.textViewReleaseDate);
        textViewOverview = view.findViewById(R.id.textViewOverView);
        imageViewAddToFavourite = view.findViewById(R.id.imageViewAddToFavourite);
        textViewLabelTrailers = view.findViewById(R.id.textViewLabelTrailers);
        textViewLabelReviews = view.findViewById(R.id.textViewLabelReviews);
        lang = Locale.getDefault().getLanguage();
        setOnClicListenerkAddToFavourite();
        detailViewModel = ViewModelProviders.of(this).get(DetailViewModel.class);
        favouriteViewModel = ViewModelProviders.of(this).get(FavouriteViewModel.class);
        id = getArguments().getInt("id");
        movie = detailViewModel.getMovieById(id);
        getActivity().setTitle(movie.getTitle());
        Picasso.get().load(movie.getPoster_path_big()).placeholder(R.drawable.placeholder).into(imageView);
        textViewTitle.setText(movie.getTitle());
        textViewOriginTitle.setText(movie.getOriginal_title());
        textViewReleaseDate.setText(movie.getRelease_date());
        textViewOverview.setText(movie.getOverview());
//        } else {
//            Toast.makeText(this, R.string.loading_error, Toast.LENGTH_SHORT).show();
//            Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(intent1);
//        }
        setFavourite();
        recyclerViewTrailers = view.findViewById(R.id.recyclerViewTrailers);
        recyclerViewReviews = view.findViewById(R.id.recyclerViewReviews);
        trailersAdapter =  new TrailersAdapter();
        reviewsAdapter = new ReviewsAdapter();
        recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewTrailers.setAdapter(trailersAdapter);
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(getContext()));
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
                    Toast.makeText(getContext(), R.string.loading_error, Toast.LENGTH_SHORT).show();
                    detailViewModel.clearError();
                }
            }
        });
        return view;
    }

    public void setOnClicListenerkAddToFavourite() {
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

}
