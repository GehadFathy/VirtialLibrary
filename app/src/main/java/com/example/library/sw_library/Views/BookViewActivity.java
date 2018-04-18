package com.example.library.sw_library.Views;

import com.example.library.sw_library.Models.*;
import com.example.library.sw_library.R;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class BookViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        String bookTitle = getIntent().getExtras().getString("title");

        BookModel bookModel = new BookModel();
        try {
            JSONObject bookInfo = bookModel.getBookJSONObject(bookTitle);
            displayBook(bookInfo);
        } catch (BookModel.BadRequestException e) {
            e.printStackTrace();
        }
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });



    }

    private void displayBook(JSONObject obj) {
        try {
            JSONObject volumeInfo = obj.getJSONObject("volumeInfo");
            String title = volumeInfo.getString("title");
            String isbn = (String) volumeInfo.get("industryIdentifiers");
            JSONArray authorsArray = volumeInfo.getJSONArray("authors");
            JSONArray categoriesArr = volumeInfo.getJSONArray("categories");
            String authors = "" ;
            for (int k=0 ; k<authorsArray.length();k++){
                authors+=authorsArray.get(k)+", ";
            }
            String categories = "";
            for (int i = 0; i < categoriesArr.length(); i++) {
                categories+=categoriesArr.get(i)+", ";
            }
            String imageURL = (String) volumeInfo.getJSONObject("imageLinks").get("smallThumbnail");
            String previewLink = (String) volumeInfo.get("previewLink");

            setImage(imageURL);
            TextView titleTV = (TextView) findViewById(R.id.titleTextView);
            titleTV.setText("Title: " + title);
            TextView authorsTV = (TextView) findViewById(R.id.authorTextView);
            authorsTV.setText("Authors: " + authors);
            TextView ISBNTV = (TextView) findViewById(R.id.ISBNTextView);
            ISBNTV.setText("ISBN: " + authors);
            TextView categoryTV = (TextView) findViewById(R.id.categoryTextView);
            categoryTV.setText("Category: " + categories);

            setImage(imageURL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setImage(String imageAddress) {
        final ImageView imageView = (ImageView) findViewById(R.id.imageView1);
        URL url = null;
        try {
            url = new URL(imageAddress);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        InputStream content = null;
        try {
            content = (InputStream)url.getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Drawable d = Drawable.createFromStream(content , "src");
        imageView.setImageDrawable(d);
    }


}
