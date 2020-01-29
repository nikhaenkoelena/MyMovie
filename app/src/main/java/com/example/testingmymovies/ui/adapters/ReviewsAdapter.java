package com.example.testingmymovies.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testingmymovies.R;
import com.example.testingmymovies.repository.pojo.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private List<Review> reviews;

    public ReviewsAdapter() {
        reviews = new ArrayList<>();
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.textViewReviewAuthor.setText(review.getAuthor());
        holder.textViewReviewText.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewReviewAuthor;
        private TextView textViewReviewText;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewReviewAuthor = itemView.findViewById(R.id.textViewReviewAuthor);
            textViewReviewText = itemView.findViewById(R.id.textViewReviewText);
        }
    }
}
