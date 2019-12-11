package com.example.testingmymovies.fragments.FavouriteFragment;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.testingmymovies.data.MovieDatabase;
import com.example.testingmymovies.pojo.FavouriteMovie;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class FavouriteViewModel extends AndroidViewModel {

    private static MovieDatabase database;
    public LiveData<List<FavouriteMovie>> favouriteMovies;

    public FavouriteViewModel(@NonNull Application application) {
        super(application);
        database = MovieDatabase.getInstance(getApplication());
        favouriteMovies = database.movieDao().getAllFavouriteMovies();
    }

    public LiveData<List<FavouriteMovie>> getFavouriteMovies () {return favouriteMovies; }

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
}
