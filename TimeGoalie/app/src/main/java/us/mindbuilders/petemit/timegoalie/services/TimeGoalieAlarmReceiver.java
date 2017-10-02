package us.mindbuilders.petemit.timegoalie.services;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Peter on 9/29/2017.
 */

public class TimeGoalieAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Mindbuilders", "Alarm Finished!");

    }

    public static Intent createTimeGoalieAlarmIntent(Context context){
        Intent intent = new Intent(context, TimeGoalieAlarmReceiver.class);
        intent.setAction("us.mindbuilders.petemit.timegoalie.GOAL_FINISHED");
        return intent;
    }

    public static PendingIntent createTimeGoaliePendingIntent(Context context, int goal_id) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, goal_id,
                createTimeGoalieAlarmIntent(context), 0);
        return pendingIntent;
    }
}
