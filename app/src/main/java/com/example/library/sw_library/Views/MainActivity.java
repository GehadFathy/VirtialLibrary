package com.example.library.sw_library.Views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.library.sw_library.Database.DBHelper;
import com.example.library.sw_library.R;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.deleteDatabase("Library");

        DBHelper mydb =DBHelper.getInstance(this);
        mydb.fillCategory();
        mydb.getCategories();
        mydb.initialFillBooks();
//        mydb.getBook();
        mydb.close();
        Toast.makeText(this,"to swtich",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(MainActivity.this,ShelvesViewActivity.class);
        intent.putExtra("id",1);
        intent.putExtra("name","Math");
         startActivity(intent);


    }
}
