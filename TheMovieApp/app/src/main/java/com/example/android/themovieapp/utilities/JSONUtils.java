package com.example.android.themovieapp.utilities;

import android.util.Log;

import com.example.android.themovieapp.data.MovieInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.android.themovieapp.constants.JSONComponents.ID;
import static com.example.android.themovieapp.constants.JSONComponents.ORIGINAL_TITLE;
import static com.example.android.themovieapp.constants.JSONComponents.PLOT_SYNOPSIS;
import static com.example.android.themovieapp.constants.JSONComponents.POSTER_PATH;
import static com.example.android.themovieapp.constants.JSONComponents.RELEASE_DATE;
import static com.example.android.themovieapp.constants.JSONComponents.RESULTS;
import static com.example.android.themovieapp.constants.JSONComponents.USER_RATING;

public class JSONUtils {

    private static final String TAG = JSONUtils.class.getSimpleName();

    /**
     * This method will return the movie ID and poster path for the list of movies
     *
     * @param JSONString List of movies in JSON String format
     * @return ArrayList of the movie id and poster_path for each movie
     * @throws JSONException
     */
    @SuppressWarnings("JavaDoc")
    public ArrayList<MovieInfo> getMoviesList(String JSONString) throws JSONException {

        JSONObject moviesData = new JSONObject(JSONString);
        JSONArray moviesList = moviesData.getJSONArray(RESULTS);

        ArrayList<MovieInfo> movieInfoArrayList = new ArrayList<>();

        for (int i = 0; i < moviesList.length(); i++) {
            String movieId;
            String moviePosterPath;
            MovieInfo movieInfo = new MovieInfo();

            movieId = moviesList.getJSONObject(i).getString(ID);
            moviePosterPath = moviesList.getJSONObject(i).getString(POSTER_PATH);

            movieInfo.setId(movieId);
            movieInfo.setPosterPath(moviePosterPath);

            movieInfoArrayList.add(movieInfo);
        }

        Log.d(TAG, "Number of movies in result: " + movieInfoArrayList.size());

        return movieInfoArrayList;
    }

    /**
     * This method will return movie information for any given movie
     *
     * @param JSONString Movie details in JSON String format
     * @return Movie id, poster_path, original_title, overview, vote_average and release_date for the movie
     * @throws JSONException
     */

    @SuppressWarnings("JavaDoc")
    public MovieInfo getMovieInfo(String JSONString) throws JSONException {
        JSONObject movieInfoData = new JSONObject(JSONString);
        MovieInfo selectedMovieInfo = new MovieInfo();

        selectedMovieInfo.setId(movieInfoData.getString(ID));
        selectedMovieInfo.setPosterPath(movieInfoData.getString(POSTER_PATH));
        selectedMovieInfo.setOriginalTitle(movieInfoData.getString(ORIGINAL_TITLE));
        selectedMovieInfo.setPlotSynopsis(movieInfoData.getString(PLOT_SYNOPSIS));
        selectedMovieInfo.setUserRating(movieInfoData.getString(USER_RATING));
        selectedMovieInfo.setReleaseDate(movieInfoData.getString(RELEASE_DATE));

        Log.d(TAG, "MovieInfoData: " + movieInfoData);

        return selectedMovieInfo;
    }
}
