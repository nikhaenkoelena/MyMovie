package com.example.testingmymovies.api;

import com.example.testingmymovies.pojo.ReviewsResult;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiServiceReviews {

    @GET("movie/{id}/reviews")
    Observable<ReviewsResult> getReviews (@Path ("id") int id, @Query("api_key") String API_KEY, @Query("language") String language);
}
