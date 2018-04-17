package com.example.library.sw_library.Views;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class activity_book extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
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

    public void displayBook(JSONObject obj) {
        try {
            JSONObject volumeInfo = obj.getJSONObject("volumeInfo");
            String title = volumeInfo.getString("title");
            JSONArray authorsArray = volumeInfo.getJSONArray("authors");
            String authors = "" ;
            for (int k=0 ; k<authorsArray.length();k++){
                authors+=authorsArray.get(i)+",";
            }
            String imageURL = (String) volumeInfo.getJSONObject("imageLinks").get("smallThumbnail");
            String previewLink = (String) volumeInfo.get("previewLink");

            setImage(imageURL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setImage(String imageAddress) {
        final ImageView imageView = (ImageView) findViewById(R.id.imageDisplay);
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
