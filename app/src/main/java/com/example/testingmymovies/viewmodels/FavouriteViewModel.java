package com.example.testingmymovies.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.testingmymovies.repository.Repository;
import com.example.testingmymovies.repository.pojo.FavouriteMovie;

import java.util.List;

public class FavouriteViewModel extends AndroidViewModel {

    private Repository repository;
    private LiveData<List<FavouriteMovie>> favouriteMovies;

    public FavouriteViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(getApplication());
        favouriteMovies = repository.getFavouriteMovies();
    }

    public LiveData<List<FavouriteMovie>> getFavouriteMovies() {
        return favouriteMovies;
    }

    public void insertFavouriteMovie(FavouriteMovie movie) {
        repository.insertFavouriteMovie(movie);
    }

    public void deleteFavouriteMovie(FavouriteMovie favouriteMovie) {
        repository.deleteFavouriteMovie(favouriteMovie);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.dispose();
    }

}
