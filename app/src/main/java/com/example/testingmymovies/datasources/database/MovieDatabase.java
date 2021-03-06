package com.example.testingmymovies.datasources.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.testingmymovies.repository.pojo.FavouriteMovie;
import com.example.testingmymovies.repository.pojo.Movie;
import com.example.testingmymovies.repository.pojo.Review;
import com.example.testingmymovies.repository.pojo.Trailer;

@Database(entities = {Movie.class, FavouriteMovie.class, Trailer.class, Review.class}, version = 14, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {

    private static MovieDatabase database;
    private static final String DB_NAME = "moviedatabase.db";
    private static final Object LOCK = new Object();

    public static MovieDatabase getInstance(Context context) {
        synchronized (LOCK) {
            if (database == null) {
                database = Room.databaseBuilder(context, MovieDatabase.class, DB_NAME).fallbackToDestructiveMigration().build();
            }
            return database;
        }
    }

    public abstract MovieDao movieDao();
}