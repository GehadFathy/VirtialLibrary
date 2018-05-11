package com.example.library.sw_library.Views;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
    private boolean admin = false ;
    private String admin_name = "" ;
    private Menu dropDownMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_view);
        admin =  getIntent().getExtras().getBoolean("admin");
        admin_name =  getIntent().getExtras().getString("name");
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
                ArrayList<String> books = dbhelber.getBooks(id);
                Log.d("book-size", String.valueOf(books.size()));
                for (int i=0;i<books.size();i++){
                    Log.d("book-i", books.get(i));
                }
                intent.putExtra("books",books);
                startActivity(intent);
            }
        });
    }
    /*
    for login menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.library, menu);

        dropDownMenu = menu;

        //if not admin -> don't show add and remove options
        MenuItem item = menu.findItem(R.id.menu_add);
        MenuItem item1 = menu.findItem(R.id.menu_login);
        MenuItem item2 = menu.findItem(R.id.menu_remove);

        if (!admin) {
            item.setVisible(false);
            item2.setVisible(false);
        }
        else {
            getSupportActionBar().setTitle(admin_name);
            item1.setTitle("Logout");
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.menu_login) {
            Toast.makeText(this,"clicked",Toast.LENGTH_SHORT).show();

            MenuItem item1 = dropDownMenu.findItem(R.id.menu_login);
            MenuItem item2 = dropDownMenu.findItem(R.id.menu_remove);
            MenuItem item3 = dropDownMenu.findItem(R.id.menu_add);
            if(admin) {
                getSupportActionBar().setTitle("Virtual Shelf Browser");
                item1.setTitle("Login");
                item3.setVisible(false);
                item2.setVisible(false);
                admin = false;
            }
            else {
                Intent loginIntent = new Intent(
                        CategoryViewActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        }if (item.getItemId() == R.id.menu_add) {
            Intent loginIntent = new Intent(
                    CategoryViewActivity.this, AddBookActivity.class);
            startActivity(loginIntent);

        }

        return true;
    }
}