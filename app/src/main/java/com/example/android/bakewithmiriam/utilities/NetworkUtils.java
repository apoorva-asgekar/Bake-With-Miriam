package com.example.android.bakewithmiriam.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import static android.R.attr.bitmap;
import static android.R.attr.src;

/**
 * Created by apoorva on 7/20/17.
 */

/**
 * Utility class containing all methods which interact with the network connection.
 */
public class NetworkUtils {

    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();
    private static Bitmap returnedImage;
    private static String returnedContentType;

    /**
     * Connects to the Baking Url and gets the response to the Http request.
     *
     * @param requestUrl - URL for the API request
     * @return String which contains the API response
     * @throws IOException
     */
    public static String getResponseFromUrl(URL requestUrl) throws IOException {
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        String resultJson = null;

        try {
            if (requestUrl != null) {
                urlConnection = (HttpURLConnection) requestUrl.openConnection();
            }
            if (urlConnection != null) {
                inputStream = urlConnection.getInputStream();
                if (inputStream != null) {
                    Scanner scanner = new Scanner(inputStream);
                    scanner.useDelimiter("\\A");
                    if (scanner.hasNext()) {
                        resultJson = scanner.next();
                    }
                }
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    return resultJson;
                } else {
                    Log.e(LOG_TAG, "Httprequest returned with an error code: "
                            + urlConnection.getResponseCode());
                }
            }
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    public static Bitmap getBitmapFromURL(String src) {
        String[] params = new String[]{src};
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                try {
                    URL url = new URL(params[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                    return myBitmap;
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error while getting Bitmap from the thumbnail.", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                runOnPostExecute(bitmap);
            }
        }.execute(params);
        return returnedImage;
    }

    private static void runOnPostExecute(Bitmap bitmap) {
        returnedImage = bitmap;
    }

    private static void runOnPostExecute(String contentType) {
        returnedContentType = contentType;
    }

    public static String getUrlContentType(String url) {
        String[] params = new String[]{url};
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    URL url = new URL(params[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    String contentType = connection.getHeaderField("Content-Type");
                    return contentType;
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error while getting Url Content Type", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String contentType) {
                super.onPostExecute(contentType);
                runOnPostExecute(contentType);
            }
        }.execute(params);
        return returnedContentType;
    }

    //Check if internet connection is currently available.
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

}
