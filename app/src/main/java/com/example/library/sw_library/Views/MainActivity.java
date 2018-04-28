package com.example.library.sw_library.Views;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.library.sw_library.Database.DBHelper;
import com.example.library.sw_library.R;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
Splash screen
*/
public class MainActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 2500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            this.deleteDatabase("Library");
            DBHelper mydb = DBHelper.getInstance(this);
            mydb.fillCategory();
            mydb.fillAdmin();
            try {
                mydb.fillBooksFromAPI();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mydb.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent mainIntent = new Intent(MainActivity.this,CategoryViewActivity.class);
                mainIntent.putExtra("admin",false);
                MainActivity.this.startActivity(mainIntent);
                MainActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}