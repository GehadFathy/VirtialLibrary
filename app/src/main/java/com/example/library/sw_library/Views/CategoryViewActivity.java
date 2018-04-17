package com.example.library.sw_library.Views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.library.sw_library.R;

public class CategoryViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelf);
        int numRow = 4;
        int numCol = 8;

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
