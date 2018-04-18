
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

    private static  DBHelper instance = null;

    private DBHelper(Context context){
        super(context,"Library",null,1);
        Log.e("DBhelper", "created!!: " );

    }

    public static DBHelper getInstance(Context context){
        if (instance==null)
            instance=new DBHelper(context);
        return instance ;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table Category (id integer primary key AUTOINCREMENT, name text);" );
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

        db.execSQL("insert into Category (name) values('Math'),('Drama'),('Romance')," +
                "('Travel'),('Children'),('Religion'),('Science'),('History'),('Comedy')," +
                "('Tragedy'),('Adventure'),('cook'),('Art'),('Poetry'),('Health')");

    }
    public void initialFillBooks()  {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("insert into Book values (null,'Childcraft','null',1);");
        db.execSQL("insert into Book values (null,'Christmast','Holly Berry-Byrd',1);");
        db.execSQL("insert into Book values (null,'A Common Stage','Carol Symes',2);");
        db.execSQL("insert into Book values (null,'Macbeth','William Shakespeare',2);");
        db.execSQL("insert into Book values (null,'Man and Superman','George Bernard Shaw',2);");
        db.execSQL("insert into Book values (null,'Fawcetta','Dalian Artanian',3);");
        db.execSQL("insert into Book values (null,'Dare to Love','Carly Phillips',3);");
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

        //get all categories
        Map <Integer,String> categories =getCategories();
        /*for each category : access google API to get books for it*/
        Iterator<Integer> itr = categories.keySet().iterator();
        while(itr.hasNext()){
            final int catId = itr.next();
            String catName =categories.get(catId);
            String apiUrlString = "https://www.googleapis.com/books/v1/volumes?q=subject:"+catName;
            new GoogleApiRequest(apiUrlString ,new GoogleApiRequest.AsyncResponse(){
                @Override
                public void processFinish(JSONObject output) throws JSONException {
                    JSONObject responseJson=output ;

                    Log.e("Network", "Network:: fillBooksFromAPI: "+responseJson );
                    if (responseJson.has("items")) {
                        Log.e("fillBooksFromAPI", "fillBooksFromAPI: items"+  responseJson.get("items"));
                        JSONArray booksArray = responseJson.getJSONArray("items");
                        SQLiteDatabase db = DBHelper.super.getWritableDatabase();
                        Log.e("fillBooksFromAPI", "fillBooksFromAPI: arraylen"+booksArray.length() );
                        for (int i = 0; i < booksArray.length(); i++) {
                            JSONObject book = (JSONObject) booksArray.get(i);
                            if (book.has("volumeInfo")) {

                                String title ="",author="",changedAuthors="",changedTitle="";
                                JSONObject volumeInfo = book.getJSONObject("volumeInfo");
                                if (volumeInfo.has("title")) {
                                    title = volumeInfo.getString("title");
                                    changedTitle = title.replace("'", "''");
                                    Log.e("fillBooksFromAPI", "fillBooksFromAPI: title "+title );
                                }
                                if (volumeInfo.has("authors")) {
                                    JSONArray authorsArray = volumeInfo.getJSONArray("authors");
                                    Log.e("aaa", "processFinish: " + title);
                                    String authors = "";
                                    for (int k = 0; k < authorsArray.length(); k++) {
                                        author = (String) authorsArray.get(i);
                                        author.replaceAll(",", " ");
                                        authors += author;
                                        if (k != authorsArray.length() - 1)
                                            authors += ',';
                                    }
                                    changedAuthors = authors.replace("'", "''");

                                }
                                Log.e("fillBooksFromAPI", "fillBooksFromAPI: insert "+changedTitle );
                                db.execSQL("insert into Book values (null,\'" + changedTitle + "\',\'" + changedAuthors + "\'," + catId + ");");
                            }

                        }
                    }

                }

            }).execute();
        }

    }

    public void getBook() {
        Log.d("BookView", "onCreateBook: christmast" );
        String apiUrlString="https://www.googleapis.com/books/v1/volumes?q=intitle:Childcraft&filter:free-ebooks&printType:books";
         new GoogleApiRequest(apiUrlString ,new GoogleApiRequest.AsyncResponse(){
            @Override
            public void processFinish(JSONObject output) throws JSONException {
                Log.d(this.toString(), "BookResponse: "+output);
            }}).execute();

    }

    public List<BookModel> getBooks(int categoryID , String categoryName){
        SQLiteDatabase db = this.getReadableDatabase();
        List<BookModel>books = new ArrayList<BookModel>();
        Cursor resultSet = db.rawQuery("Select * from Book where category_id="+categoryID,null);
        if (resultSet != null) {
            if (resultSet.moveToFirst()) {
                do {

                    int bookId = resultSet.getInt(resultSet.getColumnIndex("id"));
                    //Log.e("books", "getBooks: id "+bookId);
                    String bookTitle = resultSet.getString(resultSet.getColumnIndex("title"));
                    String authorsString = resultSet.getString(resultSet.getColumnIndex("authors"));
                  //  Log.e("books", "getBooks: author "+authorsString);
                    String []authors = new String[10];
                    if (authorsString!=null)
                         authors = authorsString.split(",");
                    BookModel book = new BookModel(bookId,bookTitle,authors,categoryName);
                    Log.e("books", "getBooks: "+bookTitle);
                    books.add(book);
                } while (resultSet.moveToNext());
            }
        }
        else
            Log.e("books", "getBooks: no books");


        return books;
    }
}
