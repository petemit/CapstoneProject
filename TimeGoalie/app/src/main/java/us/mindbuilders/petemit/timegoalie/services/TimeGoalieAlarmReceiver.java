package us.mindbuilders.petemit.timegoalie.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;

import us.mindbuilders.petemit.timegoalie.BaseApplication;
import us.mindbuilders.petemit.timegoalie.R;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.GoalEntry;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.TimeGoalieAlarmObject;
import us.mindbuilders.petemit.timegoalie.data.GetSuccessfulGoalCount;
import us.mindbuilders.petemit.timegoalie.data.InsertNewGoalEntry;
import us.mindbuilders.petemit.timegoalie.data.TimeGoalieContract;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieAlarmManager;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieDateUtils;
import us.mindbuilders.petemit.timegoalie.widget.TimeGoalieWidgetProvider;

/**
 * Created by Peter on 9/29/2017.
 */

public class TimeGoalieAlarmReceiver extends BroadcastReceiver {
    public static final String GOAL_FINISHED =
            "us.mindbuilders.petemit.timegoalie.GOAL_FINISHED";
    public static final String GOAL_ONE_MINUTE_WARNING =
            "us.mindbuilders.petemit.timegoalie.GOAL_ONE_MINUTE_WARNING";
    public static final String SECONDLY_GOAL_UPDATE_ENTRY =
            "us.mindbuilders.petemit.timegoalie.SECONDLY_GOAL_UPDATE_ENTRY";
    public static final long SECONDLY_FREQUENCY = 1000;
    public static final int SECONDLY_ID = 10101;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Mindbuilders", "Alarm Finished!");
        String action = intent.getAction();
        if (!action.equals(SECONDLY_GOAL_UPDATE_ENTRY)) {
            Goal goal = null;
            GoalEntry goalEntry = null;
            int id = intent.getIntExtra(context.getString(R.string.goal_id_key), -1);
            Cursor cursor = context.getContentResolver().query(TimeGoalieContract.buildGetaGoalByIdUri(id),
                    null,
                    null,
                    null,
                    null);

            if (cursor != null) {
                try {
                    ArrayList<Goal> goals = Goal.createGoalListFromCursor(cursor);
                    if (goals != null && goals.size() > 0) {
                        goal = goals.get(0);
                        Cursor goalEntryCursor = context.getContentResolver().query(
                                TimeGoalieContract.buildGetAGoalEntryByGoalId(goal.getGoalId()),
                                null,
                                null,
                                new String[]{TimeGoalieDateUtils.getSqlDateString()},
                                null);

                        if (goalEntryCursor != null && goalEntryCursor.getCount() > 0) {
                            goalEntryCursor.moveToFirst();
                            goalEntry = new GoalEntry(goalEntryCursor.getLong(
                                    goalEntryCursor.getColumnIndex(TimeGoalieContract.GoalEntries._ID)
                            ), goal.getGoalId(), goalEntryCursor.getString(
                                    goalEntryCursor.getColumnIndex(
                                            TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_DATETIME)));
                            goalEntry.setGoalAugment(goalEntryCursor.getInt(goalEntryCursor.getColumnIndex(
                                    TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_GOALAUGMENT
                            )));
                            goalEntry.setSecondsElapsed(goalEntryCursor.getInt(goalEntryCursor.getColumnIndex(
                                    TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_SECONDSELAPSED
                            )));
                            goalEntry.setRunning(goalEntryCursor.getInt(goalEntryCursor.getColumnIndex(
                                    TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_ISRUNNING
                            )));
                            goalEntry.setTargetTime(goalEntryCursor.getLong(goalEntryCursor.getColumnIndex(
                                    TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_TARGETTIME
                            )));
                            goalEntry.setHasFinished(goalEntryCursor.getInt(goalEntryCursor.getColumnIndex(
                                    TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_ISFINISHED
                            )));
                            goalEntry.setHasSucceeded(goalEntryCursor.getInt(goalEntryCursor.getColumnIndex(
                                    TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_SUCCEEDED
                            )));

                        }
                    }
                } finally {
                    cursor.close();
                }
            }

            if (goal != null && goalEntry != null) {
                switch (action) {
                    case GOAL_FINISHED:


                        goalEntry.setHasFinished(true);
                        if (goalEntry.isHasFinished()) {
                            if (goal.getGoalTypeId() == 0) { //goal to encourage
                                TimeGoalieNotifications.createNotification(context, intent, "You're done with your goal!");
                                if (goalEntry != null) {
                                    goalEntry.setHasSucceeded(1);
                                }
                            } else if (goal.getGoalTypeId() == 1) {
                                TimeGoalieNotifications.createNotification(context, intent, "Oops! Ran out of time.  Maybe next time.");
                                if (goalEntry != null) {
                                    goalEntry.setHasSucceeded(0);
                                }
                            }
                        }

//                if (goalEntry != null) {
//                    goalEntry.setSecondsElapsed((int) goal.getGoalSeconds() + goalEntry.getGoalAugment(), true);
//                }
                        if (id != -1) {
                            //  BaseApplication.getTimeGoalieAlarmObjectById(id).setRunning(false);
                            //BaseApplication.getTimeGoalieAlarmObjectById(id).setHasFinished(true);

                        }

                        break;
                    case GOAL_ONE_MINUTE_WARNING:
                        TimeGoalieNotifications.createNotification(context, intent, "Hurry up! " +
                                " You have only one minute left");
                        id = intent.getIntExtra(context.getString(R.string.goal_id_key), -1);
                        if (id != -1) {
                            //  BaseApplication.getTimeGoalieAlarmObjectById(id).setRunning(false);
                            if (goalEntry.getDate() != null) {
                                if (BaseApplication.getTimeGoalieAlarmObjectById(id,
                                        goalEntry.getDate()) != null) {
                                    BaseApplication.getTimeGoalieAlarmObjectById(id).setHasBeenWarned(true);
                                }
                            }
                        }

                        break;
                }
            }
            if (goalEntry != null)

            {
                new InsertNewGoalEntry(context).execute(goalEntry);
            }

        }// if secondly
        else {
            long diff = TimeGoalieDateUtils.getCurrentTimeInMillis() -
                    BaseApplication.getLastTimeSecondUpdated();
            Log.e("mindy", diff+" diff");
            if (diff >= 1000 ) {
                Cursor cursor = context.getContentResolver().query
                        (TimeGoalieContract.getRunningGoalEntriesThatHaveGoalEntryForToday(),
                                null,
                                null,
                                new String[]{TimeGoalieDateUtils.
                                        getSqlDateString(BaseApplication.getActiveCalendarDate())},
                                null);

                if (cursor != null && cursor.getCount() > 0) {
                    ArrayList<GoalEntry> goalEntries = GoalEntry.makeGoalEntryListFromCursor(cursor);
                    for (GoalEntry goalEntry : goalEntries
                            ) {

                        if (goalEntry.isRunning()) {

                            goalEntry.addSecondElapsed();
                            //    goalEntry.setNeedsSecondsUpdate(true);
//
//                    AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//                    alarmMgr.setExact(AlarmManager.RTC_WAKEUP, 1000, TimeGoalieAlarmReceiver.createSecondlyTimeGoaliePendingIntent(context,
//                            TimeGoalieAlarmReceiver.
//                                    createEverySecondDbUpdateAlarmIntent(context,
//                                            (int)goal.getGoalId()),(int)goal.getGoalId()));



                            if (BaseApplication.getGoalActivityListListener() != null) {
                                BaseApplication.getGoalActivityListListener().notifyChanges(goalEntry);
                            }

                            new InsertNewGoalEntry(context).execute(goalEntry);

                            Intent updateWidgetintent = new Intent(context, TimeGoalieWidgetProvider.class);
                            updateWidgetintent.setAction(TimeGoalieWidgetProvider.ACTION_GET_GOALS_FOR_TODAY);
                            context.sendBroadcast(updateWidgetintent);

                            Log.e("alarm", goalEntry.getGoal_id() + " : " + goalEntry.getSecondsElapsed() + "");

                            BaseApplication.setLastTimeSecondUpdated(TimeGoalieDateUtils.getCurrentTimeInMillis());

                        }


                    }//end for


                }
                TimeGoalieAlarmManager.setTimeGoalAlarm(SECONDLY_FREQUENCY, context, null,
                        TimeGoalieAlarmReceiver.createSecondlyTimeGoaliePendingIntent(context,
                                TimeGoalieAlarmReceiver.
                                        createEverySecondDbUpdateAlarmIntent(context,
                                                SECONDLY_ID),0));
            }///end if second has elapsed.
            else {
                //catch up
                TimeGoalieAlarmManager.setTimeGoalAlarm(1000-diff, context, null,
                        TimeGoalieAlarmReceiver.createSecondlyTimeGoaliePendingIntent(context,
                                TimeGoalieAlarmReceiver.
                                        createEverySecondDbUpdateAlarmIntent(context,
                                                SECONDLY_ID),0));
            }



//

        }//else SECONDLY

    }

    public static void cancelSecondlyAlarm(Context context, Goal goal) {
//        PendingIntent secondlyPi = TimeGoalieAlarmReceiver.createSecondlyTimeGoaliePendingIntent(
//                context, TimeGoalieAlarmReceiver.createEverySecondDbUpdateAlarmIntent(context,
//                        (int)goal.getGoalId()),(int)goal.getGoalId());
//        TimeGoalieAlarmManager.cancelTimeGoalAlarm(context, secondlyPi);
    }

    public static Intent createAlarmDoneTimeGoalieAlarmIntent(Context context, String message, int goal_id) {
        Intent intent = new Intent(context, TimeGoalieAlarmReceiver.class);
        intent.putExtra(context.getString(R.string.goal_id_key), goal_id);
        intent.setAction(GOAL_FINISHED);
        intent.putExtra(context.getString(R.string.goal_title_key), message);
        return intent;
    }

    public static Intent createOneMinuteWarningTimeGoalieAlarmIntent
            (Context context, String message, int goal_id) {
        Intent intent = new Intent(context, TimeGoalieAlarmReceiver.class);
        intent.putExtra(context.getString(R.string.goal_id_key), goal_id);
        intent.setAction(GOAL_ONE_MINUTE_WARNING);
        intent.putExtra(context.getString(R.string.goal_title_key), message);
        return intent;
    }

    public static Intent createEverySecondDbUpdateAlarmIntent(
            Context context, int goal_id) {
        Intent intent = new Intent(context, TimeGoalieAlarmReceiver.class);
        intent.putExtra(context.getString(R.string.goal_id_key), goal_id);
        intent.setAction(SECONDLY_GOAL_UPDATE_ENTRY);
        return intent;
    }


    public static PendingIntent createSecondlyTimeGoaliePendingIntent(Context context, Intent intent, int goal_id) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, SECONDLY_ID,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return pendingIntent;
    }


    public static PendingIntent createTimeGoaliePendingIntent(Context context, Intent intent, int goal_id) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, goal_id,
                intent, 0);
        return pendingIntent;
    }
}
