package com.example.library.sw_library.Views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.library.sw_library.Database.DBHelper;
import com.example.library.sw_library.Network.GoogleApiRequest;
import com.example.library.sw_library.R;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       DBHelper mydb = new DBHelper(this);
        mydb.fillCategory();
        mydb.getCategories();
        try {
            mydb.fillBooksFromAPI();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mydb.getBooks(1);
        mydb.close();

    }
}
