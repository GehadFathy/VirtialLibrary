package com.example.library.sw_library.Views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.library.sw_library.Database.DBHelper;
import com.example.library.sw_library.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBHelper mydb = new DBHelper(this);
        mydb.fillCategory();
        mydb.getBooks(1);
        mydb.close();

    }
}
