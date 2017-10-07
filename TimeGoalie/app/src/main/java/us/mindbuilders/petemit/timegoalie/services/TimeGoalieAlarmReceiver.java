package us.mindbuilders.petemit.timegoalie.services;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;

import us.mindbuilders.petemit.timegoalie.BaseApplication;
import us.mindbuilders.petemit.timegoalie.R;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.GoalEntry;
import us.mindbuilders.petemit.timegoalie.data.InsertNewGoalEntry;
import us.mindbuilders.petemit.timegoalie.data.TimeGoalieContract;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieDateUtils;

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
        Cursor cursor =context.getContentResolver().query(TimeGoalieContract.buildGetaGoalByIdUri(id),
                null,
                null,
                null,
                null);
        if (cursor != null) {
            ArrayList<Goal> goals = Goal.createGoalListFromCursor(cursor);
            if (goals.get(0) != null) {
                Goal goal = goals.get(0);
                Cursor goalEntryCursor = context.getContentResolver().query(
                        TimeGoalieContract.buildGetAGoalEntryByGoalId(goal.getGoalId()),
                        null,
                        null,
                        new String[]{TimeGoalieDateUtils.getSqlDateString()},
                        null);

                if (goalEntryCursor != null){
                    goalEntryCursor.moveToFirst();
                    GoalEntry goalEntry = new GoalEntry();
                    goalEntry.setDate(goalEntryCursor.getString(goalEntryCursor.getColumnIndex(
                            TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_DATETIME
                    )));
                    goalEntry.setGoal_id(goal.getGoalId());
                    goalEntry.setSecondsElapsed((int)goal.getGoalSeconds(),true);
                    new InsertNewGoalEntry(context).execute(goalEntry);

                }
            }
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
