package com.example.library.sw_library.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
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
class to manage data base
 */

public class DBHelper extends SQLiteOpenHelper {


    public Context context;
    public DBHelper(Context context){
        super(context,"Library",null,1);
        this.context=context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table Category (id integer primary key AUTOINCREMENT, name text);" );

        /*db.execSQL("create table Shelf (id integer primary key, category_id integer ,num_of_books integer," +
                " foreign key (category_id) references Category(id));");*/
        db.execSQL("create table Book (id integer primary key, title text, authors text, category_id integer," +
                " foreign key (category_id) references Category(id)); ");
        db.execSQL("create table Admin (id integer primary key, name text,email text,pwd text);" );



    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS Category " );
        db.execSQL("DROP TABLE IF EXISTS Book " );
        onCreate(db);

    }

    public void fillCategory(){
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("insert into Category (name) values ('fiction'),('Math'),('Drama'),('Romance')," +
                "('Travel'),('Children'),('Religion'),('Science'),('History'),('Comedy')," +
                "('Tragedy'),('Adventure'),('cook'),('Art'),('Poetry'),('Health')");
        /*db.execSQL("insert into Book (title,author,category_id) values ('gehadbook', 'mennaselim',1)");
        db.execSQL("insert into Book (title,author,category_id) values ('gehadbook2', 'mennaselim2',1)");*/

    }
    public void fillBooksFromAPI(){
        SQLiteDatabase db = this.getWritableDatabase();
        String apiUrlString = "https://www.googleapis.com/books/v1/volumes?q=subject:" ;

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
                Log.w(getClass().getName(), "GoogleBooksAPI request failed. Response Code: " + responseCode);
                connection.disconnect();
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
            Log.d(getClass().getName(), "Response String: " + responseString);
            JSONObject responseJson = new JSONObject(responseString);
            JSONArray booksArray = responseJson.getJSONArray("items");
            for (int i=0 ; i<booksArray.length();i++){
                JSONObject book =(JSONObject) booksArray.get(i);
                JSONObject volumeInfo=book.getJSONObject("volumeInfo");
                String title = volumeInfo.getString("title");
                JSONArray authorsArray = volumeInfo.getJSONArray("authors");
                String authors = "" ;
                for (int k=0 ; k<authorsArray.length();k++){
                    authors+=authorsArray.get(i);
                }
                db.execSQL("insert into Book (title,author,category_id) values ("+title+"," +authors+"'*',1)");
                Log.e("BOOKS","insert into Book (title,author,category_id) values ("+title+"," +authors+"'*',1)");

            }
            // Close connection and return response code.
            connection.disconnect();
        } catch (SocketTimeoutException e) {
            Log.w(getClass().getName(), "Connection timed out. Returning null");
        } catch(IOException e){
            Log.d(getClass().getName(), "IOException when connecting to Google Books API.");
            e.printStackTrace();

        } catch (JSONException e) {
            Log.d(getClass().getName(), "JSONException when connecting to Google Books API.");
            e.printStackTrace();
        }

    }

    public /*List<BookModel>*/ void getBooks(int categoryID){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor resultSet = db.rawQuery("Select * from Book where category_id=1",null);

        if (resultSet != null) {
            if (resultSet.moveToFirst()) {
                do {
                    String book1 = resultSet.getString(resultSet.getColumnIndex("name"));
                    Toast.makeText(context,book1 , Toast.LENGTH_SHORT).show();

                } while (resultSet.moveToNext());
            }
        }
       /* Log.e("TAG","********************");
        Log.e("TAG","Book: "+ book1);

        System.out.println(book1);*/


    }
}
