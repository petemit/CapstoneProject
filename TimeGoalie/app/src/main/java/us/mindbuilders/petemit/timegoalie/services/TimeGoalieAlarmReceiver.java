package us.mindbuilders.petemit.timegoalie.services;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import us.mindbuilders.petemit.timegoalie.BaseApplication;
import us.mindbuilders.petemit.timegoalie.R;

/**
 * Created by Peter on 9/29/2017.
 */

public class TimeGoalieAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Mindbuilders", "Alarm Finished!");
        TimeGoalieNotifications.createNotification(context, intent);
        int id = -1;
        id = intent.getIntExtra(context.getString(R.string.goal_id_key),-1);
        if (id != -1) {
          //  BaseApplication.getTimeGoalieAlarmObjectById(id).setRunning(false);
            BaseApplication.getTimeGoalieAlarmObjectById(id).setHasFinished(true);
        }
    }

    public static Intent createTimeGoalieAlarmIntent(Context context, String message, int goal_id){
        Intent intent = new Intent(context, TimeGoalieAlarmReceiver.class);
        intent.putExtra(context.getString(R.string.goal_id_key),goal_id);
        intent.setAction("us.mindbuilders.petemit.timegoalie.GOAL_FINISHED");
        intent.putExtra(context.getString(R.string.goal_title_key),message);
        return intent;
    }

    public static PendingIntent createTimeGoaliePendingIntent(Context context, int goal_id, String goal_title) {
        Intent intent = createTimeGoalieAlarmIntent(context, goal_title, goal_id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, goal_id,
                intent, 0);
        return pendingIntent;
    }
}
