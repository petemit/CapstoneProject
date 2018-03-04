package us.mindbuilders.petemit.timegoalie.services;


import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Peter on 3/3/2018.
 */

public class TimeGoalieForegroundService extends Service {

    public static String notificationChannel = "channel1";
    public static int foregroundService = 1238;

    @Override
    public void onCreate() {
        NotificationCompat.Builder notificationBuilder = new  NotificationCompat.Builder(this, notificationChannel)
                .setContentTitle("testTitle")
                .setContentText("test Content");
        Notification notification = notificationBuilder.build();
        startForeground(foregroundService, notification);
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

