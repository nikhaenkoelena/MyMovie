package com.example.testingmymovies.api;

import com.example.testingmymovies.pojo.MovieResult;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiServise {

    @GET("movie?api_key=978314745d3ce652ac32e226b079bf48")
    Observable<MovieResult> getMovies(@Query("language") String language, @Query("sort_by") String typeOfSort, @Query("vote_count.gte") int vote, @Query("page") int page);
}
