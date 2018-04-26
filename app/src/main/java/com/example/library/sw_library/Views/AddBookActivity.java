package com.example.library.sw_library.Views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.library.sw_library.Database.DBHelper;
import com.example.library.sw_library.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AddBookActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText bookNameText, authorNameText;
    Spinner categorySpinner;
    String bookName, authorName, categoryName;

    Map<Integer,String> categories = new HashMap<Integer,String>();
    Button addBtn;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        dbHelper=DBHelper.getInstance(this);
        categories=dbHelper.getCategories();

        final List<String>categoriesList= new ArrayList<>();

        Iterator<Integer> itr = categories.keySet().iterator();
        while (itr.hasNext()){
            Integer id  = itr.next();
            categoriesList.add(categories.get(id));
            Log.e("onCreate", "onCreate: "+ id+" "+categories.get(id));
        }


        bookNameText =findViewById(R.id.book_name);
        authorNameText =findViewById(R.id.author_name);
        categorySpinner=findViewById(R.id.category_spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddBookActivity.this,
                android.R.layout.simple_spinner_item,categoriesList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        categorySpinner.setOnItemSelectedListener(this);

        addBtn = findViewById(R.id.add_btn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookName=bookNameText.getText().toString();
                authorName=authorNameText.getText().toString();
                Toast.makeText(getApplicationContext(),categoryName+" "+getCategoryId(categoryName),Toast.LENGTH_SHORT).show();
                if (bookName.length()==0 ||authorName .length()==0 ){
                    new AlertDialog.Builder(AddBookActivity.this)
                            .setTitle("Warning")
                            .setMessage("Please Fill all fields")
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Whatever...
                                }
                            }).show();

                }

                else {
                    dbHelper.addOneBook(bookName, authorName, getCategoryId(categoryName));
                    Intent intent = new Intent(AddBookActivity.this, CategoryViewActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        categoryName = adapterView.getItemAtPosition(i).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public int getCategoryId(String categoryName){

        Iterator<Integer> itr = categories.keySet().iterator();
        while (itr.hasNext()){
            Integer id  = itr.next();
            Log.e("Gehad", "onCreate:  id:  "+id );
            if (categoryName.equals(categories.get(id))){
                return id;
            }
        }
        return -1;
    }
}
