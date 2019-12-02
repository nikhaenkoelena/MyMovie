package com.example.testingmymovies.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
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
public interface MovieDao {

    @Query("SELECT * FROM moviestable")
    LiveData<List<Movie>> getAllMovies ();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovies (List<Movie> movies);

    @Query("DELETE FROM moviestable")
    void deleteAllMovies ();

    @Insert
    void insertFavouriteMovie (FavouriteMovie movie);

    @Query("SELECT * FROM favourite_moviestable")
    LiveData<List<FavouriteMovie>> getAllFavouriteMovies ();

    @Query("SELECT * FROM moviestable WHERE id == :movieId")
    Movie getMovieById (int movieId);

    @Query("SELECT * FROM favourite_moviestable WHERE id == :favMovieId")
    FavouriteMovie getFavouriteMovieById (int favMovieId);

    @Delete
    void deleteFavouriteMovie (FavouriteMovie favouriteMovie);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTrailers (List<Trailer> trailers);

    @Query("DELETE FROM trailerstable")
    void deleteAllTrailers ();

    @Query("SELECT * FROM trailerstable")
    LiveData<List<Trailer>> getTrailers ();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertReviews (List<Review> reviews);

    @Query("DELETE FROM reviewstable")
    void deleteAllReviews ();

    @Query("SELECT * FROM reviewstable")
    LiveData<List<Review>> getReviews ();

}
