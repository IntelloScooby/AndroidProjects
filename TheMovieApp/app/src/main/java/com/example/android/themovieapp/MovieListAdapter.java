package com.example.android.themovieapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.themovieapp.data.MovieInfo;
import com.example.android.themovieapp.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieListViewHolder> {

    private static final String TAG = MovieListAdapter.class.getSimpleName();

    private int numberOfMovies;
    private ArrayList<MovieInfo> movieInfo;
    private final MovieClickListener movieClickListener;
    private final NetworkUtils networkUtils = new NetworkUtils();

    public interface MovieClickListener {
        void onMovieClick(int movieClickedIndex);
    }

    public MovieListAdapter(MovieClickListener movieClickListener) {
        this.movieClickListener = movieClickListener;
    }

    @Override
    public MovieListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutIDForListItem = R.layout.movie_list;
        boolean shouldAttachToParentImmediately = false;

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        @SuppressWarnings("ConstantConditions") View view = layoutInflater.inflate(layoutIDForListItem, parent, shouldAttachToParentImmediately);

        return new MovieListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieListViewHolder holder, int position) {
        URL imageURL = networkUtils.buildMovieImageUrl(movieInfo.get(position).getPosterPath());
        Context context = holder.movieImage.getContext();
        Log.d(TAG, "Loading image from URL: " + imageURL.toString());
        Picasso.with(context)
                .load(imageURL.toString())
                .into(holder.movieImage);
    }

    @Override
    public int getItemCount() {
        return numberOfMovies;
    }

    public class MovieListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView movieImage;

        public MovieListViewHolder(View itemView) {
            super(itemView);
            movieImage = (ImageView) itemView.findViewById(R.id.movie_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int itemPosition = getAdapterPosition();
            movieClickListener.onMovieClick(itemPosition);
        }
    }

    public void setMovieListData(ArrayList<MovieInfo> movieListData) {
        movieInfo = movieListData;
        setNumberOfMovies(movieInfo.size());
        notifyDataSetChanged();
    }

    private void setNumberOfMovies(int numberOfMovies) {
        this.numberOfMovies = numberOfMovies;
    }

    public String getSelectedMovieId(int selectedMovieIndex) {
        return movieInfo.get(selectedMovieIndex).getId();
    }

}
