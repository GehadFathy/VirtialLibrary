package com.example.library.sw_library.Database;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
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
        db.execSQL("create table Admin (id integer primary key AUTOINCREMENT, name text,email text,pwd text);" );
        db.execSQL("create table Category (id integer primary key AUTOINCREMENT, name text);" );
        db.execSQL("create table Book (id integer primary key, title text, category_id integer," +
                " foreign key (category_id) references Category(id) ON DELETE CASCADE); ");
        db.execSQL("create table BookAuthors (book_id integer, author_name text," +
                " foreign key (book_id) references Book(id));");

    }

    /*drop the DB tables if exist*/
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS Category " );
        db.execSQL("DROP TABLE IF EXISTS Book " );
        db.execSQL("DROP TABLE IF EXISTS Admin " );
        db.execSQL("DROP TABLE IF EXISTS BookAuthors " );
        onCreate(db);

    }
    public void addOneBook(String bookName, String authorName, int categoryId){
        SQLiteDatabase db = this.getWritableDatabase();
        if (bookName!=null &&authorName !=null) {
            db.execSQL("insert into Book values (null,\'" + bookName + "\'," + categoryId + ");");

            int bookId=0;
            Cursor resultSet =  db.rawQuery("SELECT MAX(id) AS LastID FROM Book;", null);
            if (resultSet != null) {
                if (resultSet.moveToFirst()) {
                    do {
                        bookId = resultSet.getInt(resultSet.getColumnIndex("LastID"));
                    } while (resultSet.moveToNext());
                }
            }
            db.execSQL("insert into BookAuthors values (" + bookId + ",\'" + authorName+ "\')");
        }
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
                                }
                                int bookId = 0;
                                try {
                                    Log.d("inserted-book", title);
                                    db.execSQL("insert into Book values (null,\'"  + title + "\'," + catId + ");");

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
                                        db.execSQL("insert into BookAuthors values (" + bookId + ",\'" + auth+ "\')");
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
    public ArrayList<String> getBooks(int categoryID){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String>books = new ArrayList<String>();
        /*result set from the DB*/
        Cursor resultSet = db.rawQuery("Select * from Book where category_id="+categoryID,null);

        if (resultSet != null) {
            if (resultSet.moveToFirst()) {
                do {
                    String bookTitle = resultSet.getString(resultSet.getColumnIndex("title"));
                    books.add(bookTitle);
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
        Cursor resultSet = null;
        ArrayList<String> booksTitles = new ArrayList<String>();
        if (title.length() >0 && authors.length() > 0) {
            resultSet =
                    db.rawQuery("Select * FROM (Select title from Book where id IN " +
                            "(Select book_id from BookAuthors where author_name IN (" + values + "))) t1" +
                            " INNER JOIN (SELECT title from Book where title IN (\'"+title.trim()+"\')) t2 on t1.title = t2.title", null);


        }else if (title.length() == 0 && authors.length() > 0){
            resultSet =
                    db.rawQuery("Select title from Book where id IN " +
                            "( Select book_id from BookAuthors where author_name IN (" + values + "))", null);


        }else if (title.length() > 0  && authors.length() ==0){
            resultSet = db.rawQuery("SELECT title FROM Book WHERE title IN (\'" + title.trim() + "\')",null);
        }
        if (resultSet != null) {
            if (resultSet.moveToFirst()){
                do {
                    String bookTitle = resultSet.getString(resultSet.getColumnIndex("title"));
                    booksTitles.add(bookTitle);
                } while (resultSet.moveToNext());
            }
        }
        return booksTitles;
    }
    public boolean validMail(String mail) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resultSet = db.rawQuery("Select * from Admin where email='"+mail+"'",null);
        if(resultSet != null){
            if(resultSet.moveToFirst()) {
                return true;
            }
            else{
                return false;
            }
        }
        else {
            return false;
        }
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

    public int getTotalNumBooks(){
        SQLiteDatabase db = this.getReadableDatabase();
        Long count = DatabaseUtils.queryNumEntries(db, "Book");
        db.close();
        return count.intValue();
    }
    public int getNumOfBooksOFCategory(Integer categoryId){
        SQLiteDatabase db = this.getReadableDatabase();
        Long count = DatabaseUtils.longForQuery(db, "SELECT COUNT (*) FROM Book  WHERE category_id =?",
                new String[] { String.valueOf(categoryId) });
        db.close();
        return count.intValue();
    }

    /*remove book from DB*/
    public int removeBook(String title){
        SQLiteDatabase db = this.getWritableDatabase();
        int deleted=db.delete("Book","title" + "='" + title +"'",null);
        Log.e("menna", "deleted: " + deleted);
        return deleted;
    }



    public String getAuthorForBook(String bookName){

        SQLiteDatabase db = this.getReadableDatabase();
        String authorsString="";
        Cursor resultSet = db.rawQuery("Select * from Book where title =?", new String[] {bookName});
        if (resultSet.moveToFirst()) {
            do {
                authorsString = resultSet.getString(resultSet.getColumnIndex("authors"));
            } while (resultSet.moveToNext());
        }

        return authorsString;

    }

}