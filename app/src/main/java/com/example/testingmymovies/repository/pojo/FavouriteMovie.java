package com.example.testingmymovies.repository.pojo;

import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "favourite_movies_table")
public class FavouriteMovie extends Movie {


    public FavouriteMovie(int uniqId, int id, int vote_count, double vote_average, String title, String original_title, String poster_path_small, String poster_path_big, String backdrop_path, String overview, String release_date) {
        super(uniqId, id, vote_count, vote_average, title, original_title, poster_path_small, poster_path_big, backdrop_path, overview, release_date);
    }

    @Ignore
    public FavouriteMovie(Movie movie) {
        super(movie.getUniqId(), movie.getId(), movie.getVote_count(), movie.getVote_average(), movie.getTitle(), movie.getOriginal_title(), movie.getPoster_path_small(), movie.getPoster_path_big(), movie.getBackdrop_path(), movie.getOverview(), movie.getRelease_date());
    }
}
