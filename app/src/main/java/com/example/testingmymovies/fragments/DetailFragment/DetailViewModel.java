package com.example.testingmymovies.fragments.DetailFragment;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.testingmymovies.api.ApiFactory;
import com.example.testingmymovies.api.ApiServiceReviews;
import com.example.testingmymovies.api.ApiServiceVideo;
import com.example.testingmymovies.data.MovieDatabase;
import com.example.testingmymovies.pojo.Movie;
import com.example.testingmymovies.pojo.Review;
import com.example.testingmymovies.pojo.ReviewsResult;
import com.example.testingmymovies.pojo.Trailer;
import com.example.testingmymovies.pojo.TrailersResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DetailViewModel extends AndroidViewModel {

    private static MovieDatabase database;
    public LiveData<List<Trailer>> trailers;
    public LiveData<List<Review>> reviews;
    public MutableLiveData<Throwable> errors;
    private CompositeDisposable compositeDisposable;

    private static final String BASE_YOUTUBE_URL = "https://www.youtube.com/watch?v=";
    private static final String API_KEY = "978314745d3ce652ac32e226b079bf48";


    public DetailViewModel(@NonNull Application application) {
        super(application);
        database = MovieDatabase.getInstance(getApplication());
        trailers = database.movieDao().getTrailers();
        reviews = database.movieDao().getReviews();
        errors = new MutableLiveData<>();
        compositeDisposable = new CompositeDisposable();
    }

    public MutableLiveData<Throwable> getErrors() {
        return errors;
    }

    public LiveData<List<Review>> getReviews () {return reviews; }

    public LiveData<List<Trailer>> getTrailers () { return trailers; }

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

    public Movie getMovieById(int id) {
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

    public void loadTrailers (int id, String lang) {
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

    public void loadReviews (int id, String lang) {
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

    public void clearError () {
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
