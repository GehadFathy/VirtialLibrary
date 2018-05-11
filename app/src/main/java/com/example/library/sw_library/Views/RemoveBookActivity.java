package com.example.library.sw_library.Views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.library.sw_library.Database.DBHelper;
import com.example.library.sw_library.R;

public class RemoveBookActivity extends AppCompatActivity {

    EditText book_text;
    String book_name="";
    Button removeBtn;
    DBHelper dbHelper;
    String admin_name="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_book);
        admin_name =  getIntent().getExtras().getString("name");

        book_text=findViewById(R.id.book_name);
        removeBtn=findViewById(R.id.remove_btn);
        dbHelper=DBHelper.getInstance(this);

        removeBtn.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             book_name= book_text.getText().toString();
                                             Log.e("remove_book", "isbn: " +book_name);

                                             if(book_name.matches("")){
                                                 //Toast.makeText(getApplicationContext(), "Please enter book ISBN", Toast.LENGTH_SHORT).show();
                                                 new AlertDialog.Builder(RemoveBookActivity.this)
                                                         .setTitle("Error")
                                                         .setMessage("Please enter book Title")
                                                         .setCancelable(false)
                                                         .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                             @Override
                                                             public void onClick(DialogInterface dialog, int which) {
                                                                 // Whatever...
                                                             }
                                                         }).show();
                                             }
//                else if (!dbHelper.bookExist(isbn)){
//                    Toast.makeText(getApplicationContext(), "invalid book ISBN", Toast.LENGTH_SHORT).show();
//                }
                                             else {

                                                 int deleted=dbHelper.removeBook(book_name);
                                                 if(deleted >0) {
                                                     Toast.makeText(getApplicationContext(), "Book removed successfully", Toast.LENGTH_SHORT).show();
                                                     Intent intent = new Intent(RemoveBookActivity.this, CategoryViewActivity.class);
                                                     intent.putExtra("admin", true);
                                                     intent.putExtra("name", admin_name);
                                                     startActivity(intent);
                                                 }
                                                 else{
                                                     new AlertDialog.Builder(RemoveBookActivity.this)
                                                             .setTitle("Error")
                                                             .setMessage("Ivalid book title")
                                                             .setCancelable(false)
                                                             .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                                 @Override
                                                                 public void onClick(DialogInterface dialog, int which) {
                                                                     // Whatever...
                                                                 }
                                                             }).show();
                                                 }
                                             }


                                         }

                                     }

        );

    }
}
