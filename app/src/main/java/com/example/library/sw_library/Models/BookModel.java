package com.example.library.sw_library.Models;

/*
model for book
 */

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

public class BookModel {

    private int bookID ;
    private String name, authorName;
    private int categoryID;

    public JSONObject getBookJSONObject(String bookTitle) throws BadRequestException {

        String apiUrlString = "https://www.googleapis.com/books/v1/volumes?q=intitle:" + bookTitle + "&filter:free-ebooks&printType:books";
        try{
            HttpURLConnection connection = null;
            // Build Connection.
            try{
                URL url = new URL(apiUrlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(5000); // 5 seconds
                connection.setConnectTimeout(5000); // 5 seconds
            } catch (MalformedURLException e) {
                // Impossible: The only two URLs used in the app are taken from string resources.
                e.printStackTrace();
            } catch (ProtocolException e) {
                // Impossible: "GET" is a perfectly valid request method.
                e.printStackTrace();
            }
            int responseCode = connection.getResponseCode();
            if(responseCode != 200){
                //System.out.println("GoogleBooksAPI request failed. Response Code: " + responseCode);
                //Log.w(getClass().getName(), "GoogleBooksAPI request failed. Response Code: " + responseCode);
                connection.disconnect();
                throw new BadRequestException("GoogleBooksAPI request failed. Response Code: " + responseCode);

//                return null;
            }

            // Read data from response.
            StringBuilder builder = new StringBuilder();
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = responseReader.readLine();
            while (line != null){
                builder.append(line);
                line = responseReader.readLine();
                System.out.println(line);
            }
            String responseString = builder.toString();
            //Log.d(getClass().getName(), "Response String: " + responseString);
            JSONObject responseJson = new JSONObject(responseString);
            // Close connection and return response code.
            connection.disconnect();
            return responseJson;
        } catch (SocketTimeoutException e) {
            //Log.w(getClass().getName(), "Connection timed out. Returning null");
            return null;
        } catch(IOException e){
            //Log.d(getClass().getName(), "IOException when connecting to Google Books API.");
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            //Log.d(getClass().getName(), "JSONException when connecting to Google Books API.");
            e.printStackTrace();
            return null;
        }
    }

    public class BadRequestException extends Exception {
        public BadRequestException(String msg) {
            super(msg);
        }
    }


}
