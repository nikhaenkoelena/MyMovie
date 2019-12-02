package com.example.testingmymovies.api;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiFactory {

    private static ApiFactory apiFactory;
    private static Retrofit retrofit;
    private static Retrofit retrofitVideos;
    private static Retrofit retrofitReviews;
    private static final String BASE_URL = "https://api.themoviedb.org/3/";


    private ApiFactory() {
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();

    }

    public static ApiFactory getInstance() {
        if (apiFactory == null) {
            apiFactory = new ApiFactory();
        }
        return apiFactory;
    }

    public ApiService getApiServise () { return retrofit.create(ApiService.class); }

    public ApiServiceVideo getApiServiseVideo () { return retrofit.create(ApiServiceVideo.class); }

    public ApiServiceReviews getApiServiseReviews () { return retrofit.create(ApiServiceReviews.class); }

}
