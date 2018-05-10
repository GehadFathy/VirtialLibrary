package com.example.library.sw_library.Views;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import com.example.library.sw_library.Database.DBHelper;
import com.example.library.sw_library.R;
import java.util.ArrayList;
public class SearchBookActivity extends AppCompatActivity {
    DBHelper dbHelper = DBHelper.getInstance(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book);
        ImageButton buttonOne = (ImageButton) findViewById(R.id.imageButton);
        buttonOne.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View v) {
                Intent intent= new Intent(SearchBookActivity.this,CategoryViewActivity.class);
                intent.putExtra("admin",false);
                startActivity(intent);

            }
        });
        Button buttonTwo = (Button) findViewById(R.id.button);
        buttonTwo.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                EditText title = (EditText) findViewById(R.id.editText3);
                EditText authors = (EditText) findViewById(R.id.editText4);
                String bookTitle = String.valueOf(title.getText());
                String bookAuthors = String.valueOf(authors.getText());

                ArrayList<String> books = dbHelper.findBook(bookTitle,bookAuthors);
                Intent intent= new Intent(SearchBookActivity.this,ShelvesViewActivity.class);
                intent.putExtra("books",books);

                startActivity(intent);

            }
        });
    }
}