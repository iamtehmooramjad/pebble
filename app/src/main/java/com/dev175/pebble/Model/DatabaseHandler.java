package com.dev175.pebble.Model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    protected static final String DATABASE_NAME = "PebbleDatabase";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE pairedDevice " +
                "( name TEXT  , " +
                "address TEXT PRIMARY KEY ) ";
        String sql2 = "CREATE TABLE notification " +
                "( id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT  , " +
                "message TEXT ) ";
        db.execSQL(sql);
        db.execSQL(sql2);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS pairedDevice";
        String sql2 = "DROP TABLE IF EXISTS notification";
        db.execSQL(sql);
        db.execSQL(sql2);
        onCreate(db);
    }
}
