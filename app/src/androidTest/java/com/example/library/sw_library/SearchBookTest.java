package com.example.library.sw_library;

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


public class SearchBookTest {
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
    public void searchBookWithTitle() throws Exception {
        assertEquals("Testament", dbHelper.findBook("Testament", "").get(0));
    }
    @Test
    public void searchBookWithTitleSize() throws Exception {
        assertEquals(1, dbHelper.findBook("Testament", "").size());
    }
    @Test
    public void searchBooKWithNotExistTitle() throws Exception {
        assertEquals(0, dbHelper.findBook("NotExist", "").size());
    }


    @Test
    public void searchBookWithAuthor() throws Exception {
        assertEquals("Rigby on our way to English", dbHelper.findBook("", "Various").get(0));
    }
    @Test
    public void searchBookWithAuthorSize() throws Exception {
        assertEquals(1, dbHelper.findBook("", "Various").size());
    }

    @Test
    public void searchBooKWithNotExistAuthor() throws Exception {
        assertEquals(0, dbHelper.findBook("", "NotExist").size());
    }
    @Test
    public void searchBookWithAuthors() throws Exception {
        ArrayList<String> titles = dbHelper.findBook("", "Various, Benson Bobrick, Carly Phillips");
        assertEquals(3, titles.size());
        assertEquals("Testament", titles.get(0));
        assertEquals("Dare to Love", titles.get(1));
        assertEquals("Rigby on our way to English", titles.get(2));
    }

    @Test
    public void searchBookEmpty() throws Exception {
        assertEquals(0, dbHelper.findBook("", "").size());
    }

    @Test
    public void searchBookWithTitleAuthor() throws Exception {
        ArrayList<String> titles = dbHelper.findBook("Rigby on our way to English", "Various");
        assertEquals(1, titles.size());
        assertEquals("Rigby on our way to English", titles.get(0));
    }


    @Test
    public void searchBookWithTitleAuthor2() throws Exception {
        ArrayList<String> titles = dbHelper.findBook("Rigby on our way to English", "Carly Phillips");
        assertEquals(0, titles.size());
    }

    @Test
    public void searchBookWithTitleAuthor3() throws Exception {
        ArrayList<String> titles = dbHelper.findBook("Rigby on our way to English", "Carly Phillips, Various");
        assertEquals(1, titles.size());
        assertEquals("Rigby on our way to English", titles.get(0));
    }
}
