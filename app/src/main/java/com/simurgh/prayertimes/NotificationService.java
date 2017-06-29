package com.simurgh.prayertimes;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by moshe on 29/06/2017.
 */

public class NotificationService extends BroadcastReceiver{

    public static String NOTIFICATION_SOUND = "notificationSound";
    public static String NOTIFICATION_NAME = "notificationName";
    public static String NOTIFICATION_BODY = "notificationBody";
    Uri path;


    @Override
    public void onReceive(Context context, Intent intent) {



        showNotification(context, intent);

    }

    private void showNotification(Context context, Intent intent) {
        Log.i("notification", "visible");
        int id = intent.getIntExtra(NOTIFICATION_SOUND, 0);


        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, NotificationService.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_notification_white)
                        .setContentTitle(intent.getStringExtra(NOTIFICATION_NAME))
                        .setContentText("Худованд кабул кунад.");
        mBuilder.setContentIntent(contentIntent);

        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        switch (id){
            case 0:// default sound
                mBuilder.setDefaults(Notification.DEFAULT_SOUND);
                mNotificationManager.notify(1, mBuilder.build());
                Log.e("notificationInfo","default");
                break;
            case 1:// adhan sound
                mBuilder.setSound(Uri.parse("android.resource://com.simurgh.prayertimes/raw/solemn"));
                mNotificationManager.notify(1, mBuilder.build());
                Log.e("notificationInfo","adhan");
                break;
            case 2:// silent
                mNotificationManager.notify(1, mBuilder.build());
                Log.e("notificationInfo","silent");
                break;
            case 3:// don't notify
                Log.e("notificationInfo","No Notification");
                break;

        }

    }
}
