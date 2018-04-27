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
    /*constructor*/
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
        /*construct the DB tables*/
        db.execSQL("create table Category (id integer primary key AUTOINCREMENT, name text);" );
        db.execSQL("create table Book (id integer primary key, title text, authors text, category_id integer," +
                " foreign key (category_id) references Category(id)); ");
        db.execSQL("create table Admin (id integer primary key AUTOINCREMENT, name text,email text,pwd text);" );

    }

    /*drop the DB tables if exist*/
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS Category " );
        db.execSQL("DROP TABLE IF EXISTS Book " );
        onCreate(db);

    }
    public int addOneBook(String bookName, String authorName, int categoryId){
        SQLiteDatabase db = this.getWritableDatabase();
        String query ="insert into Book values (null,\'"+bookName+"\',\'"+authorName +"\',"+categoryId+");";
        Log.e("Query", "addOneBook: "+query );
        db.execSQL(query);
        return  0;
    }

    /*fill the DB admins*/
    public void fillAdmin(){
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("insert into Admin values(null, 'Asmaa', 'sh.asmosama@hotmail.com', 'DontEnter'),"+
                "(null, 'Amira', 'amira@hotmail.com', 'DontEnter')," +
                "(null, 'Gehad', 'gehad@hotmail.com', 'DontEnter')," +
                "(null, 'Menna', 'menna@hotmail.com', 'DontEnter')");

    }

    /*fill the DB categories*/
    public void fillCategory(){
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("insert into Category (name) values('Math'),('Drama'),('Romance')," +
                "('Travel'),('Children'),('Religion'),('Science'),('History'),('Comedy')," +
                "('Tragedy'),('Adventure'),('cook'),('Art'),('Poetry'),('Health')");

    }

    /*initial fill for the Book tables for test*/
    public void initialFillBooks()  {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("insert into Book values (null,'Childcraft','null',15);");
        db.execSQL("insert into Book values (null,'Christmast','Holly Berry-Byrd',15);");
        db.execSQL("insert into Book values (null,'A Common Stage','Carol Symes',15);");
        db.execSQL("insert into Book values (null,'Macbeth','William Shakespeare',15);");
        db.execSQL("insert into Book values (null,'Man and Superman','George Bernard Shaw',15);");
        db.execSQL("insert into Book values (null,'Fawcetta','Dalian Artanian',15);");
        db.execSQL("insert into Book values (null,'Dare to Love','Carly Phillips',15);");


        db.execSQL("insert into Book values (null,'Childcraft','null',13);");
        db.execSQL("insert into Book values (null,'Christmast','Holly Berry-Byrd',13);");
        db.execSQL("insert into Book values (null,'A Common Stage','Carol Symes',5);");
        db.execSQL("insert into Book values (null,'Macbeth','William Shakespeare',4);");
        db.execSQL("insert into Book values (null,'Man and Superman','George Bernard Shaw',1);");
        db.execSQL("insert into Book values (null,'Fawcetta','Dalian Artanian',1);");
        db.execSQL("insert into Book values (null,'Dare to Love','Carly Phillips',8);");

        db.execSQL("insert into Book values (null,'Childcraft','null',8);");
        db.execSQL("insert into Book values (null,'Christmast','Holly Berry-Byrd',8);");
        db.execSQL("insert into Book values (null,'A Common Stage','Carol Symes',6);");
        db.execSQL("insert into Book values (null,'Macbeth','William Shakespeare',7);");
        db.execSQL("insert into Book values (null,'Man and Superman','George Bernard Shaw',5);");
        db.execSQL("insert into Book values (null,'Fawcetta','Dalian Artanian',6);");
        db.execSQL("insert into Book values (null,'Dare to Love','Carly Phillips',8);");

        db.execSQL("insert into Book values (null,'Childcraft','null',10);");
        db.execSQL("insert into Book values (null,'Christmast','Holly Berry-Byrd',10);");
        db.execSQL("insert into Book values (null,'A Common Stage','Carol Symes',11);");
        db.execSQL("insert into Book values (null,'Macbeth','William Shakespeare',11);");
        db.execSQL("insert into Book values (null,'Man and Superman','George Bernard Shaw',12);");
        db.execSQL("insert into Book values (null,'Fawcetta','Dalian Artanian',12);");
        db.execSQL("insert into Book values (null,'Dare to Love','Carly Phillips',12);");


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
                    Log.e("Category:::::: ",id+" "+category);
                } while (resultSet.moveToNext());
            }
        }
        else
            Log.e("Category:::::: ","No category");

        return categories ;
    }

    /*fill the book table in DB from the API*/
    public void fillBooksFromAPI() throws JSONException {
        Map <Integer,String> categories = getCategories();


        // print all categories

        ArrayList<String> cats = new ArrayList<>(categories.values());
        for (String s:cats) {
            Log.d("cat-i", s);
        }
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
                        Log.d("has-books", catName);

                        JSONArray booksArray = responseJson.getJSONArray("items");
                        SQLiteDatabase db = DBHelper.super.getWritableDatabase();
                        Log.d("books-array", catName + " " + booksArray.length());
                        for (int i = 0; i < booksArray.length(); i++) {

                            JSONObject book = (JSONObject) booksArray.get(i);

                            if (book.has("volumeInfo")) {

                                String title ="",author="",changedAuthors="",changedTitle="";

                                JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                                if (volumeInfo.has("title")) {
                                    title = volumeInfo.getString("title");
                                    changedTitle = title.replace("'", "''");
                                }
                                if (volumeInfo.has("authors")) {
                                    JSONArray authorsArray = volumeInfo.getJSONArray("authors");
                                    String authors = "";


                                    try {
                                        for (int k = 0; k < authorsArray.length(); k++) {
                                            author = (String) authorsArray.get(i);
                                            author.replaceAll(",", " ");
                                            authors += author;
                                            if (k != authorsArray.length() - 1)
                                                authors += ',';
                                        }
                                    }catch (Exception e) {}

                                    changedAuthors = authors.replace("'", "''");

                                }
                                Log.d("book-i",   catName+ changedTitle +" " + changedAuthors );
                                try {
                                    db.execSQL("insert into Book values (null,\'" + changedTitle + "\',\'" + changedAuthors + "\'," + catId + ");");
                                }catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }else {
                                Log.d("book-i", catName +" "+"hasn't volume info ");
                            }

                        }
                    }else {
                        Log.d("No-books", catName);
                    }

                }

            }).execute();
        }
    }

    /*get book*/
    public void getBook() {
        Log.d("BookView", "onCreateBook: christmast" );
        String apiUrlString="https://www.googleapis.com/books/v1/volumes?q=intitle:Childcraft&filter:free-ebooks&printType:books";
        new GoogleApiRequest(apiUrlString ,new GoogleApiRequest.AsyncResponse(){
            @Override
            public void processFinish(JSONObject output) throws JSONException {
                Log.d(this.toString(), "BookResponse: "+output);
            }}).execute();

    }

    /*get book from the DB by category name and ID*/
    public List<BookModel> getBooks(int categoryID , String categoryName){
        SQLiteDatabase db = this.getReadableDatabase();
        List<BookModel>books = new ArrayList<BookModel>();
        /*result set from the DB*/
        Cursor resultSet = db.rawQuery("Select * from Book where category_id="+categoryID,null);
        if (resultSet != null) {
            if (resultSet.moveToFirst()) {
                do {

                    int bookId = resultSet.getInt(resultSet.getColumnIndex("id"));
                    String bookTitle = resultSet.getString(resultSet.getColumnIndex("title"));
                    String authorsString = resultSet.getString(resultSet.getColumnIndex("authors"));
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

    public boolean validMail(String mail) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resultSet = db.rawQuery("Select * from Admin where email='"+mail+"'",null);
        return resultSet != null;
    }

    public boolean validPW(String mail, String pw) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resultSet = db.rawQuery("Select pwd from Admin WHERE email=?",new String[]{mail});
        if (resultSet == null) return false;
        if(resultSet.moveToFirst()) {
            Log.e("DBHELPER", "validPW: " + resultSet.getString(resultSet.getColumnIndex("pwd")));
            return resultSet.getString(resultSet.getColumnIndex("pwd")).equals(pw);
        }
        else {
            return false;
        }
    }
}