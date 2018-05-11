package com.example.library.sw_library;


import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.example.library.sw_library.Database.DBHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
    @Test
    public void testPreConditions() {
        assertNotNull(dbHelper);
    }



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


}