package com.example.library.sw_library.Views;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.library.sw_library.Network.GoogleApiRequest;
import com.example.library.sw_library.R;

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

        Log.d("BookView", "onCreateBook: "+bookTitle );
       String apiUrlString="https://www.googleapis.com/books/v1/volumes?q=intitle:" + bookTitle + "&filter:free-ebooks&printType:books";
        new GoogleApiRequest(apiUrlString ,new GoogleApiRequest.AsyncResponse(){
            @Override
            public void processFinish(JSONObject output) throws JSONException {
                displayBook(output);
                Log.d(this.toString(), "BookResponse: "+output);
            }}).execute();


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

            Log.d("RESPONSE", "displayBook: ");
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
