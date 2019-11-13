package com.example.testingmymovies.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.testingmymovies.pojo.FavouriteMovie;
import com.example.testingmymovies.pojo.Movie;
import com.example.testingmymovies.pojo.Trailer;

import java.util.List;

import retrofit2.http.DELETE;

@Dao
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

}
