package com.example.library.sw_library;


import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;

import com.example.library.sw_library.Database.DBHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddBookTest {


    private DBHelper dbHelper;

    @Before
    public void setUp(){
        dbHelper = DBHelper.getInstance(InstrumentationRegistry.getTargetContext());
    }
    @Test
    public void testPreConditions() {
        assertNotNull(dbHelper);
    }

    @Test
    public void DummyTest () {
       assertEquals(4, 2 + 2);
    }

    @Test
    public void additionIsCorrectForCategory() throws Exception {
        int countBefore =dbHelper.getNumOfBooksOFCategory(1);
        dbHelper.addOneBook("Test","Test",1);
        int countAfter =dbHelper.getNumOfBooksOFCategory(1);
        assertEquals(countBefore+1, countAfter);
    }
    @Test
    public void additionIsCorrectInTotal() throws Exception {
        int countBefore =dbHelper.getTotalNumBooks();
        dbHelper.addOneBook("Test","Test",1);
        int countAfter =dbHelper.getTotalNumBooks();
        assertEquals(countBefore+1, countAfter);
    }
     @Test
        public void additionIsNull() throws Exception {
            int countBefore =dbHelper.getTotalNumBooks();
            dbHelper.addOneBook(null,null,1);
            int countAfter =dbHelper.getTotalNumBooks();
            assertEquals(countBefore, countAfter);
     }
    @Test
        public void additionIsTrue() throws Exception {

            dbHelper.addOneBook("Book1","TestAuthor",2);
            String author = dbHelper.getAuthorForBook("Book1");

            assertEquals("TestAuthor", author);
     }
}