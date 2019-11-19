package com.example.testingmymovies.pojo;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.testingmymovies.converters.Converter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = "moviestable")
@TypeConverters(value = Converter.class)
public class Movie {
    @PrimaryKey (autoGenerate = true)
    @SerializedName("uniqId")
    @Expose
    private int uniqId;
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("vote_count")
    @Expose
    private int vote_count;
    @SerializedName("vote_average")
    @Expose
    private double vote_average;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("original_title")
    @Expose
    private String original_title;
    @SerializedName("poster_path_big")
    @Expose
    private String poster_path_big;
    @SerializedName("poster_path")
    @Expose
    private String poster_path_small;
    @SerializedName("backdrop_path")
    @Expose
    private String backdrop_path;
    @SerializedName("overview")
    @Expose
    private String overview;
    @SerializedName("release_date")
    @Expose
    private String release_date;
    @SerializedName("trailers")
    @Expose
    private List<Trailer> trailers = null;


    @Ignore
    public Movie(int uniqId, int id, int vote_count, double vote_average, String title, String original_title, String poster_path_small, String poster_path_big, String backdrop_path, String overview, String release_date) {
        this.uniqId = uniqId;
        this.id = id;
        this.vote_count = vote_count;
        this.vote_average = vote_average;
        this.title = title;
        this.original_title = original_title;
        this.poster_path_big = poster_path_big;
        this.poster_path_small = poster_path_small;
        this.backdrop_path = backdrop_path;
        this.overview = overview;
        this.release_date = release_date;
    }

    @Ignore
    public Movie(int id, int vote_count, double vote_average, String title, String original_title, String poster_path_small, String poster_path_big, String backdrop_path, String overview, String release_date) {
        this.id = id;
        this.vote_count = vote_count;
        this.vote_average = vote_average;
        this.title = title;
        this.original_title = original_title;
        this.poster_path_big = poster_path_big;
        this.poster_path_small = poster_path_small;
        this.backdrop_path = backdrop_path;
        this.overview = overview;
        this.release_date = release_date;
    }

    public Movie(int uniqId, int id, int vote_count, double vote_average, String title, String original_title, String poster_path_big, String poster_path_small, String backdrop_path, String overview, String release_date, List<Trailer> trailers) {
        this.uniqId = uniqId;
        this.id = id;
        this.vote_count = vote_count;
        this.vote_average = vote_average;
        this.title = title;
        this.original_title = original_title;
        this.poster_path_big = poster_path_big;
        this.poster_path_small = poster_path_small;
        this.backdrop_path = backdrop_path;
        this.overview = overview;
        this.release_date = release_date;
        this.trailers = trailers;
    }

    public int getUniqId() {
        return uniqId;
    }

    public void setUniqId(int uniqId) {
        this.uniqId = uniqId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVote_count() {
        return vote_count;
    }

    public void setVote_count(int vote_count) {
        this.vote_count = vote_count;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getPoster_path_small() {
        return poster_path_small;
    }

    public void setPoster_path_small(String poster_path_small) {
        this.poster_path_small = poster_path_small;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getPoster_path_big() {
        return poster_path_big;
    }

    public void setPoster_path_big(String poster_path_big) {
        this.poster_path_big = poster_path_big;
    }

    public List<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }
}
