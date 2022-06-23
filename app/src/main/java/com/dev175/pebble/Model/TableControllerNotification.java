package com.dev175.pebble.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class TableControllerNotification extends DatabaseHandler {

    public TableControllerNotification(Context context) {
        super(context);
    }

    public boolean create(Notification notification) {
        ContentValues values = new ContentValues();
        values.put("title", notification.getTitle());
        values.put("message", notification.getMessage());
        SQLiteDatabase db = this.getWritableDatabase();
        boolean createSuccessful = db.insert("notification", null, values) > 0;
        db.close();
        return createSuccessful;
    }

    public ArrayList<Notification> readNotifications() {
        ArrayList<Notification> recordsList = new ArrayList<Notification>();
        String sql = "SELECT * FROM notification ORDER BY id DESC LIMIT 15";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {

                String title = cursor.getString(cursor.getColumnIndex("title"));
                String message = cursor.getString(cursor.getColumnIndex("message"));
                Notification notification = new Notification();
                notification.setTitle(title);
                notification.setMessage(message);
                recordsList.add(notification);

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return recordsList;
    }

}
