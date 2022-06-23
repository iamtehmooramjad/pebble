package com.dev175.pebble.Model;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.dev175.pebble.Activity.HomeActivity;
import com.dev175.pebble.Activity.SplashActivity;
import com.dev175.pebble.R;


public class Notification extends Application {

    private static final String TAG = "Notification";

    //Notification
    private static final String CHANNEL_ID = "com.dev175.pebble";
    private static final int NOTIFICATION_ID = 175;

    private String title;
    private String message;

    public Notification(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public Notification() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }





    private void createNotificationChannel(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            CharSequence name = "My Notification";
            String description = "My notification description";
            //importance of your notification
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationChannel.setDescription(description);

            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            NotificationManager notificationManager = (NotificationManager)ctx.getSystemService(NOTIFICATION_SERVICE);

            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}

