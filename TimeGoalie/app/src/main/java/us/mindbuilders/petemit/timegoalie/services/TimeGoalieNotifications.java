package us.mindbuilders.petemit.timegoalie.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v7.preference.PreferenceManager;

import com.google.firebase.messaging.FirebaseMessaging;

import us.mindbuilders.petemit.timegoalie.GoalListActivity;
import us.mindbuilders.petemit.timegoalie.R;

/**
 * Created by Peter on 10/2/2017.
 */

public class TimeGoalieNotifications {
    private static final int NOTIFICATIONID = 1;
    private static final int CANCELLATION_TIMEOUT = 2000;

    public enum vibrationPref {
        vibrate, sound, soundandvibrate
    }

    public static void createNotification(Context context, Intent intent, String injectedMessage) {

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        boolean isSilent = sharedPreferences.getBoolean("pref_silent_mode", false);
        String selection = sharedPreferences.
                getString("pref_audio_mode", vibrationPref.soundandvibrate.name());
        boolean notificationsDisabled = sharedPreferences.
                getBoolean("pref_disable_app_notifications", false);

        if (!notificationsDisabled) {
            String message = "";
            message = intent.getStringExtra(context.getString(R.string.goal_title_key));
            if (message == null) {
                message = context.getString(R.string.goal_finished);
            }
            NotificationManager notifyMgr = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);


            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context, null)
                            .setSmallIcon(R.drawable.ic_goalie_with_ball_grey)
                            .setContentTitle(message)
                            .setTimeoutAfter(CANCELLATION_TIMEOUT)
                            .setAutoCancel(true)
                            .setContentText(injectedMessage);


            if (!isSilent) {

                if (selection.equals(vibrationPref.vibrate.name())) {
                    builder.setDefaults(Notification.DEFAULT_VIBRATE);
                }
                if (selection.equals(vibrationPref.sound.name())) {
                    builder.setSound((soundUri));
                    builder.setVibrate(new long[]{0L});
                }

                if (selection.equals(vibrationPref.soundandvibrate.name())) {
                    builder.setSound((soundUri));
                    builder.setDefaults(Notification.DEFAULT_VIBRATE);
                }
            } else { //might not need anything here

            }

            Intent resultIntent = new Intent(context, GoalListActivity.class);

            PendingIntent pendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            builder.setContentIntent(pendingIntent);

            notifyMgr.notify(1, builder.build());
        }
    }

    public static void subscribeFromPref(boolean isSubscribed) {

        if (isSubscribed) {
            FirebaseMessaging.getInstance().subscribeToTopic("dailyNotification");
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("dailyNotification");
        }
    }
}
