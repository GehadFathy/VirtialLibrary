package com.example.library.sw_library.Views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.library.sw_library.Database.DBHelper;
import com.example.library.sw_library.Models.BookModel;
import com.example.library.sw_library.R;

import java.util.List;

public class ShelvesViewActivity extends AppCompatActivity {

    private int categoryID ;
    private String categoryName;
    private int no_of_books_in_shelf=5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelf);

        categoryID = getIntent().getExtras().getInt("id");
        categoryName = getIntent().getExtras().getString("name");
        Log.e("Gehad", "Extras: "+categoryID+ " "+categoryName);


        DBHelper dbHelper=new DBHelper(this);
        List<BookModel> books = dbHelper.getBooks(categoryID,categoryName);
        int numCol = no_of_books_in_shelf;
        int numRow = books.size()/numCol;

        Log.e("Gehad", "num: "+books.size()+" "+numCol+ " "+numRow);

        TableLayout tblLayout = findViewById(R.id.tblLayout);

        for(int i = 0; i < numRow; i++) {
            //for shelf scroll
            HorizontalScrollView HSV = new HorizontalScrollView(this);
            HSV.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                    LayoutParams.FILL_PARENT));

            TableRow tblRow = new TableRow(this);
            tblRow.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT));
            tblRow.setBackgroundResource(R.drawable.shelf);

           for(int j = 0; j < numCol; j++) {
                ImageView imageView = new ImageView(this);
                imageView.setImageResource(R.drawable.new_book);

                TextView textView = new TextView(this);
                textView.setText("Java Tester");
                textView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT));

                tblRow.addView(imageView,j);
            }

            HSV.addView(tblRow);
            tblLayout.addView(HSV, i);

        }
    }

}
