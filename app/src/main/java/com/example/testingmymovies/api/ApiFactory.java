package com.example.testingmymovies.api;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiFactory {

    private static ApiFactory apiFactory;
    private static Retrofit retrofit;
    private static Retrofit retrofitVideos;
    private static Retrofit retrofitReviews;
    private static final String BASE_URL = "https://api.themoviedb.org/3/discover/";
    private static final String BASE_URL_VIDEOS = "https://api.themoviedb.org/3/movie/";
    private static final String BASE_URL_REVIEWS = "https://api.themoviedb.org/3/movie/";

    private ApiFactory() {
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        retrofitVideos = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_URL_VIDEOS)
                .build();

        retrofitReviews = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_URL_REVIEWS)
                .build();
    }

    public static ApiFactory getInstance() {
        if (apiFactory == null) {
            apiFactory = new ApiFactory();
        }
        return apiFactory;
    }

    public ApiService getApiServise () { return retrofit.create(ApiService.class); }

    public ApiServiceVideo getApiServiseVideo () { return retrofitVideos.create(ApiServiceVideo.class); }

    public ApiServiceReviews getApiServiseReviews () { return retrofitReviews.create(ApiServiceReviews.class); }

}
