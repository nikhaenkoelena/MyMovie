package com.example.testingmymovies.api;

import com.example.testingmymovies.pojo.ReviewsResult;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiServiseReviews {

    @GET("{id}/reviews?api_key=978314745d3ce652ac32e226b079bf48")
    Observable<ReviewsResult> getReviews (@Path ("id") int id, @Query("language") String language);
}
