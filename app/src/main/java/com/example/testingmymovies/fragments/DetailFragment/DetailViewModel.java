package com.example.testingmymovies.fragments.DetailFragment;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.testingmymovies.api.ApiFactory;
import com.example.testingmymovies.api.ApiServiceReviews;
import com.example.testingmymovies.api.ApiServiceVideo;
import com.example.testingmymovies.data.MovieDatabase;
import com.example.testingmymovies.pojo.FavouriteMovie;
import com.example.testingmymovies.pojo.Movie;
import com.example.testingmymovies.pojo.Review;
import com.example.testingmymovies.pojo.ReviewsResult;
import com.example.testingmymovies.pojo.Trailer;
import com.example.testingmymovies.pojo.TrailersResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DetailViewModel extends AndroidViewModel {

    private static MovieDatabase database;
    private LiveData<List<Trailer>> trailers;
    private LiveData<List<Review>> reviews;
    private MutableLiveData<Movie> movieFromDb;
    private MutableLiveData<FavouriteMovie> favouriteMovieFromDb;
    private MutableLiveData<Throwable> errors;
    private CompositeDisposable compositeDisposable;

    private static final String BASE_YOUTUBE_URL = "https://www.youtube.com/watch?v=";
    private static final String API_KEY = "978314745d3ce652ac32e226b079bf48";
    private static final FavouriteMovie favouriteMovieNull = new FavouriteMovie(0, 0, 0, 0, null, null, null, null, null, null, null);

    public DetailViewModel(@NonNull Application application) {
        super(application);
        database = MovieDatabase.getInstance(getApplication());
        trailers = database.movieDao().getTrailers();
        reviews = database.movieDao().getReviews();
        errors = new MutableLiveData<>();
        movieFromDb = new MutableLiveData<>();
        favouriteMovieFromDb = new MutableLiveData<>();
        compositeDisposable = new CompositeDisposable();
    }

    public MutableLiveData<Throwable> getErrors() {
        return errors;
    }

    public LiveData<List<Review>> getReviews() {
        return reviews;
    }

    public LiveData<List<Trailer>> getTrailers() {
        return trailers;
    }

    public MutableLiveData<Movie> getMovieFromDb() {
        return movieFromDb;
    }

    public MutableLiveData<FavouriteMovie> getFavouriteMovieFromDb() {
        return favouriteMovieFromDb;
    }

    public void loadFavouriteMovieById(int id) {
        Log.i("CheckId", Integer.toString(id));
        compositeDisposable.add(database.movieDao().getFavouriteMovieByIdTransaction(id)
        .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<FavouriteMovie>() {
                    @Override
                    public void accept(FavouriteMovie favouriteMovie) throws Exception {
                        favouriteMovieFromDb.setValue(favouriteMovie);
                        Log.i("CheckFavMov", favouriteMovie.getTitle());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        favouriteMovieFromDb.setValue(null);
                        Log.i("CkeckLoadError", throwable.getMessage());
                    }
                }));
    }

    public void insertTrailers(final List<Trailer> trailers) {
        compositeDisposable.add(Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                if (trailers != null && trailers.size() > 0) {
                    database.movieDao().insertTrailers(trailers);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }

    public void deleteAllTrailers() {
        compositeDisposable.add(Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                database.movieDao().deleteTrailersTransaction();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }

    public void deleteAllReviews() {
        compositeDisposable.add(Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                database.movieDao().deleteReviewsTransaction();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }

    public void insertReviews(final List<Review> reviews) {
        compositeDisposable.add(Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                if (reviews != null && reviews.size() > 0) {
                    database.movieDao().insertReviews(reviews);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }

    public void loadMovieById(int id) {
        compositeDisposable.add(database.movieDao().getMovieById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Movie>() {
                    @Override
                    public void accept(Movie movie) throws Exception {
                        movieFromDb.setValue(movie);
                    }
                }));
    }

    public void loadTrailers(int id, String lang) {
        ApiFactory apiFactory = ApiFactory.getInstance();
        ApiServiceVideo apiServiceVideo = apiFactory.getApiServiseVideo();
        Disposable disposable = apiServiceVideo.getTrailers(id, API_KEY, lang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<TrailersResult>() {
                    @Override
                    public void accept(TrailersResult trailersResult) throws Exception {
                        List<Trailer> trailers = new ArrayList<>();
                        List<Trailer> trailersFromJSON = trailersResult.getTrailers();
                        for (Trailer trailer : trailersFromJSON) {
                            trailer.setKey(BASE_YOUTUBE_URL + trailer.getKey());
                            trailers.add(trailer);
                        }
                        deleteAllTrailers();
                        insertTrailers(trailers);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        errors.setValue(throwable);
                    }
                });
        compositeDisposable.add(disposable);
    }

    public void loadReviews(int id, String lang) {
        ApiFactory apiFactory = ApiFactory.getInstance();
        ApiServiceReviews apiServiceReviews = apiFactory.getApiServiseReviews();
        Disposable disposable = apiServiceReviews.getReviews(id, API_KEY, lang)
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
                        errors.setValue(throwable);
                    }
                });
        compositeDisposable.add(disposable);
    }

    public void clearError() {
        errors.setValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }
}
