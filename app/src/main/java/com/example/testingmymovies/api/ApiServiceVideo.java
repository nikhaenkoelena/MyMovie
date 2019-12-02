package com.example.testingmymovies.api;

import com.example.testingmymovies.pojo.Trailer;
import com.example.testingmymovies.pojo.TrailersResult;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiServiceVideo {

    @GET ("movie/{id}/videos")
    Observable<TrailersResult> getTrailers (@Path ("id") int id, @Query("api_key") String API_KEY, @Query("language") String language);
}
