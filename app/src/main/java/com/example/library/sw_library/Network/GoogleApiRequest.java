package com.example.library.sw_library.Network;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;

public class GoogleApiRequest extends AsyncTask<String, Object, JSONObject> {

    public interface AsyncResponse {
        void processFinish(JSONObject output) throws JSONException;
    }

    public AsyncResponse delegate = null;
    private String category;

    public GoogleApiRequest(String category,AsyncResponse delegate){
        this.category=category;
        this.delegate = delegate;

    }
    @Override
    protected JSONObject doInBackground(String... strings) {

        String apiUrlString = "https://www.googleapis.com/books/v1/volumes?q=subject:"+category;
        Log.e("Network", "Network:: in start: "+apiUrlString);

        JSONObject responseJson = new JSONObject();

        try{
            HttpURLConnection connection = null;
            // Build Connection.
            try{
                URL url = new URL(apiUrlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(5000); // 5 seconds
                connection.setConnectTimeout(5000); // 5 seconds
                connection.connect();
                Log.e("Network", "Network:: after connection: ");

            } catch (MalformedURLException e) {
                // Impossible: The only two URLs used in the app are taken from string resources.
                e.printStackTrace();
            } catch (ProtocolException e) {
                // Impossible: "GET" is a perfectly valid request method.
                e.printStackTrace();
            }
            int responseCode = connection.getResponseCode();
            Log.e("Network", "Network:: code "+responseCode);

            if(responseCode != 200){
                Log.w(getClass().getName(), "GoogleBooksAPI request failed. Response Code: " + responseCode);
                connection.disconnect();
                return null;
            }

            // Read data from response.
            StringBuilder builder = new StringBuilder();
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = responseReader.readLine();
            while (line != null){
                builder.append(line);
                line = responseReader.readLine();
            }
            String responseString = builder.toString();
            Log.d(getClass().getName(), "Network:: Response String: " + responseString);
            responseJson = new JSONObject(responseString);
            // Close connection and return response code.
            connection.disconnect();
            return responseJson;
        } catch (SocketTimeoutException e) {
            Log.w(getClass().getName(), "Connection timed out. Returning null");
            return null;
        } catch(IOException e){
            Log.d(getClass().getName(), "IOException when connecting to Google Books API.");
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            Log.d(getClass().getName(), "JSONException when connecting to Google Books API.");
            e.printStackTrace();
            return null;
        }

    }
        @Override
    protected void onPostExecute(JSONObject jsonObject) {
            try {
                delegate.processFinish(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

}
