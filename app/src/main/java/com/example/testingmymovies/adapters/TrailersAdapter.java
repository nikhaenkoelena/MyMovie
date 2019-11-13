package com.example.testingmymovies.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testingmymovies.R;
import com.example.testingmymovies.pojo.Trailer;

import java.util.ArrayList;
import java.util.List;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersViewHolder> {

    List<Trailer> trailers;

    public TrailersAdapter() {
        trailers = new ArrayList<>();
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TrailersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item, parent, false);
        return new TrailersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailersViewHolder holder, int position) {
        Trailer trailer = trailers.get(position);
        holder.trailersTitle.setText(trailer.getName());
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    public class TrailersViewHolder extends RecyclerView.ViewHolder {

        private TextView trailersTitle;

        public TrailersViewHolder(@NonNull View itemView) {
            super(itemView);
            trailersTitle = itemView.findViewById(R.id.textViewTrailersTitle);
        }
    }
}