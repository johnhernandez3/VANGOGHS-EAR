package com.example.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class MusicDataBase extends SQLiteOpenHelper {

    //Assign variable
    public static final String DB_NAME = "Music2.db";
    public static final String TABLE_NAME = "tablature_table";
    public static final String TABLE_NAME1 = "chord_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "SONGNAME";
    public static final String CCOL_1 = "CID";
    public static final String CCOL_2 = "CHORDS";


    /**
     * Represents instance of class using context
     * @param context allows created instance to understand what is happening and connect to the database
     */
    public MusicDataBase (Context context) { super(context, DB_NAME, null, 1); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME1 + " (CID INTEGER PRIMARY KEY, CHORDS TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY, SONGNAME TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME1);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Inserts data into the desired table created in the database
     * @param DATA String representing the value inserted into the table
     * @param i decides which table is having data inserted
     * @return is true if the data was inserted and false if it was not
     */
    public boolean insertData(String DATA, int i) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        switch(i) {
            case 1:
                contentValues.put(COL_2, DATA);
                long res = db.insert(TABLE_NAME, null, contentValues);
                if(res == -1) {
                    return false;
                }
                else {
                    return true;
                }
            case 2:
                contentValues.put(CCOL_2, DATA);
                long res1 = db.insert(TABLE_NAME1, null, contentValues);
                if(res1 == -1) {
                    return false;
                }
                else {
                    return true;
                }
            default:
                return false;
        }



    }

    /**
     * Acts as a select command that receives all of the non-deleted values from the tablature table as a List object
     * @return returns List object that has every value currently in the table
     */
    public List getAllID() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> al = new ArrayList<>();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME + "\n", null);
        if(res.moveToFirst())
            do {
                al.add(res.getString(res.getColumnIndex(COL_1)));
                al.add(res.getString(res.getColumnIndex(COL_2)));
            } while(res.moveToNext());
        return al;
    }

    /**
     * Acts as a select command that receives all of the non-deleted values from the chord table as a List object
     * @return returns List object that has every value currently in the table
     */
    public List getAllID2() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> al = new ArrayList<>();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME1 + "\n", null);
        if(res.moveToFirst())
            do {
                al.add(res.getString(res.getColumnIndex(CCOL_1)));
                al.add(res.getString(res.getColumnIndex(CCOL_2)));
            } while(res.moveToNext());
        return al;
    }

    /**
     * Represents the delete query on the tablature table for id provided
     * @param id the String id that represents the value on the table that will be deleted
     * @return returns the integer representation of the rows deleted
     */
    public Integer deleteData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "SONGNAME = ?", new String[] {id});
    }

    /**
     * Represents the delete query on the chords table for id provided
     * @param id the String id that represents the value on the table that will be deleted
     * @return returns the integer representation of the rows deleted
     */
    public Integer deleteChordData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME1, "CHORDS = ?", new String[] {id});
    }
}
