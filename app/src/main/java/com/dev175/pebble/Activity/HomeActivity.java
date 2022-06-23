package com.dev175.pebble.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.dev175.pebble.Fragment.AlertFragment;
import com.dev175.pebble.Fragment.HomeFragment;
import com.dev175.pebble.Fragment.NotificationsFragment;

import com.dev175.pebble.Model.Notification;
import com.dev175.pebble.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener  {

    public static  String selectedDeviceMAC = "";
    public static  boolean selectedDeviceState = false;
    private static final String TAG = "HomeActivity";
    private FragmentManager manager;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.setItemIconTintList(null);

        manager = getSupportFragmentManager();
        manager.addOnBackStackChangedListener(this);

        addHomeFragment();
    }
    //Notification
    public void showNotification(Context ctx, Notification notification) {

        //Notification
          final String CHANNEL_ID = "com.dev175.pebble";
          final int NOTIFICATION_ID = 175;

        //creating notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, CHANNEL_ID);
        //icon
        builder.setSmallIcon(R.drawable.notification);
        //title
        builder.setContentTitle(notification.getTitle());
        //description
        builder.setContentText(notification.getMessage());
        //set priority
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        //set sound
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(sound);
        try{

            //Resume or restart the app (same as the launcher click)
            Intent resultIntent = new Intent(this, HomeActivity.class);
            resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            resultIntent.setAction(Intent.ACTION_MAIN);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
        }

        catch (Exception e)
        {
            Log.e(TAG, "showNotification: "+e.getMessage() );
        }
        builder.setAutoCancel(true);

        //notification manager
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(ctx);
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());


    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId())
            {
                case R.id.nav_home:
                {
                   selectedFragment = new HomeFragment();
                    break;
                }
                case R.id.nav_notification:
                {
                   selectedFragment = new NotificationsFragment();
                    break;
                }
                case R.id.nav_alert:
                {
                 selectedFragment = new AlertFragment();
                 break;
                }
            }
            try
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.container,selectedFragment).commit();
            } catch (Exception e)
            {
                Log.e(TAG, "onNavigationItemSelected: "+e.getMessage() );
            }
            return true;
        }
    };

    @Override
    public void onBackStackChanged()
    {
        int length = manager.getBackStackEntryCount();
        StringBuilder msg = new StringBuilder("");

        for (int i=length-1;i>=0;i--)
        {
            msg.append("Index ").append(i).append(" : ");
            msg.append(manager.getBackStackEntryAt(i).getName());
            msg.append("\n");
        }

        Log.i(TAG, "onBackStackChanged: Count = "+manager.getBackStackEntryCount());
        Log.i(TAG, "\n" + msg.toString() + "\n");
    }

    private   void addHomeFragment() {
        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container,homeFragment,"homeFragment");
        transaction.commit();
    }


    @Override
    public void onBackPressed() {

        manager.popBackStack();
        super.onBackPressed();
    }

    public void showSettingsActivity(View view) {
        startActivity(new Intent(HomeActivity.this,SettingsActivity.class));
    }

}