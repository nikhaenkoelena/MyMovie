package com.example.testingmymovies.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.testingmymovies.repository.Repository;
import com.example.testingmymovies.repository.pojo.FavouriteMovie;
import com.example.testingmymovies.repository.pojo.Movie;
import com.example.testingmymovies.repository.pojo.Review;
import com.example.testingmymovies.repository.pojo.Trailer;

import java.util.List;

public class DetailViewModel extends AndroidViewModel {

    private Repository repository;
    private LiveData<List<Trailer>> trailers;
    private LiveData<List<Review>> reviews;
    private MutableLiveData<Movie> movieFromDb;
    private MutableLiveData<FavouriteMovie> favouriteMovieFromDb;
    private MutableLiveData<Throwable> errors;

    public DetailViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(getApplication());
        errors = new MutableLiveData<>();
        movieFromDb = new MutableLiveData<>();
        favouriteMovieFromDb = new MutableLiveData<>();

    }

    public LiveData<List<Trailer>> getTrailers() {
        trailers = repository.getTrailers();
        return trailers;
    }

    public LiveData<List<Review>> getReviews() {
        reviews = repository.getReviews();
        return reviews;
    }


    public MutableLiveData<Movie> getMovieFromDb() {
        movieFromDb = repository.getMovieFromDb();
        return movieFromDb;
    }

    public MutableLiveData<FavouriteMovie> getFavouriteMovieFromDb() {
        favouriteMovieFromDb = repository.getFavouriteMovieFromDb();
        return favouriteMovieFromDb;
    }

    public MutableLiveData<Throwable> getErrors() {
        errors = repository.getErrors();
        return errors;
    }


    public void loadTrailers(int id, String lang) {
        repository.loadTrailers(id, lang);
    }

    public void loadReviews(int id, String lang) {
        repository.loadReviews(id, lang);
    }

    public void deleteAllTrailers() {
        repository.deleteAllTrailers();
    }

    public void deleteAllReviews() {
        repository.deleteAllReviews();
    }

    public void loadMovieById(int id) {
        repository.loadMovieById(id);
    }

    public void loadFavouriteMovieById(int id) {
        repository.loadFavouriteMovieById(id);
    }

    public void clearError() {
        repository.clearError();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.dispose();
    }
}
