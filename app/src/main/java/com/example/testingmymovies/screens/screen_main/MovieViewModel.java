package com.example.testingmymovies.screens.screen_main;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.testingmymovies.api.ApiFactory;
import com.example.testingmymovies.api.ApiService;
import com.example.testingmymovies.data.MovieDatabase;
import com.example.testingmymovies.pojo.Movie;
import com.example.testingmymovies.pojo.MovieResult;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MovieViewModel extends AndroidViewModel {

    private static MovieDatabase database;
    private CompositeDisposable compositeDisposable;
    public LiveData<List<Movie>> movies;
    public MutableLiveData<Throwable> errors;

    public static final String BASE_POSTER_URL = "https://image.tmdb.org/t/p/";
    public static final String SMALL_POSTER_SIZE = "w185";
    public static final String BIG_POSTER_SIZE = "w780";

    private static final String SORT_BY_POPULARITY = "popularity.desc";
    private static final String SORT_BY_TOP_RATED = "vote_average.desc";
    private static final int MIN_VOTE_COUNT_VALUE = 1000;

    public MovieViewModel(@NonNull Application application) {
        super(application);
        database = MovieDatabase.getInstance(getApplication());
        movies = database.movieDao().getAllMovies();
        compositeDisposable = new CompositeDisposable();
        errors = new MutableLiveData<>();
    }

    public MutableLiveData<Throwable> getErrors() {
        return errors;
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }

    public void insertMovies(List<Movie> movies) {
        new InsertMovieTask().execute(movies);
    }

    public static class InsertMovieTask extends AsyncTask<List<Movie>, Void, Void> {
        @Override
        protected Void doInBackground(List<Movie>... lists) {
            if (lists != null && lists.length > 0)
                database.movieDao().insertMovies(lists[0]);
            return null;
        }
    }

    public void deleteAllMovies() {
        new DeleteAllMoviesTask().execute();
    }

    public static class DeleteAllMoviesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            database.movieDao().deleteAllMovies();
            return null;
        }
    }


    public void loadData(String lang, int methodOfSort, int page) {
        String sortBy = null;
        if (methodOfSort == 1) {
            sortBy = SORT_BY_TOP_RATED;
        } else {
            sortBy = SORT_BY_POPULARITY;
        }
        final int pageNumber = page;
        ApiFactory apiFactory = ApiFactory.getInstance();
        ApiService apiService = apiFactory.getApiServise();
        Disposable disposable = apiService.getMovies(lang, sortBy, MIN_VOTE_COUNT_VALUE, pageNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MovieResult>() {
                    @Override
                    public void accept(MovieResult movieResult) throws Exception {
                        List<Movie> moviesForDb = new ArrayList<>();
                        List<Movie> moviesFromJSON = movieResult.getResults();
                        for (Movie movie : moviesFromJSON) {
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