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

    /*book attributes in the DB*/
    private int bookID ;
    private String name;
    private String []authors;
    private String category;

    /*constructor*/
    public BookModel(int bookID, String name, String []authors, String category) {
        this.bookID = bookID;
        this.name = name;
        this.authors = authors;
        this.category = category;
    }
    public BookModel(){}

    /*get the book ID*/
    public int getBookID() {
        return bookID;
    }

    /*set the book ID*/
    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    /*get the book name*/
    public String getName() {
        return name;
    }

    /*set the book name*/
    public void setName(String name) {
        this.name = name;
    }

    /*get the book authors*/
    public String[] getAuthors() {
        return authors;
    }

    /*set the book authors*/
    public void setAuthors(String[] authors) {
        this.authors = authors;
    }

    /*get the book category*/
    public String getCategory() {
        return category;
    }

    /*set the book category*/
    public void setCategory(String category) {
        this.category = category;
    }

    /*get the book info. from the API*/
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
                connection.disconnect();
                throw new BadRequestException("GoogleBooksAPI request failed. Response Code: " + responseCode);

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
            JSONObject responseJson = new JSONObject(responseString);
            // Close connection and return response code.
            connection.disconnect();
            return responseJson;
        } catch (SocketTimeoutException e) {
            return null;
        } catch(IOException e){
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
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