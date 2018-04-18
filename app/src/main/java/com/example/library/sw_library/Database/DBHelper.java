package com.example.library.sw_library.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.library.sw_library.Models.BookModel;
import com.example.library.sw_library.Network.GoogleApiRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
        context.deleteDatabase("Library.db");

        db.execSQL("create table Category (id integer primary key AUTOINCREMENT, name text);" );

        db.execSQL("create table Book (id integer primary key, title     text, authors text, category_id integer," +
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

        db.execSQL("insert into Category (name) values('Math')");/*,('Drama'),('Romance')," +
                "('Travel'),('Children'),('Religion'),('Science'),('History'),('Comedy')," +
                "('Tragedy'),('Adventure'),('cook'),('Art'),('Poetry'),('Health')");*/

    }
    public Map<Integer,String> getCategories(){

        SQLiteDatabase db = this.getReadableDatabase();
        Map<Integer,String> categories = new HashMap<Integer,String>();
        Cursor resultSet = db.rawQuery("Select * from Category",null);

        if (resultSet != null) {
            if (resultSet.moveToFirst()) {
                do {
                    String category = resultSet.getString(resultSet.getColumnIndex("name"));
                    int id = resultSet.getInt(resultSet.getColumnIndex("id"));
                    categories.put(id,category);
                    Log.e("Category:::::: ",category);
                } while (resultSet.moveToNext());
            }
        }
        else
            Log.e("Category:::::: ","No category");



        return categories ;
    }


    public void fillBooksFromAPI() throws JSONException {

        Map <Integer,String> categories =getCategories();
        Iterator<Integer> itr = categories.keySet().iterator();
        while(itr.hasNext()){
            final int catId = itr.next();
            String catName =categories.get(catId);
            new GoogleApiRequest(catName ,new GoogleApiRequest.AsyncResponse(){
                @Override
                public void processFinish(JSONObject output) throws JSONException {
                    JSONObject responseJson=output ;

                    Log.e("Network", "Network:: fillBooksFromAPI: "+responseJson );
                    JSONArray booksArray = responseJson.getJSONArray("items");
                    SQLiteDatabase db =DBHelper.super.getWritableDatabase();
                    for (int i=0 ; i<booksArray.length();i++){
                        JSONObject book =(JSONObject) booksArray.get(i);
                        JSONObject volumeInfo=book.getJSONObject("volumeInfo");
                        String title = volumeInfo.getString("title");
                        JSONArray authorsArray = volumeInfo.getJSONArray("authors");
                        Log.e("aaa", "processFinish: "+title );
                        String authors = "" ;
                        for (int k=0 ; k<authorsArray.length();k++){
                            String author = (String) authorsArray.get(i);
                            author.replaceAll(","," ");
                            authors+= author;
                            if (k!= authorsArray.length()-1)
                                authors += ',';
                        }
                        String changedAuthors = authors.replace("'","''");
                        String changedTitle = title.replace("'","''");

                        db.execSQL( "insert into Book values (null,\'"+changedTitle+"\',\'" +changedAuthors+ "\',"+catId+");");

                    }

                }

            }).execute();
        }

    }

    public List<BookModel> getBooks(int categoryID , String categoryName){
        SQLiteDatabase db = this.getReadableDatabase();
        List<BookModel>books = new ArrayList<BookModel>();

        Cursor resultSet = db.rawQuery("Select * from Book where category_id="+categoryID,null);

        if (resultSet != null) {
            if (resultSet.moveToFirst()) {
                do {

                    int bookId = resultSet.getInt(resultSet.getColumnIndex("id"));
                    String bookTitle = resultSet.getString(resultSet.getColumnIndex("title"));
                    String authorsString = resultSet.getString(resultSet.getColumnIndex("authors"));
                    String []authors = authorsString.split(",");
                    BookModel book = new BookModel(bookId,bookTitle,authors,categoryName);
                    Log.e("books", "getBooks: "+bookTitle);
                    books.add(book);
                } while (resultSet.moveToNext());
            }
        }

        return books;
    }
}
