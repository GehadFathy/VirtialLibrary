package com.example.library.sw_library;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.example.library.sw_library.Database.DBHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RemoveBookTest {


    private DBHelper dbHelper;

    @Before
    public void setUp(){
        dbHelper = DBHelper.getInstance(InstrumentationRegistry.getTargetContext());
    }
//    @Test
//    public void testPreConditions() {
//        assertNotNull(dbHelper);
//    }



    @Test
    public void removeIsCorrectInTotal() throws Exception {
        int countBefore =dbHelper.getTotalNumBooks();
        dbHelper.removeBook("Sequence Numbers");
        int countAfter =dbHelper.getTotalNumBooks();
        assertEquals(countBefore-1, countAfter);
    }
    @Test
    public void removeIsEmpty() throws Exception {
        int countBefore =dbHelper.getTotalNumBooks();
        dbHelper.removeBook("");
        int countAfter =dbHelper.getTotalNumBooks();
        assertEquals(countBefore, countAfter);
    }

    @Test
    public void removeIsIncorrect() throws Exception {
        int countBefore =dbHelper.getTotalNumBooks();
        dbHelper.removeBook("db");
        int countAfter =dbHelper.getTotalNumBooks();
        assertEquals(countBefore, countAfter);
    }

    @Test
    public void confirmRemove() throws Exception {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor resultSet_bf = db.rawQuery("Select * from Book where title=?",new String[]{"Practicing History"});
        assertEquals(true, resultSet_bf.moveToFirst());
        dbHelper.removeBook("Practicing History");
        Cursor resultSet_aft = db.rawQuery("Select * from Book where title=?",new String[]{"Practicing History"});
        assertEquals(false, resultSet_aft.moveToFirst());
    }

    @Test
    public void removeAdd1() throws Exception {
        int countBefore =dbHelper.getTotalNumBooks();
        dbHelper.removeBook("Daedalus");
        dbHelper.addOneBook("Daedalus","menna",1);
        int countAfter =dbHelper.getTotalNumBooks();
        assertEquals(countBefore, countAfter);
    }

    @Test
    public void removeAdd2() throws Exception {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        dbHelper.removeBook("Daedalus");
        Cursor resultSet_bf = db.rawQuery("Select * from Book where title=?",new String[]{"Daedalus"});
        assertEquals(false, resultSet_bf.moveToFirst());
        dbHelper.addOneBook("Daedalus","menna",1);
        Cursor resultSet_aft = db.rawQuery("Select * from Book where title=?",new String[]{"Daedalus"});
        assertEquals(true, resultSet_aft.moveToFirst());
    }

    @Test
    public void addRemove1() throws Exception {
        int countBefore =dbHelper.getTotalNumBooks();
        dbHelper.addOneBook("Test","Test",1);
        dbHelper.removeBook("Test");
        int countAfter =dbHelper.getTotalNumBooks();
        assertEquals(countBefore, countAfter);
    }

    @Test
    public void addRemove2() throws Exception {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        dbHelper.addOneBook("Test","Test",1);
        Cursor resultSet_bf = db.rawQuery("Select * from Book where title=?",new String[]{"Test"});
        assertEquals(true, resultSet_bf.moveToFirst());
        dbHelper.removeBook("Test");
        Cursor resultSet_aft = db.rawQuery("Select * from Book where title=?",new String[]{"Test"});
        assertEquals(false, resultSet_aft.moveToFirst());
    }

//    @Test
//    public void removeAddSearch() throws Exception {
//        ArrayList<String> book1 = dbHelper.findBook("West Side Story","Burton D.Fisher");
//        assertEquals("Relational Masks", book1.get(0));
//
//        dbHelper.removeBook("Relational Masks");
//        ArrayList<String> book2 = dbHelper.findBook("West Side Story","Burton D.Fisher");
//        assertEquals(0, book2.size());
//        dbHelper.addOneBook("West Side Story","Burton D.Fisher",1);
//        ArrayList<String> book3 = dbHelper.findBook("West Side Story","Burton D.Fisher");
//        assertEquals("Relational Masks", book3.get(0));
//    }


}