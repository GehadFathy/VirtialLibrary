package com.example.library.sw_library.Views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.library.sw_library.Database.DBHelper;
import com.example.library.sw_library.Network.GoogleApiRequest;
import com.example.library.sw_library.R;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.deleteDatabase("Library");
/*
       DBHelper mydb = new DBHelper(this);
        mydb.fillCategory();
        mydb.getCategories();
        try {
            mydb.fillBooksFromAPI();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mydb.close();
        Toast.makeText(this,"to swtich",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(this,ShelvesViewActivity.class);
        intent.putExtra("id",1);
        intent.putExtra("name","Math");

        startActivity(intent);*/
        new GoogleApiRequest("Math" ,new GoogleApiRequest.AsyncResponse() {
            @Override
            public void processFinish(JSONObject output) throws JSONException {
                JSONObject responseJson = output;
                Log.e("Main", "Main: "+ responseJson);
            }
        }).execute();


    }
}
