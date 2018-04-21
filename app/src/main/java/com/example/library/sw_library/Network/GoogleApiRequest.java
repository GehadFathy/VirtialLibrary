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

/*
class to integrate with google book API
*/
public class GoogleApiRequest extends AsyncTask<String, Object, JSONObject> {

    public interface AsyncResponse {
        void processFinish(JSONObject output) throws JSONException;
    }

    public AsyncResponse delegate = null;
    private String apiUrlString;

    public GoogleApiRequest(String apiUrlString,AsyncResponse delegate){
        this.apiUrlString=apiUrlString;
        this.delegate = delegate;

    }
    @Override
    protected JSONObject doInBackground(String... strings) {

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

            // Read data from response.
            StringBuilder builder = new StringBuilder();
            Log.e("Network", "Network:: after builder ");


            BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line = responseReader.readLine();
            Log.e("Network", "Network:: after line "+line);
            while (line != null){
                builder.append(line);
                line = responseReader.readLine();
            }
            Log.e("Network", "Network:: after xxx "+builder);

            responseJson = new JSONObject(builder.toString());
            // Close connection and return response code.
            connection.disconnect();
            Log.e("Network", "Network:: Response String: "+ responseJson );
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}