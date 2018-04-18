package com.example.library.sw_library.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

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

        db.execSQL("insert into Category (name) values ('fiction'),('Math'),('Drama'),('Romance')," +
                "('Travel'),('Children'),('Religion'),('Science'),('History'),('Comedy')," +
                "('Tragedy'),('Adventure'),('cook'),('Art'),('Poetry'),('Health')");

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
                            authors+=authorsArray.get(i);
                        }
                        String changedAuthors = authors.replace("'","''");
                        String changedTitle = title.replace("'","''");

                        db.execSQL( "insert into Book values (null,\'"+title+"\',\'" +changedAuthors+ "\',"+catId+");");

                    }

                }

            }).execute();
        }

    }

    //todo
    public void getBooks(int categoryID){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor resultSet = db.rawQuery("Select * from Book where category_id="+categoryID,null);

        if (resultSet != null) {
            if (resultSet.moveToFirst()) {
                do {
                    String book1 = resultSet.getString(resultSet.getColumnIndex("name"));
                    Log.e("books", "getBooks: "+book1 );
                } while (resultSet.moveToNext());
            }
        }


    }
}
