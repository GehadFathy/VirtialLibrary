package com.example.library.sw_library;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;

import com.example.library.sw_library.Database.DBHelper;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LoginTest {

    private DBHelper dbHelper;

    @Before
    public void setUp(){
        dbHelper = DBHelper.getInstance(InstrumentationRegistry.getTargetContext());
        dbHelper.fillAdmin();
    }

    @Test
    public void fillAdmin1() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor resultSet = db.rawQuery("Select * from Admin where name=?",new String[]{"Asmaa"});
        assertEquals(true, resultSet.moveToFirst());
    }

    @Test
    public void fillAdmin2() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor resultSet = db.rawQuery("Select * from Admin where name=?",new String[]{"xxx"});
        assertEquals(false, resultSet.moveToFirst());
    }

    @Test
    public void fillAdmin3() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        dbHelper.onUpgrade(db, 0, 1);
        Cursor resultSet = db.rawQuery("Select * from Admin where name=?",new String[]{"Asmaa"});
        assertEquals(false, resultSet.moveToFirst());
    }


    @Test
    public void validMail1() {
        assertEquals(true, dbHelper.validMail("sh.asmosama@hotmail.com"));
    }

    @Test
    public void validMail2() {
        assertEquals(false, dbHelper.validMail("asmaa@hotmail.com"));
    }

    @Test
    public void validMail3() {
        assertEquals(false, dbHelper.validMail(""));
    }

    @Test
    public void validPW1() {
        assertEquals(true, dbHelper.validPW("amira@hotmail.com","DontEnter"));
    }

    @Test
    public void validPW2() {
        assertEquals(false, dbHelper.validPW("amira@hotmail.com","Dont"));
    }

    @Test
    public void validPW3() {
        assertEquals(false, dbHelper.validPW("xxx@hotmail.com","DontEnter"));
    }

    @Test
    public void validPW4() {
        assertEquals(false, dbHelper.validPW("amira@hotmail.com",""));
    }

    @Test
    public void validPW5() {
        assertEquals(false, dbHelper.validPW("",""));
    }
}