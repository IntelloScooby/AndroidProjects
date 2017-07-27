package com.example.android.themovieapp.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static com.example.android.themovieapp.constants.URLComponents.API_KEY;
import static com.example.android.themovieapp.constants.URLComponents.MOVIE_BASE_URL;
import static com.example.android.themovieapp.constants.URLComponents.MOVIE_POSTER_BASE_URL;
import static com.example.android.themovieapp.constants.URLComponents.POPULAR_MOVIES_PATH;
import static com.example.android.themovieapp.constants.URLComponents.POSTER_WIDTH;
import static com.example.android.themovieapp.constants.URLComponents.QUERY_PARAM;
import static com.example.android.themovieapp.constants.URLComponents.TOP_RATED_MOVIES_PATH;

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    /**
     * Builds the URL used to get the popular movies
     *
     * @return The URL to get popular movies
     */
    public URL buildPopularMoviesUrl() {
        Uri uri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(POPULAR_MOVIES_PATH)
                .appendQueryParameter(QUERY_PARAM, API_KEY).build();

        URL builtUrl = buildUrlFromUri(uri);

        Log.d(TAG, "Url built for fetching popular movies: " + builtUrl.toString());

        return builtUrl;
    }

    /**
     * Builds the URL used to get the top rated movies
     *
     * @return The URL to get top rated movies
     */
    public URL buildTopRatedMoviesUrl() {
        Uri uri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(TOP_RATED_MOVIES_PATH)
                .appendQueryParameter(QUERY_PARAM, API_KEY).build();

        URL builtUrl = buildUrlFromUri(uri);

        Log.d(TAG, "Url built for fetching top rated movies: " + builtUrl.toString());

        return builtUrl;
    }

    /**
     * Builds the URL used to get the image for each of the movies displayed in the movies list
     *
     * @param imagePath
     * @return The URL to use to get movie poster image
     */
    @SuppressWarnings("JavaDoc")
    public URL buildMovieImageUrl(String imagePath) {
        Log.d(TAG, "Image path: " + imagePath);

        Uri uri = Uri.parse(MOVIE_POSTER_BASE_URL).buildUpon()
                .appendPath(POSTER_WIDTH)
                .appendEncodedPath(imagePath)
                .build();

        URL builtUrl = buildUrlFromUri(uri);

        Log.d(TAG, "Url built for fetching movie image: " + builtUrl.toString());

        return builtUrl;
    }

    /**
     * Builds the Url for the selected movie by using the movieId
     *
     * @param movieId selected movie Id
     * @return The URL to fetch movie information
     */
    public URL buildMovieInfoUrl(String movieId) {
        Log.d(TAG, "Movie ID for fetching movie info: " + movieId);

        Uri uri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendQueryParameter(QUERY_PARAM, API_KEY).build();

        URL builtUrl = buildUrlFromUri(uri);

        Log.d(TAG, "Url built for fetching movie info: " + builtUrl.toString());

        return builtUrl;
    }

    /**
     * Build a Url from a Uri passed as parameter
     *
     * @param uri - The Uri that needs to be converted to Url
     * @return The Url built from the uri
     */
    private URL buildUrlFromUri(Uri uri) {
        URL builtUrl = null;

        try {
            builtUrl = new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Cannot build URL from URI. URI: " + uri.toString());
            e.printStackTrace();
        }
        return builtUrl;
    }


    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public String getResponseFromHttpUrl(URL url) throws IOException {
        Log.d(TAG, "URL requesting for HTTP response: " + url);

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                Log.d(TAG, "Sending the JSON response");
                return scanner.next();
            } else {
                Log.d(TAG, "JSON response is null");
                return null;
            }
        } finally {
            Log.d(TAG, "Disconnecting URL connection");
            urlConnection.disconnect();
        }
    }
}
