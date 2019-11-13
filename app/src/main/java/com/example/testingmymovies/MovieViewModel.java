package com.example.testingmymovies;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.testingmymovies.api.ApiFactory;
import com.example.testingmymovies.api.ApiServise;
import com.example.testingmymovies.api.ApiServiseReviews;
import com.example.testingmymovies.api.ApiServiseVideo;
import com.example.testingmymovies.data.MovieDatabase;
import com.example.testingmymovies.pojo.FavouriteMovie;
import com.example.testingmymovies.pojo.Movie;
import com.example.testingmymovies.pojo.MovieResult;
import com.example.testingmymovies.pojo.Review;
import com.example.testingmymovies.pojo.ReviewsResult;
import com.example.testingmymovies.pojo.Trailer;
import com.example.testingmymovies.pojo.TrailersResult;
import com.example.testingmymovies.screens.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MovieViewModel extends AndroidViewModel {

    private static MovieDatabase database;
    private CompositeDisposable compositeDisposable;
    private CompositeDisposable compositeDisposableTrailers;
    private CompositeDisposable compositeDisposableReviews;
    public LiveData<List<Movie>> movies;
    public LiveData<List<FavouriteMovie>> favouriteMovies;
    public LiveData<List<Trailer>> trailers;
    public LiveData<List<Review>> reviews;

    public static final String BASE_POSTER_URL = "https://image.tmdb.org/t/p/";
    private static final String BASE_YOUTUBE_URL = "https://www.youtube.com/watch?v=";
    public static final String SMALL_POSTER_SIZE = "w185";
    public static final String BIG_POSTER_SIZE = "w780";

    private static final String SORT_BY_POPULARITY = "popularity.desc";
    private static final String SORT_BY_TOP_RATED = "vote_average.desc";
    private static final int MIN_VOTE_COUNT_VALUE = 1000;

    public MovieViewModel(@NonNull Application application) {
        super(application);
        database = MovieDatabase.getInstance(getApplication());
        movies = database.movieDao().getAllMovies();
        favouriteMovies = database.movieDao().getAllFavouriteMovies();
        compositeDisposable = new CompositeDisposable();
        compositeDisposableTrailers = new CompositeDisposable();
        compositeDisposableReviews = new CompositeDisposable();
        trailers = database.movieDao().getTrailers();
        reviews = database.movieDao().getReviews();
    }

    public LiveData<List<Movie>> getMovies () {
        return movies;
    }

    public LiveData<List<FavouriteMovie>> getFavouriteMovies () {return favouriteMovies; }

    public LiveData<List<Trailer>> getTrailers () {return trailers; }

    public LiveData<List<Review>> getReviews () {return reviews; }

    public void insertMovies (List<Movie> movies) {
        new InsertMovieTask().execute(movies);
    }

    public static class InsertMovieTask extends AsyncTask<List<Movie>, Void, Void> {
        @Override
        protected Void doInBackground(List<Movie>... lists) {
            if (lists != null && lists.length>0)
                database.movieDao().insertMovies(lists[0]);
            return null;
        }
    }

    public void deleteAllMovies () {
        new DeleteAllMoviesTask().execute();
    }

    public static class DeleteAllMoviesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            database.movieDao().deleteAllMovies();
            return null;
        }
    }

    public void insertFavouriteMovie (FavouriteMovie movie) {
        new InsertFavouriteMovieTask().execute(movie);
    }

    public static class InsertFavouriteMovieTask extends AsyncTask<FavouriteMovie, Void, Void> {

        @Override
        protected Void doInBackground(FavouriteMovie... favouriteMovies) {
            database.movieDao().insertFavouriteMovie(favouriteMovies[0]);
            return null;
        }
    }

    public Movie getMovieById (int id) {
        try {
            return new GetMovieByIdTask().execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class GetMovieByIdTask extends AsyncTask<Integer, Void, Movie> {

        @Override
        protected Movie doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0) {
                return database.movieDao().getMovieById(integers[0]);
            }
            return null;
        }
    }

    public FavouriteMovie getFavouriteMovieById (int id) {
        try {
            return new GetFavouriteMovieByIdTask().execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class GetFavouriteMovieByIdTask extends AsyncTask<Integer, Void, FavouriteMovie> {

        @Override
        protected FavouriteMovie doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0) {
                return database.movieDao().getFavouriteMovieById(integers[0]);
            }
            return null;
        }
    }

    public void deleteFavouriteMovie (FavouriteMovie favouriteMovie) {
        new DeleteFavouriteMovieTask().execute(favouriteMovie);
    }

    public static class DeleteFavouriteMovieTask extends AsyncTask<FavouriteMovie, Void, Void> {


        @Override
        protected Void doInBackground(FavouriteMovie... favouriteMovies) {
            database.movieDao().deleteFavouriteMovie(favouriteMovies[0]);
            return null;
        }
    }

    public void insertTrailers (List<Trailer> trailers) {
        new InsertTrailersTask().execute(trailers);
    }

    public static class InsertTrailersTask extends AsyncTask<List<Trailer>, Void, Void> {
        @Override
        protected Void doInBackground(List<Trailer>... lists) {
            if(lists != null && lists.length >0) {
                database.movieDao().insertTrailers(lists[0]);
            }
            return null;
        }
    }

    public void deleteAllTrailers () {
        new DeleteAllTrailersTask().execute();
    }

    public static class DeleteAllTrailersTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            database.movieDao().deleteAllTrailers();
            return null;
        }
    }

    public void deleteAllReviews () {
        new DeleteAllReviesTask().execute();
    }

    public static class DeleteAllReviesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            database.movieDao().deleteAllReviews();
            return null;
        }
    }

    public void insertReviews (List<Review> reviews) {
        new InsertReviewsTask().execute(reviews);
    }

    public static class InsertReviewsTask extends AsyncTask<List<Review>, Void, Void> {
        @Override
        protected Void doInBackground(List<Review>... lists) {
            if (lists != null && lists.length > 0) {
                database.movieDao().insertReviews(lists[0]);
            }
            return null;
        }
    }

    public void loadData (String lang, int methodOfSort, int page) {
        String sortBy = null;
        if (methodOfSort == 1) {
            sortBy = SORT_BY_TOP_RATED;
        } else {
            sortBy = SORT_BY_POPULARITY;
        }
        final int pageNumber = page;
        ApiFactory apiFactory = ApiFactory.getInstance();
        ApiServise apiServise = apiFactory.getApiServise();
        Disposable disposable = apiServise.getMovies(lang, sortBy, MIN_VOTE_COUNT_VALUE, pageNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MovieResult>() {
                    @Override
                    public void accept(MovieResult movieResult) throws Exception {
                        List<Movie> moviesForDb = new ArrayList<>();
                        List<Movie> moviesFromJSON = movieResult.getResults();
                        for(Movie movie :moviesFromJSON) {
                            movie.setPoster_path_big(BASE_POSTER_URL + BIG_POSTER_SIZE + movie.getPoster_path_small());
                            movie.setPoster_path_small(BASE_POSTER_URL + SMALL_POSTER_SIZE + movie.getPoster_path_small());
                            moviesForDb.add(movie);
                        }
                        if (pageNumber == 1) {
                            deleteAllMovies();
                        }
                        insertMovies(moviesForDb);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        compositeDisposable.add(disposable);
    }

    public void loadTrailers (int id, String lang) {
        ApiFactory apiFactory = ApiFactory.getInstance();
        ApiServiseVideo apiServiseVideo = apiFactory.getApiServiseVideo();
        Disposable disposable = apiServiseVideo.getTrailers(id, lang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<TrailersResult>() {
                    @Override
                    public void accept(TrailersResult trailersResult) throws Exception {
                        List<Trailer> trailers = new ArrayList<>();
                        List<Trailer> trailersFromJSON = trailersResult.getTrailers();
                        for (Trailer trailer : trailersFromJSON) {
                            trailer.setKey(BASE_YOUTUBE_URL + trailer.getKey());
                            Log.i("трейлер", trailer.getKey());
                            trailers.add(trailer);
                        }
                        deleteAllTrailers();
                        insertTrailers(trailers);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        compositeDisposableTrailers.add(disposable);
    }

    public void loadReviews (int id, String lang) {
        ApiFactory apiFactory = ApiFactory.getInstance();
        ApiServiseReviews apiServiseReviews = apiFactory.getApiServiseReviews();
        Disposable disposable = apiServiseReviews.getReviews(id, lang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ReviewsResult>() {
                    @Override
                    public void accept(ReviewsResult reviewsResult) throws Exception {
                        List<Review> reviews = reviewsResult.getReviews();
                        deleteAllReviews();
                        insertReviews(reviews);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });

    }



    @Override
    protected void onCleared() {
        super.onCleared();
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        } if (compositeDisposableTrailers!= null) {
            compositeDisposableTrailers.dispose();
        }
        if (compositeDisposableReviews != null) {
            compositeDisposableReviews.dispose();
        }
    }
}
