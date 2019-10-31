package com.example.testingmymovies.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.testingmymovies.pojo.Movie;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM moviestable")
    LiveData<List<Movie>> getAllMovies ();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovies (List<Movie> movies);

    @Query("DELETE FROM moviestable")
    void deleteAllMovies ();

}
