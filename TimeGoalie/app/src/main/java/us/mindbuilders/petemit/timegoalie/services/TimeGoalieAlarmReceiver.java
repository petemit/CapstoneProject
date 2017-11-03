package us.mindbuilders.petemit.timegoalie.services;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import us.mindbuilders.petemit.timegoalie.BaseApplication;
import us.mindbuilders.petemit.timegoalie.R;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.GoalEntry;
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
    public static final String GOAL_KILLED =
            "us.mindbuilders.petemit.timegoalie.GOAL_KILL_SAFETY";
    public static final long SECONDLY_FREQUENCY = 1000;
    public static final long KILLGOAL_TIMER = 36000;
    public static final int SECONDLY_ID = 10101;
    public static final int KILLGOAL = 1010101;
    private static final int DELAYINTENT_INTERVAL = 7;


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Mindbuilders", "Alarm Finished!");
        new HandleAlarmFinished(context).execute(intent);
    }

    public static void cancelSecondlyAlarm(Context context, Goal goal) {
//        PendingIntent secondlyPi = TimeGoalieAlarmReceiver.createSecondlyTimeGoaliePendingIntent(
//                context, TimeGoalieAlarmReceiver.createEverySecondDbUpdateAlarmIntent(context,
//                        (int)goal.getGoalId()),(int)goal.getGoalId());
//        TimeGoalieAlarmManager.cancelTimeGoalAlarm(context, secondlyPi);
    }

    public static Intent createAlarmDoneTimeGoalieAlarmIntent(Context context,
                                                              String message, int goal_id) {
        Intent intent = new Intent(context, TimeGoalieAlarmReceiver.class);
        intent.putExtra(context.getString(R.string.goal_id_key), goal_id);
        intent.setAction(GOAL_FINISHED);
        intent.putExtra(context.getString(R.string.goal_title_key), message);
        return intent;
    }

    public static Intent createKillGoalTimeGoalieAlarmIntent(Context context,
                                                             String message, int goal_id) {
        Intent intent = new Intent(context, TimeGoalieAlarmReceiver.class);
        intent.putExtra(context.getString(R.string.goal_id_key), goal_id);
        intent.setAction(GOAL_KILLED);
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
            Context context) {
        Intent intent = new Intent(context, TimeGoalieAlarmReceiver.class);
        intent.setAction(SECONDLY_GOAL_UPDATE_ENTRY);
        return intent;
    }


    public static PendingIntent createSecondlyTimeGoaliePendingIntent(Context context,
                                                                      Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, SECONDLY_ID,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return pendingIntent;
    }

    public static PendingIntent createKillGoalSafetyPendingIntent(Context context,
                                                                  Intent intent, int goal_id) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (KILLGOAL + goal_id),
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return pendingIntent;
    }


    public static PendingIntent createTimeGoaliePendingIntent(Context context,
                                                              Intent intent, int goal_id) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, goal_id,
                intent, 0);
        return pendingIntent;
    }

    private class HandleAlarmFinished extends AsyncTask<Intent, Void, Cursor> {
        private Context context;
        private Goal goal;
        private GoalEntry goalEntry;
        private String action;
        Intent intent;
        private int id;

        public HandleAlarmFinished(Context context) {
            this.context = context;
        }

        @Override
        protected Cursor doInBackground(Intent... intents) {
            if (intents[0] != null) {
                intent = intents[0];
            }
            action = intent.getAction();
            int id = intent.getIntExtra(context.getString(R.string.goal_id_key), -1);
            Cursor cursor;

            if (!action.equals(SECONDLY_GOAL_UPDATE_ENTRY)) {
                cursor = context.getContentResolver()
                        .query(TimeGoalieContract.buildGetaGoalByIdUri(id),
                                null,
                                null,
                                null,
                                null);
            } else {
                cursor = context.getContentResolver().query
                        (TimeGoalieContract.getRunningGoalEntriesThatHaveGoalEntryForToday(),
                                null,
                                null,
                                new String[]{TimeGoalieDateUtils.
                                        getSqlDateString(BaseApplication.getActiveCalendarDate())},
                                null);
            }


            return cursor;

        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null) {

                if (!action.equals(SECONDLY_GOAL_UPDATE_ENTRY)) {
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
                        case GOAL_KILLED:
                            goalEntry.setRunning(false);
                            break;
                        case GOAL_FINISHED:


                            goalEntry.setHasFinished(true);
                            if (goalEntry.isHasFinished()) {
                                if (goal.getGoalTypeId() == 0) { //goal to encourage
                                    TimeGoalieNotifications.createNotification(context, intent,
                                            context.getString(R.string.done_with_goal));
                                    if (goalEntry != null) {
                                        goalEntry.setHasSucceeded(1);
                                    }
                                } else if (goal.getGoalTypeId() == 1) {
                                    TimeGoalieNotifications.createNotification(context, intent,
                                            context.getString(R.string.ran_out_of_time));
                                    if (goalEntry != null) {
                                        goalEntry.setHasSucceeded(0);
                                    }
                                }
                            }

                            Intent killGoalIntent = createKillGoalTimeGoalieAlarmIntent(context,
                                    context.getString(R.string.stopping_inactive_goal), (int) goal.getGoalId());

                            PendingIntent killGoalPi = createKillGoalSafetyPendingIntent(context,
                                    killGoalIntent, (int) goal.getGoalId());

                            TimeGoalieAlarmManager.setTimeGoalAlarm(TimeGoalieDateUtils.
                                            createTargetSecondlyCalendarTime(
                                                    (int) KILLGOAL_TIMER / 1000)
                                    , context, null,
                                    killGoalPi);

                            break;
                        case GOAL_ONE_MINUTE_WARNING:
                            TimeGoalieNotifications.createNotification(context, intent,
                                    context.getString(R.string.hurry_up) +
                                            context.getString(R.string.one_minute_warning));
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
                } else { //if a secondly goal

                    Intent updateWidgetintent = new Intent(context,
                            TimeGoalieWidgetProvider.class);
                    //updateWidgetintent.setAction(TimeGoalieWidgetProvider.
                    //        ACTION_GET_GOALS_FOR_TODAY);
                    updateWidgetintent.setAction(TimeGoalieWidgetProvider.
                            ACTION_GET_GOALS_FOR_TODAY);
                    context.sendBroadcast(updateWidgetintent);


                    long diff = TimeGoalieDateUtils.getCurrentTimeInMillis() -
                            BaseApplication.getLastTimeSecondUpdated();
                    Log.e("mindy", diff + " diff");
                    if (!BaseApplication.isHandlerRunning()) {
                        if (diff >= SECONDLY_FREQUENCY) {

                            long secondsElapsed = (int) (Math.floor(diff / 1000));

                            if (cursor.getCount() > 0) {
                                ArrayList<GoalEntry> goalEntries =
                                        GoalEntry.makeGoalEntryListFromCursor(cursor);
                                for (GoalEntry goalEntry : goalEntries
                                        ) {

                                    if (goalEntry.isRunning()) {

                                        if (diff >= 10000000) {
                                            goalEntry.addSecondElapsed();
                                        }
                                        if (diff <= 10000000 && diff >= 0) {
                                            goalEntry.setSecondsElapsed(goalEntry.getSecondsElapsed()
                                                    + (int) secondsElapsed);
                                        }


                                        if (BaseApplication.getGoalActivityListListener() != null) {
                                            BaseApplication.getGoalActivityListListener()
                                                    .notifyChanges(goalEntry);
                                        }

                                        new InsertNewGoalEntry(context).execute(goalEntry);


                                        BaseApplication.setLastTimeSecondUpdated(
                                                TimeGoalieDateUtils.getCurrentTimeInMillis());

                                    }


                                }//end for
                                TimeGoalieAlarmManager.setTimeGoalAlarm(TimeGoalieDateUtils.createTargetSecondlyCalendarTime((int) (
                                                TimeGoalieAlarmReceiver.SECONDLY_FREQUENCY) / 1000),
                                        context, null,
                                        TimeGoalieAlarmReceiver.createSecondlyTimeGoaliePendingIntent(context,
                                                TimeGoalieAlarmReceiver.
                                                        createEverySecondDbUpdateAlarmIntent(context)));

                                cursor.close();
                            }
                        }///end if second has elapsed.
                        else {
                            //catch up
                            TimeGoalieAlarmManager.setTimeGoalAlarm(TimeGoalieDateUtils.createTargetSecondlyCalendarTime((int) (
                                            TimeGoalieAlarmReceiver.SECONDLY_FREQUENCY - diff) / 1000),
                                    context, null,
                                    TimeGoalieAlarmReceiver.createSecondlyTimeGoaliePendingIntent(context,
                                            TimeGoalieAlarmReceiver.
                                                    createEverySecondDbUpdateAlarmIntent(context)));
                        }
                    } else {
                        //check back later
                        PendingIntent pi = TimeGoalieAlarmReceiver.createSecondlyTimeGoaliePendingIntent(context,
                                TimeGoalieAlarmReceiver.
                                        createEverySecondDbUpdateAlarmIntent(context));
                        TimeGoalieAlarmManager.cancelTimeGoalAlarm(context, pi);
                        TimeGoalieAlarmManager.setTimeGoalAlarm(TimeGoalieDateUtils.createTargetSecondlyCalendarTime((int)
                                        DELAYINTENT_INTERVAL), context,
                                null, pi);

                    }

                }//else SECONDLY


            }//if cursor!= null


        }
    }
}
