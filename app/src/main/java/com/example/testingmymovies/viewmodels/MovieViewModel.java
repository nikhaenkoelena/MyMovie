package com.example.testingmymovies.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.testingmymovies.repository.Repository;
import com.example.testingmymovies.repository.pojo.Movie;

import java.util.List;

public class MovieViewModel extends AndroidViewModel {

    public LiveData<List<Movie>> movies;
    public MutableLiveData<Throwable> errors;
    private Repository repository;

    public MutableLiveData<Throwable> getErrors() {
        errors = repository.getErrors();
        return errors;
    }

    public MovieViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(getApplication());
    }

    public void loadData(String lang, int methodOfSort, int page) {
        repository.loadData(lang, methodOfSort, page);
    }

    public LiveData<List<Movie>> getMovies() {
        movies = repository.getMovies();
        return movies;
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
