package com.example.testingmymovies.repository;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.testingmymovies.R;
import com.example.testingmymovies.datasources.api.ApiFactory;
import com.example.testingmymovies.datasources.api.ApiService;
import com.example.testingmymovies.datasources.api.ApiServiceReviews;
import com.example.testingmymovies.datasources.api.ApiServiceVideo;
import com.example.testingmymovies.datasources.database.MovieDatabase;
import com.example.testingmymovies.repository.pojo.FavouriteMovie;
import com.example.testingmymovies.repository.pojo.Movie;
import com.example.testingmymovies.repository.pojo.MovieResult;
import com.example.testingmymovies.repository.pojo.Review;
import com.example.testingmymovies.repository.pojo.ReviewsResult;
import com.example.testingmymovies.repository.pojo.Trailer;
import com.example.testingmymovies.repository.pojo.TrailersResult;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class Repository {

    private static Repository repository;
    private static MovieDatabase database;
    private CompositeDisposable compositeDisposable;
    public LiveData<List<Movie>> movies;
    private LiveData<List<Trailer>> trailers;
    private LiveData<List<Review>> reviews;
    private MutableLiveData<Movie> movieFromDb;
    private MutableLiveData<FavouriteMovie> favouriteMovieFromDb;
    public MutableLiveData<Throwable> errors;
    private LiveData<List<FavouriteMovie>> favouriteMovies;
    private Context context;

    private static final String BASE_YOUTUBE_URL = "https://www.youtube.com/watch?v=";
    private static final String API_KEY = "978314745d3ce652ac32e226b079bf48";
    public static final String BASE_POSTER_URL = "https://image.tmdb.org/t/p/";
    public static final String SMALL_POSTER_SIZE = "w185";
    public static final String BIG_POSTER_SIZE = "w780";

    private static final String SORT_BY_POPULARITY = "popularity.desc";
    private static final String SORT_BY_TOP_RATED = "vote_average.desc";
    private static final int MIN_VOTE_COUNT_VALUE = 1000;

    public Repository(Context context) {
        this.context = context;
        database = MovieDatabase.getInstance(context);
        movies = database.movieDao().getAllMovies();
        trailers = database.movieDao().getTrailers();
        reviews = database.movieDao().getReviews();
        compositeDisposable = new CompositeDisposable();
        errors = new MutableLiveData<>();
        movieFromDb = new MutableLiveData<>();
        favouriteMovieFromDb = new MutableLiveData<>();
        favouriteMovies = database.movieDao().getAllFavouriteMovies();
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }

    public LiveData<List<Trailer>> getTrailers() {
        return trailers;
    }

    public LiveData<List<Review>> getReviews() {
        return reviews;
    }

    public MutableLiveData<Movie> getMovieFromDb() {
        return movieFromDb;
    }

    public MutableLiveData<FavouriteMovie> getFavouriteMovieFromDb() {
        return favouriteMovieFromDb;
    }

    public LiveData<List<FavouriteMovie>> getFavouriteMovies() {
        return favouriteMovies;
    }

    public MutableLiveData<Throwable> getErrors() {
        return errors;
    }

    public static Repository getInstance(Context context) {
        if (repository == null) {
            repository = new Repository(context);
        }
        return repository;
    }

    public void deleteAllMovies() {
        compositeDisposable.add(Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                database.movieDao().deleteMoviesTransaction();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }

    public void insertMovies(final List<Movie> movies) {
        compositeDisposable.add(Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                if (movies != null && movies.size() > 0) {
                    database.movieDao().insertMovies(movies);
                } else {
                    Toast.makeText(context, R.string.error_no_movies, Toast.LENGTH_SHORT).show();
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
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
        Disposable disposable = apiService.getMovies(API_KEY, lang, sortBy, MIN_VOTE_COUNT_VALUE, pageNumber)
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

    public void loadFavouriteMovieById(int id) {
        compositeDisposable.add(database.movieDao().getFavouriteMovieById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<FavouriteMovie>() {
                    @Override
                    public void accept(FavouriteMovie favouriteMovie) throws Exception {
                        favouriteMovieFromDb.postValue(favouriteMovie);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        favouriteMovieFromDb.postValue(null);
                    }
                }));
    }

    public void insertFavouriteMovie(final FavouriteMovie movie) {
        compositeDisposable.add(Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                if (movie != null) {
                    database.movieDao().insertFavouriteMovieTransaction(movie);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }


    public void deleteFavouriteMovie(final FavouriteMovie favouriteMovie) {
        compositeDisposable.add(Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                database.movieDao().deleteFavouriteMovie(favouriteMovie);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }

    public void clearError() {
        errors.setValue(null);
    }

    public void dispose() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }
}
