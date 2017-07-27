package com.example.android.themovieapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.themovieapp.R;
import com.example.android.themovieapp.data.MovieInfo;
import com.example.android.themovieapp.utilities.JSONUtils;
import com.example.android.themovieapp.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MovieInfoActivity extends AppCompatActivity {

    private static final String TAG = MovieInfoActivity.class.getSimpleName();

    public static final String EXTRA_MOVIE_ID = "movie_id";

    private ImageView poster;
    private TextView originalTitle;
    private TextView plotSynopsis;
    private TextView userRating;
    private TextView releaseDate;
    private TextView errorMessage;
    private ProgressBar progressBar;

    private final JSONUtils jsonUtils = new JSONUtils();
    private final NetworkUtils networkUtils = new NetworkUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);
        Intent movieInfoIntent = getIntent();
        String movieId = movieInfoIntent.getStringExtra(EXTRA_MOVIE_ID);

        setTitle(R.string.movie_info);

        setupUiComponents();
        makeMovieInfoFetchCall(movieId);
    }

    private void setupUiComponents() {
        poster = (ImageView) findViewById(R.id.movie_poster);
        originalTitle = (TextView) findViewById(R.id.original_title);
        plotSynopsis = (TextView) findViewById(R.id.plot_synopsis);
        userRating = (TextView) findViewById(R.id.user_rating);
        releaseDate = (TextView) findViewById(R.id.release_date);
        errorMessage = (TextView) findViewById(R.id.tv_error_message_display);
        progressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
    }

    private void makeMovieInfoFetchCall(String movieId) {
        URL selectedMovieInfo = networkUtils.buildMovieInfoUrl(movieId);
        MovieInfoFetchTask movieInfoFetchTask = new MovieInfoFetchTask();
        movieInfoFetchTask.execute(selectedMovieInfo);
    }

    private class MovieInfoFetchTask extends AsyncTask<URL, Void, String> {

        MovieInfo movieInfo = null;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... urls) {

            String movieInfo = null;

            try {
                movieInfo = networkUtils.getResponseFromHttpUrl(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return movieInfo;
        }

        @Override
        protected void onPostExecute(String movieInfoResult) {
            progressBar.setVisibility(View.INVISIBLE);

            if (movieInfoResult != null && !movieInfoResult.equals("")) {
                Log.d(TAG, "Movie Info result received. Hiding Progress bar");

                try {
                    Log.d(TAG, "Trying to get movie info from the JSON output");
                    movieInfo = jsonUtils.getMovieInfo(movieInfoResult);
                } catch (JSONException e) {
                    Log.e(TAG, "Error while reading JSON output");
                    e.printStackTrace();
                }

                loadPosterImage();
                displayOriginalTitle();
                displayPlotSynopsis();
                displayUserRating();
                displayReleaseDate();

            } else {
                Log.d(TAG, "JSON output is empty - Showing error message");
                errorMessage.setVisibility(View.VISIBLE);
            }
        }

        private void displayReleaseDate() {
            String releaseDateText = movieInfo.getReleaseDate();

            if (releaseDateText != null && !releaseDateText.equals("")) {
                findViewById(R.id.release_date_label).setVisibility(View.VISIBLE);
                releaseDate.setVisibility(View.VISIBLE);
                releaseDate.setText(movieInfo.getReleaseDate());
            }
        }

        private void displayUserRating() {
            String userRatingText = movieInfo.getUserRating();

            if (userRatingText != null && !userRatingText.equals("")) {
                findViewById(R.id.user_rating_label).setVisibility(View.VISIBLE);
                userRating.setVisibility(View.VISIBLE);
                userRating.setText(movieInfo.getUserRating());
            }
        }

        private void displayPlotSynopsis() {
            String plotSynopsisText = movieInfo.getPlotSynopsis();

            if (plotSynopsisText != null && !plotSynopsisText.equals("")) {
                findViewById(R.id.plot_synopsis_label).setVisibility(View.VISIBLE);
                plotSynopsis.setVisibility(View.VISIBLE);
                plotSynopsis.setText(movieInfo.getPlotSynopsis());
            }
        }

        private void displayOriginalTitle() {
            String originalTitleText = movieInfo.getOriginalTitle();

            if (originalTitleText != null && !originalTitleText.equals("")) {
                findViewById(R.id.original_title_label).setVisibility(View.VISIBLE);
                originalTitle.setVisibility(View.VISIBLE);
                originalTitle.setText(movieInfo.getOriginalTitle());
            }
        }

        private void loadPosterImage() {
            Context context = getApplicationContext();

            if (movieInfo.getPosterPath() != null && !movieInfo.getPosterPath().equals("")) {
                poster.setVisibility(View.VISIBLE);
                URL imageURL = networkUtils.buildMovieImageUrl(movieInfo.getPosterPath());
                Log.d(TAG, "Loading image from URL: " + imageURL.toString());

                Picasso.with(context)
                        .load(imageURL.toString())
                        .into(poster);
            }
        }
    }
}
