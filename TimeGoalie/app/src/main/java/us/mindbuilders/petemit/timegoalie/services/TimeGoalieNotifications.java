package us.mindbuilders.petemit.timegoalie.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    public static final int NOTIFICATIONID = 50000;
    public static final int RESUME_ID = 1;
    public static final int STOP_ID = 2;
    public static final int ONE_MINUTE_NOTIFICATIONID = 4000;
    private static final int CANCELLATION_TIMEOUT = 2000;
    private static boolean isSilent;
    private static String selection;
    private static boolean notificationsDisabled;
    private static NotificationManager notifyMgr;
    private static Uri soundUri;

    public static void setUpNotification(Context context) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        isSilent = sharedPreferences.getBoolean("pref_silent_mode", false);
        selection = sharedPreferences.getString("pref_audio_mode", vibrationPref.soundandvibrate.name());
        notificationsDisabled = sharedPreferences.
                getBoolean("pref_disable_app_notifications", false);
        notifyMgr = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.fun_charm);
    }

    public static void createNotification(Context context, Intent intent, String injectedMessage) {

        setUpNotification(context);
        int goal_id;
        if (!notificationsDisabled) {
            String message = "";
            message = intent.getStringExtra(context.getString(R.string.goal_title_key));
            goal_id = intent.getIntExtra(context.getString(R.string.goal_id_key),0);
            if (message == null) {
                message = context.getString(R.string.goal_finished);
            }

            int notification_id = goal_id;
            int resume_id = Integer.valueOf("" + RESUME_ID + NOTIFICATIONID);

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context, null)
                            .setSmallIcon(R.drawable.ic_goalie_with_ball_grey)
                            .setContentTitle(message)
                            .setTimeoutAfter(CANCELLATION_TIMEOUT)
                            .setAutoCancel(true)
                            .addAction(R.drawable.ic_goalie_with_ball_grey, "Dismiss", NotificationActivity.getDismissIntent(notification_id,context))
                            .addAction(R.drawable.ic_goalie_with_ball_grey, "Resume Goal",NotificationActivity.getResumeIntent(notification_id,context,goal_id))
                            .setContentText(injectedMessage);

            setSoundForBuilder(builder);
            setContentIntentForBuilder(context, builder);

            notifyMgr.notify(notification_id, builder.build());
        }
    }

    public static void createOneMinuteNotification(Context context, Intent intent, String injectedMessage) {
        setUpNotification(context);
        int goal_id;
        if (!notificationsDisabled) {
            String message = "";
            message = intent.getStringExtra(context.getString(R.string.goal_title_key));
            goal_id = intent.getIntExtra(context.getString(R.string.goal_id_key),0);
            if (message == null) {
                message = context.getString(R.string.goal_finished);
            }

            int notification_id = goal_id;
            int stop_id = Integer.valueOf("" + STOP_ID + goal_id);

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context, null)
                            .setSmallIcon(R.drawable.ic_goalie_with_ball_grey)
                            .setContentTitle(message)
                            .setTimeoutAfter(CANCELLATION_TIMEOUT)
                            .setAutoCancel(true)
                            .addAction(R.drawable.ic_goalie_with_ball_grey, "Stop Timer", NotificationActivity.getStopIntent(notification_id,context,goal_id))
                            .addAction(R.drawable.ic_goalie_with_ball_grey, "Keep Timer Going", NotificationActivity.getDismissIntent(notification_id,context))
                            .setContentText(injectedMessage);

            setSoundForBuilder(builder);
            setContentIntentForBuilder(context, builder);
            notifyMgr.notify(notification_id, (builder.build()));
        }
    }


    public static NotificationCompat.Builder setContentIntentForBuilder(Context context,
                                                                        NotificationCompat.Builder builder) {
        Intent resultIntent = new Intent(context, GoalListActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(pendingIntent);
        return builder;
    }

    public static NotificationCompat.Builder setSoundForBuilder(NotificationCompat.Builder builder) {
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
        }
        return builder;
    }

    public static void subscribeFromPref(boolean isSubscribed) {

        if (isSubscribed) {
            FirebaseMessaging.getInstance().subscribeToTopic("dailyNotification");
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("dailyNotification");
        }
    }

    public enum vibrationPref {
        vibrate, sound, soundandvibrate
    }
}
