package us.mindbuilders.petemit.timegoalie.services;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import us.mindbuilders.petemit.timegoalie.BaseApplication;

public class NotificationActivity extends Activity {

    public static final String NOTIFICATION_ID = "NOTIFICATION_ID";
    public static final String DISMISS_ACTION = "DISMISS";
    public static final String RESUME_ACTION = "RESUME";
    public static final String STOP_ACTION = "STOP";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int goalId = 0;
        String action = getIntent().getStringExtra("action");

        switch (action) {
            case DISMISS_ACTION:
                cancelNotification();
                break;
            case RESUME_ACTION:
                goalId = getIntent().getIntExtra("goal_id",0);
                BaseApplication.getGoalEntryController().resumeGoalAfterFinishedWithElapsedTime(goalId);
                cancelNotification();
                break;
            case STOP_ACTION:
                goalId = getIntent().getIntExtra("goal_id",0);
                BaseApplication.getGoalEntryController().stopGoalById(goalId);
                cancelNotification();
                break;
            default:
                cancelNotification();
                break;
        }

    }



    public void cancelNotification() {
        NotificationManager manager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(getIntent().getIntExtra(NOTIFICATION_ID, -1));
        finish(); // since finish() is called in onCreate(), onDestroy() will be called immediately
    }

    public static PendingIntent getDismissIntent(int notificationId, Context context) {
        Intent intent = new Intent(context, NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(NOTIFICATION_ID, notificationId);
        intent.putExtra("action", DISMISS_ACTION);
        PendingIntent dismissIntent = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return dismissIntent;
    }

    public static PendingIntent getResumeIntent(int notificationId, Context context, int goalId) {
        Intent intent = new Intent(context, NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(NOTIFICATION_ID, notificationId);
        intent.putExtra("action", RESUME_ACTION);
        intent.putExtra("goal_id", goalId);
        PendingIntent resumeIntent = PendingIntent.getActivity(context, notificationId+40000, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return resumeIntent;
    }

    public static PendingIntent getStopIntent(int notificationId, Context context, int goalId) {
        Intent intent = new Intent(context, NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(NOTIFICATION_ID, notificationId);
        intent.putExtra("action", STOP_ACTION);
        intent.putExtra("goal_id", goalId);
        //offset the notification id to keep it unique but allow it to be cancelled
        PendingIntent stopIntent = PendingIntent.getActivity(context, notificationId+50000, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return stopIntent;
    }

}