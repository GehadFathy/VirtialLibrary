package com.example.library.sw_library.Database;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.library.sw_library.Network.GoogleApiRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/*
class to manage data base
 */

public class DBHelper extends SQLiteOpenHelper {

    private static  DBHelper instance = null;
    /*constructor*/
    private DBHelper(Context context){
        super(context,"Library",null,1);
    }

    public static DBHelper getInstance(Context context){
        if (instance==null)
            instance=new DBHelper(context);
        return instance ;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        /*construct the DB tables*/

        db.execSQL("create table Category (id integer primary key AUTOINCREMENT, name text);" );
        db.execSQL("create table Admin (id integer primary key, name text,email text,pwd text);" );
        db.execSQL("create table Book (id integer primary key, title text, category_id integer," +
                " foreign key (category_id) references Category(id)); ");
        db.execSQL("create table Author (id integer primary key, name text);" );
        db.execSQL("create table BookAuthors (book_id integer, author_id integer," +
                " foreign key (book_id) references Book(id), foreign key (author_id) references Author(id));");
    }

    /*drop the DB tables if exist*/
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Category " );
        db.execSQL("DROP TABLE IF EXISTS Book " );
        db.execSQL("DROP TABLE IF EXISTS Author " );
        db.execSQL("DROP TABLE IF EXISTS BookAuthors " );
        onCreate(db);
    }
    /*fill the DB categories*/
    public void fillCategory(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("insert into Category (name) values('Math'),('Drama'),('Romance')," +
                "('Travel'),('Children'),('Religion'),('Science'),('History'),('Comedy')," +
                "('Tragedy'),('Adventure'),('cook'),('Art'),('Poetry'),('Health')");
    }

    /*get the categories name and id from thr DB*/
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
                } while (resultSet.moveToNext());
            }
        }
        return categories ;
    }


    /*fill the book table in DB from the API*/
    public void fillBooksFromAPI() throws JSONException {
        Map <Integer,String> categories = getCategories();
        // print all categories
        Iterator<Integer> itr = categories.keySet().iterator();
        while(itr.hasNext()){
            final int catId = itr.next();
            final String catName = categories.get(catId);
            String apiUrlString = "https://www.googleapis.com/books/v1/volumes?q=subject:"+catName;
            new GoogleApiRequest(apiUrlString ,new GoogleApiRequest.AsyncResponse(){
                @Override
                public void processFinish(JSONObject output) throws JSONException {
                    /*parse the JSON file*/
                    JSONObject responseJson = output;
                    if (responseJson.has("items")) {
                        JSONArray booksArray = responseJson.getJSONArray("items");
                        SQLiteDatabase db = DBHelper.super.getWritableDatabase();
                        for (int i = 0; i < booksArray.length(); i++) {
                            JSONObject book = (JSONObject) booksArray.get(i);
                            if (book.has("volumeInfo")) {
                                String title ="",author="",changedTitle="";
                                JSONObject volumeInfo = book.getJSONObject("volumeInfo");
                                if (volumeInfo.has("title")) {
                                    title = volumeInfo.getString("title");
                                    changedTitle = title.replace("'", "''");
                                    changedTitle = changedTitle.replace(' ', '$').toLowerCase().trim();
                                }
                                int bookId = 0;
                                try {
                                    Log.d("inserted-book", changedTitle);
                                    db.execSQL("insert into Book values (null,\'" + changedTitle + "\'," + catId + ");");

                                    Cursor resultSet =  db.rawQuery("SELECT MAX(id) AS LastID FROM Book;", null);
                                    if (resultSet != null) {
                                        if (resultSet.moveToFirst()) {
                                            do {
                                                bookId = resultSet.getInt(resultSet.getColumnIndex("LastID"));
                                            } while (resultSet.moveToNext());
                                        }
                                    }
                                }catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (volumeInfo.has("authors")) {
                                    JSONArray authorsArray = volumeInfo.getJSONArray("authors");
                                    ArrayList<String> authors = new ArrayList<>();
                                    try {
                                        for (int k = 0; k < authorsArray.length(); k++) {
                                            author = (String) authorsArray.get(i);
                                            author.replaceAll(",", " ");
                                            authors.add(author);
                                        }
                                    }catch (Exception e) {}
                                    for (String auth: authors) {
                                        Log.d("Author", auth);
                                        db.execSQL("insert into Author values (null,\'" + auth.trim().toLowerCase() + "\');");
                                        Cursor resultSet = db.rawQuery("SELECT MAX(id) AS LastID FROM Author;", null);
                                        int authorId = 0;
                                        if (resultSet != null) {
                                            if (resultSet.moveToFirst()) {
                                                do {
                                                    authorId = resultSet.getInt(resultSet.getColumnIndex("LastID"));
                                                } while (resultSet.moveToNext());
                                            }
                                        }
                                        db.execSQL("insert into BookAuthors values (" + bookId + "," + authorId + ");");
                                    }
                                }
                            }
                        }
                    }
                }
            }).execute();
        }
    }
    /*get book from the DB by category name and ID*/
    public ArrayList<String> getBooks(int categoryID , String categoryName){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String>books = new ArrayList<String>();
        /*result set from the DB*/
        Cursor resultSet = db.rawQuery("Select * from Book where category_id="+categoryID,null);

        if (resultSet != null) {
            if (resultSet.moveToFirst()) {
                do {
                    String bookTitle = resultSet.getString(resultSet.getColumnIndex("title"));
                    books.add(bookTitle.replace('$', ' '));
                } while (resultSet.moveToNext());
            }
        }
        return books;
    }
    public ArrayList<String> findBook(String title, String authors){

        SQLiteDatabase db = this.getReadableDatabase();
        String[] authorsList = authors.split("\\s*,\\s*");
        String values = "";

        for (int i=0;i<authorsList.length;i++){
            values += "'" + authorsList[i].trim().toLowerCase() + "'";
               if (i < authorsList.length-1){
                   values +=",";
               }
        }
        title = title.replace(' ', '$').toLowerCase().trim();
        Cursor resultSet = null;
        ArrayList<String> booksTitles = new ArrayList<String>();
        if (title.length() >0 && authors.length() > 0) {

            resultSet =
                    db.rawQuery("Select * FROM (Select title from Book where id IN " +
                            "(Select book_id from BookAuthors where author_id IN (Select id from Author where name IN (" + values + ")))) t1" +
                            " INNER JOIN (SELECT title from Book where title="+"\'"+title+"\'" + ") t2 on t1.title = t2.title", null);


        }else if (title.length() == 0 && authors.length() > 0){
            resultSet =
                    db.rawQuery("Select title from Book where id IN " +
                            "( Select book_id from BookAuthors where author_id IN (Select id from Author where name IN (" + values + ")))", null);

        }else if (title.length() > 0  && authors.length() ==0) {
            resultSet = db.rawQuery("SELECT title FROM Book WHERE title="+"\'"+title+"\'",null);
        }

        if (resultSet != null) {
            if (resultSet.moveToFirst()){
                do {
                    String bookTitle = resultSet.getString(resultSet.getColumnIndex("title"));
                    booksTitles.add(bookTitle.replace('$', ' '));
                } while (resultSet.moveToNext());
            }
        }
        return booksTitles;
    }
}