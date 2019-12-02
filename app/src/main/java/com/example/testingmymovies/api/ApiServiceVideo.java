package com.example.testingmymovies.api;

import com.example.testingmymovies.pojo.Trailer;
import com.example.testingmymovies.pojo.TrailersResult;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiServiceVideo {

    @GET ("{id}/videos?api_key=978314745d3ce652ac32e226b079bf48")
    Observable<TrailersResult> getTrailers (@Path ("id") int id, @Query("language") String language);
}
