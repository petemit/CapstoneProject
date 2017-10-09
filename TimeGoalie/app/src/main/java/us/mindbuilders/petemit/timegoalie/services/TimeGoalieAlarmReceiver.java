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
    public static final String GOAL_FINISHED =
            "us.mindbuilders.petemit.timegoalie.GOAL_FINISHED";
    public static final String GOAL_ONE_MINUTE_WARNING =
            "us.mindbuilders.petemit.timegoalie.GOAL_ONE_MINUTE_WARNING";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Mindbuilders", "Alarm Finished!");
        String action = intent.getAction();
        switch (action) {
            case GOAL_FINISHED:
                TimeGoalieNotifications.createNotification(context, intent, "You're done with your goal!");
                int id = -1;
                id = intent.getIntExtra(context.getString(R.string.goal_id_key), -1);
                if (id != -1) {
                    //  BaseApplication.getTimeGoalieAlarmObjectById(id).setRunning(false);
                    BaseApplication.getTimeGoalieAlarmObjectById(id).setHasFinished(true);
                }
                Cursor cursor = context.getContentResolver().query(TimeGoalieContract.buildGetaGoalByIdUri(id),
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

                        if (goalEntryCursor != null) {
                            goalEntryCursor.moveToFirst();
                            GoalEntry goalEntry = new GoalEntry(goal.getGoalId(), goalEntryCursor.getString(
                                    goalEntryCursor.getColumnIndex(
                                            TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_DATETIME)));
                            goalEntry.setGoalAugment(goalEntryCursor.getInt(goalEntryCursor.getColumnIndex(
                                    TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_GOALAUGMENT
                            )));

                            goalEntry.setSecondsElapsed((int) goal.getGoalSeconds() + goalEntry.getGoalAugment(), true);
                            new InsertNewGoalEntry(context).execute(goalEntry);


                        }
                    }
                }
                break;
            case GOAL_ONE_MINUTE_WARNING:
                TimeGoalieNotifications.createNotification(context, intent, "Hurry up! " +
                        " You have only one minute left");
                break;
        }//end switch
    }

    public static Intent createAlarmDoneTimeGoalieAlarmIntent(Context context, String message, int goal_id) {
        Intent intent = new Intent(context, TimeGoalieAlarmReceiver.class);
        intent.putExtra(context.getString(R.string.goal_id_key), goal_id);
        intent.setAction("us.mindbuilders.petemit.timegoalie.GOAL_FINISHED");
        intent.putExtra(context.getString(R.string.goal_title_key), message);
        return intent;
    }

    public static Intent createOneMinuteWarningTimeGoalieAlarmIntent
            (Context context, String message, int goal_id) {
        Intent intent = new Intent(context, TimeGoalieAlarmReceiver.class);
        intent.putExtra(context.getString(R.string.goal_id_key), goal_id);
        intent.setAction("us.mindbuilders.petemit.timegoalie.GOAL_ONE_MINUTE_WARNING");
        intent.putExtra(context.getString(R.string.goal_title_key), message);
        return intent;
    }

    public static PendingIntent createTimeGoaliePendingIntent(Context context, Intent intent, int goal_id) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, goal_id,
                intent, 0);
        return pendingIntent;
    }
}
