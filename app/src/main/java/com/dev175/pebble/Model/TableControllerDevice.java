package com.dev175.pebble.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;


public class TableControllerDevice extends DatabaseHandler{

    public TableControllerDevice(Context context) {
        super(context);
    }

    public boolean create(Device device) {
        ContentValues values = new ContentValues();
        values.put("name", device.getName());
        values.put("address", device.getAddress());
        SQLiteDatabase db = this.getWritableDatabase();
        boolean createSuccessful = db.insert("pairedDevice", null, values) > 0;
        db.close();
        return createSuccessful;
    }

    public ArrayList<Device> read() {
        ArrayList<Device> recordsList = new ArrayList<Device>();
        String sql = "SELECT * FROM pairedDevice";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {

                String name = cursor.getString(cursor.getColumnIndex("name"));
                String address = cursor.getString(cursor.getColumnIndex("address"));
                Device device = new Device();
                device.setName(name);
                device.setAddress(address);
                recordsList.add(device);

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return recordsList;
    }

    public boolean delete(String address) {
        boolean deleteSuccessful = false;
        SQLiteDatabase db = this.getWritableDatabase();
        deleteSuccessful = db.delete("pairedDevice", "address ='" + address + "'", null) > 0;
        db.close();
        return deleteSuccessful;
    }

}
