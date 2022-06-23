package com.dev175.pebble.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.dev175.pebble.Fragment.HomeFragment;
import com.dev175.pebble.Model.BluetoothConnection;
import com.dev175.pebble.R;
import com.dev175.pebble.SessionManagement;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

    }

    public void showHomeActivity(View view) {
        Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void logout(View view) {
        SessionManagement sessionManagement = new SessionManagement(SettingsActivity.this);
        sessionManagement.removeSession();
        //Removing static variables
        HomeActivity.selectedDeviceMAC="";
        HomeActivity.selectedDeviceState =false;

       try {
           HomeFragment.bluetoothConnection.mConnectedThread.cancel();

        }
       catch (Exception e)
       {
           Log.e(TAG, "logout: "+e.getMessage() );
       }

        Intent intent = new Intent(SettingsActivity.this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}