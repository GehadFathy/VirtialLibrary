package com.example.library.sw_library.Views;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.library.sw_library.Network.GoogleApiRequest;
import com.example.library.sw_library.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

/*
Book View
*/
public class BookViewActivity extends AppCompatActivity implements GoogleApiRequest.AsyncResponse {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        /*get the book title from the previous activity*/
        String bookTitle = getIntent().getExtras().getString("title");

        Log.d("BookView", "onCreateBook: "+bookTitle );
        String apiUrlString="https://www.googleapis.com/books/v1/volumes?q=intitle:" + bookTitle.replace(" ", "+") + "&filter:free-ebooks&printType:books";
        new GoogleApiRequest(apiUrlString,this).execute(bookTitle);


    }

    /*Image downloader*/
    protected class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
            Toast.makeText(getApplicationContext(), "Please wait, it may take a few minute...", Toast.LENGTH_SHORT).show();
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }

    /*display the book info.*/
    public void displayBook(JSONObject obj) {
        try {
            JSONObject volumeInfo = obj.getJSONObject("volumeInfo");
            String title = volumeInfo.getString("title");
            String isbn = "";
            if(volumeInfo.has("industryIdentifiers"))isbn = (String) ((JSONObject)volumeInfo.getJSONArray("industryIdentifiers").get(0)).get("identifier");
            JSONArray authorsArray = null;
            if(volumeInfo.has("authors")) authorsArray = volumeInfo.getJSONArray("authors");
            JSONArray categoriesArr = null;
            if(volumeInfo.has("categories")) categoriesArr = volumeInfo.getJSONArray("categories");
            String authors = "" ;
            try {
                if (authorsArray != null) {
                    for (int k = 0; k < authorsArray.length(); k++) {
                        authors += authorsArray.get(k) + ", ";
                    }
                }
            }catch (Exception e) {}

            String categories = "";
            try {
                if (categoriesArr != null) {
                    for (int i = 0; i < categoriesArr.length(); i++) {
                        categories += categoriesArr.get(i) + ", ";
                    }
                }
            }catch (Exception e){}

            String imageURL = null;
            if(volumeInfo.has("imageLinks"))imageURL = (String) volumeInfo.getJSONObject("imageLinks").get("smallThumbnail");
            String previewLink = "";
            if(volumeInfo.has("previewLink")) previewLink = (String) volumeInfo.get("previewLink");
            if(imageURL != null) new DownloadImageFromInternet((ImageView) findViewById(R.id.imageView1))
                    .execute(imageURL);
            TextView titleTV = (TextView) findViewById(R.id.titleTextView);
            titleTV.setText("Title: " + title);
            TextView authorsTV = (TextView) findViewById(R.id.authorTextView);
            authorsTV.setText("Authors: " + authors);
            TextView ISBNTV = (TextView) findViewById(R.id.ISBNTextView);
            ISBNTV.setText("ISBN: " + isbn);
            TextView categoryTV = (TextView) findViewById(R.id.categoryTextView);
            categoryTV.setText("Category: " + categories);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void processFinish(JSONObject output) throws JSONException {
        if(output.getInt("totalItems") != 0) {
            JSONObject book = (JSONObject) output.getJSONArray("items").get(0);
            Log.d(this.toString(), "BookResponse: " + book);
            displayBook(book);
        }
    }
}