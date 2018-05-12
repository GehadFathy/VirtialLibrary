package com.example.library.sw_library.Views;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.HorizontalScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.example.library.sw_library.R;
import java.util.ArrayList;

/*
Shelves view class
*/
public class ShelvesViewActivity extends AppCompatActivity {

    private ArrayList<String> books;
    private int no_of_books_in_shelf=4; //max. number of books/shelf

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelf);
        /*get category name and id from previous activity*/

        /*get the books for the specified category*/
        books = getIntent().getExtras().getStringArrayList("books");
        int numCol = no_of_books_in_shelf;
        int numRow = (int) Math.ceil(books.size()/(float)numCol);
        Log.e("Gehad", "num: "+books.size()+" "+numCol+ " "+numRow);

        TableLayout tblLayout = findViewById(R.id.tblLayout);
        int bookCounter=0;

        /*fill the shelves with books*/
        for(int i = 0; i < numRow; i++) {
            //for shelf scroll
            HorizontalScrollView HSV = new HorizontalScrollView(this);
            HSV.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                    LayoutParams.FILL_PARENT));

            TableRow tblRow = new TableRow(this);
            tblRow.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT));
            tblRow.setBackgroundResource(R.drawable.shelf);

            for(int j = 0; j < numCol && bookCounter<books.size(); j++) {

                final String name=books.get(bookCounter++);
                TextView textView = new TextView(this);

                textView.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                textView.setSingleLine(false);
                textView.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                int cnt = 0;
                String s = name.toString();
                for (int k=0;k<s.length();k++){
                    if (s.charAt(k) == ' ') {
                        cnt++;
                    }
                    if (cnt == 2) {
                        s = s.substring(0, k) + "\n" + s.substring(k + 1, s.length());
                        cnt = 0;
                    }
                }
                textView.setText(s);

                textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                textView.setBackgroundResource(R.drawable.book2);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ShelvesViewActivity.this,BookViewActivity.class);
                        intent.putExtra("title",name);
                        startActivity(intent);
                    }
                });
                tblRow.addView(textView,j);
            }

            HSV.addView(tblRow);
            tblLayout.addView(HSV, i);

        }
    }

}