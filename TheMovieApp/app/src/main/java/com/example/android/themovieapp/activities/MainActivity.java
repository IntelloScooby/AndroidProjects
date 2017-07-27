package com.example.android.themovieapp.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.themovieapp.MovieListAdapter;
import com.example.android.themovieapp.R;
import com.example.android.themovieapp.data.MovieInfo;
import com.example.android.themovieapp.utilities.JSONUtils;
import com.example.android.themovieapp.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieListAdapter.MovieClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private MovieListAdapter movieListAdapter;
    private RecyclerView movieImageRecyclerView;
    private GridLayoutManager layoutManager;
    private TextView errorMessage;
    private ProgressBar progressBar;

    private final JSONUtils jsonUtils = new JSONUtils();
    private final NetworkUtils networkUtils = new NetworkUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUiComponents();
        setupRecyclerView();
        makePopularMovieInfoFetchCall();
    }

    private void setupUiComponents() {
        errorMessage = (TextView) findViewById(R.id.tv_error_message_display);
        progressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
    }

    private void setupRecyclerView() {
        movieImageRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_image);

        layoutManager = new GridLayoutManager(this, 2);
        movieImageRecyclerView.setLayoutManager(layoutManager);
        movieImageRecyclerView.setHasFixedSize(true);

        movieListAdapter = new MovieListAdapter(this);
        movieImageRecyclerView.setAdapter(movieListAdapter);
    }

    private void makePopularMovieInfoFetchCall() {

        URL popularMoviesUrl = networkUtils.buildPopularMoviesUrl();
        MoviesListFetchTask moviesListFetchTask = new MoviesListFetchTask();
        moviesListFetchTask.execute(popularMoviesUrl);
    }

    private void makeTopRatedMovieInfoFetchCall() {

        URL topRatedMoviesUrl = networkUtils.buildTopRatedMoviesUrl();
        MoviesListFetchTask moviesListFetchTask = new MoviesListFetchTask();
        moviesListFetchTask.execute(topRatedMoviesUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        setTitle(R.string.popular_movies);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemSelected = item.getItemId();

        if (itemSelected == R.id.action_sort_popular) {
            makePopularMovieInfoFetchCall();
            setTitle(R.string.popular_movies);
        }

        if (itemSelected == R.id.action_sort_top_rated) {
            makeTopRatedMovieInfoFetchCall();
            setTitle(R.string.top_rated_movies);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMovieClick(int movieClickedIndex) {
        String selectedMovieId = movieListAdapter.getSelectedMovieId(movieClickedIndex);
        presentMovieInfoActivity(selectedMovieId);
    }

    private void presentMovieInfoActivity(String selectedMovieId) {
        Intent movieInfoActivityIntent = new Intent(this, MovieInfoActivity.class);
        movieInfoActivityIntent.putExtra(MovieInfoActivity.EXTRA_MOVIE_ID, selectedMovieId);
        startActivity(movieInfoActivityIntent);
    }

    private class MoviesListFetchTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            movieImageRecyclerView.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(URL... urls) {

            String moviesInfoResult = null;

            try {
                moviesInfoResult = networkUtils.getResponseFromHttpUrl(urls[0]);
                Log.d(TAG, "moviesInfoResult received");
            } catch (IOException e) {
                Log.e(TAG, "IOException received in the background thread");
                e.printStackTrace();
            }
            return moviesInfoResult;
        }

        @Override
        protected void onPostExecute(String moviesInfoResult) {
            progressBar.setVisibility(View.INVISIBLE);

            if (moviesInfoResult != null && !moviesInfoResult.equals("")) {

                Log.d(TAG, "Displaying list of movies");

                layoutManager.scrollToPosition(0);
                showMovieListRecyclerView();
                ArrayList<MovieInfo> movieInfoArrayList = null;

                try {
                    movieInfoArrayList = jsonUtils.getMoviesList(moviesInfoResult);
                } catch (JSONException e) {
                    Log.e(TAG, "JSONException received when trying to get list of movies");
                    e.printStackTrace();
                }

                movieListAdapter.setMovieListData(movieInfoArrayList);

            } else {
                Log.d(TAG, "No movies returned. Displaying error message");
                showErrorMessage();
            }
        }

    }

    private void showMovieListRecyclerView() {
        movieImageRecyclerView.setVisibility(View.VISIBLE);
        errorMessage.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage() {
        errorMessage.setVisibility(View.VISIBLE);
        movieImageRecyclerView.setVisibility(View.INVISIBLE);
    }
}
