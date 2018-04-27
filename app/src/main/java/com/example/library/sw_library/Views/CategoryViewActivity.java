package com.example.library.sw_library.Views;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.library.sw_library.Database.DBHelper;
import com.example.library.sw_library.R;
import java.util.ArrayList;
import java.util.Map;

/*
Category view
*/
public class CategoryViewActivity extends AppCompatActivity {
    private Map<Integer,String> categories; //map each categoryID to its name
    private CategoryAdapter categoryAdapter;
    private DBHelper dbhelber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_view);
        dbhelber = DBHelper.getInstance(this);
        categories = dbhelber.getCategories();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryAdapter = new CategoryAdapter(recyclerView, new ArrayList<>(categories.values()), this);
        recyclerView.setAdapter(categoryAdapter);
        categoryAdapter.setOnClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(String category) {
                /*send the category name to the next activity*/
                Intent intent=new Intent(CategoryViewActivity.this,ShelvesViewActivity.class);
                int id = 0;
                for (Map.Entry<Integer, String> entry : categories.entrySet()) {
                    if (category.equals(entry.getValue())) {
                        id = entry.getKey();
                        break;
                    }
                }
                Log.d("category ", category);
                ArrayList<String> books = dbhelber.getBooks(id,category);
                Log.d("book-size", String.valueOf(books.size()));
                for (int i=0;i<books.size();i++){
                    Log.d("book-i", books.get(i));
                }
                intent.putExtra("books",books);
                startActivity(intent);
            }
        });
    }
}