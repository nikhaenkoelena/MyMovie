package com.example.testingmymovies.api;

import com.example.testingmymovies.pojo.MovieResult;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("discover/movie")
    Observable<MovieResult> getMovies(@Query("api_key") String API_KEY, @Query("language") String language, @Query("sort_by") String typeOfSort, @Query("vote_count.gte") int vote, @Query("page") int page);
}
