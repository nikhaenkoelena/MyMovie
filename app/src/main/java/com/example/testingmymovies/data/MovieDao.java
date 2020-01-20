package com.example.testingmymovies.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.TypeConverters;

import com.example.testingmymovies.converters.Converter;
import com.example.testingmymovies.pojo.FavouriteMovie;
import com.example.testingmymovies.pojo.Movie;
import com.example.testingmymovies.pojo.Review;
import com.example.testingmymovies.pojo.Trailer;

import java.util.List;

import io.reactivex.Flowable;

@Dao
@TypeConverters(value = Converter.class)
public abstract class MovieDao {

    @Transaction
    public void deleteMoviesTransaction () {
        deleteAllMovies();
    }

    @Query("SELECT * FROM movies_table")
    public abstract LiveData<List<Movie>> getAllMovies ();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertMovies (List<Movie> movies);

    @Query("DELETE FROM movies_table")
    public abstract void deleteAllMovies ();

    @Insert
    public abstract void insertFavouriteMovie (FavouriteMovie movie);

    @Query("SELECT * FROM favourite_movies_table")
    public abstract LiveData<List<FavouriteMovie>> getAllFavouriteMovies ();

    @Query("SELECT * FROM movies_table WHERE id == :movieId")
    public abstract Movie getMovieById (int movieId);

    @Query("SELECT * FROM favourite_movies_table WHERE id == :favMovieId")
    public abstract FavouriteMovie getFavouriteMovieById (int favMovieId);

    @Delete
    public abstract void deleteFavouriteMovie (FavouriteMovie favouriteMovie);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertTrailers (List<Trailer> trailers);

    @Query("DELETE FROM trailers_table")
    public abstract void deleteAllTrailers ();

    @Query("SELECT * FROM trailers_table")
    public abstract LiveData<List<Trailer>> getTrailers ();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertReviews (List<Review> reviews);

    @Query("DELETE FROM reviews_table")
    public abstract void deleteAllReviews ();

    @Query("SELECT * FROM reviews_table")
    public abstract LiveData<List<Review>> getReviews ();

}
