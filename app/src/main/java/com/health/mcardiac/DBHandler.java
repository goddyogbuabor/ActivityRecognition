package com.health.mcardiac;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {
    public static final String DB_NAME = "activity.db";
    public static final String TABLE_NAME = "activity_tb";
    public static final String COL1 = "ID";
    public static final String COL2 = "activity";
    public static final String COL3 = "position";
    public static final String COL4 = "X";
    public static final String COL5 = "Y";
    public static final String COL6 = "Z";
    public static final String COL7 = "curtime";
    public static final String COL8 = "curdate";

    public DBHandler(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(" CREATE TABLE " + TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT, Activity TEXT,Position TEXT, X TEXT, Y TEXT, Z TEXT, curtime TEXT, curdate TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
    public void insertData(String curactivity,String curposition, String X, String Y, String Z, Long curtime, String curdate ) {
        SQLiteDatabase dbase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, curactivity);
        contentValues.put(COL3, curposition);
        contentValues.put(COL4, X);
        contentValues.put(COL5, Y);
        contentValues.put(COL6, Z);
        contentValues.put(COL7, curtime);
        contentValues.put(COL8, curdate);
        //contentValues.put(COL_4,marks);
        long result = dbase.insert(TABLE_NAME, null, contentValues);
    }
    public void deleteData(){
        SQLiteDatabase dbase = this.getWritableDatabase();
        //Delete all records of table
        dbase.execSQL("DELETE FROM " + TABLE_NAME);
        //Reset the auto_increment primary key if you needed
        dbase.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = 'activity_tb'");

    }
}
