package com.example.testingmymovies.converters;

import androidx.room.TypeConverter;

import com.example.testingmymovies.pojo.Trailer;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.converter.gson.GsonConverterFactory;

public class Converter {

    @TypeConverter
    public String getTrailersToString (List<Trailer> trailers) {
        return new Gson().toJson(trailers);
    }

    @TypeConverter
    public List<Trailer> getTrailersFromString (String trailersAsString) {
        Gson gson = new Gson();
        ArrayList objects = gson.fromJson(trailersAsString, ArrayList.class);
        ArrayList<Trailer> trailers = new ArrayList<>();
        for (Object o : objects) {
            trailers.add(gson.fromJson(o.toString(), Trailer.class));
        }
        return trailers;
    }
}
