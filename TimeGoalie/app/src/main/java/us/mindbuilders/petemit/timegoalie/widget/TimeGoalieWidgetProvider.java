package us.mindbuilders.petemit.timegoalie.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.ArrayList;

import us.mindbuilders.petemit.timegoalie.BaseApplication;
import us.mindbuilders.petemit.timegoalie.R;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.GoalEntry;
import us.mindbuilders.petemit.timegoalie.data.InsertNewGoalEntry;
import us.mindbuilders.petemit.timegoalie.data.TimeGoalieContract;
import us.mindbuilders.petemit.timegoalie.services.TimeGoalieAlarmReceiver;
import us.mindbuilders.petemit.timegoalie.utils.CustomTextView;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieAlarmManager;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieDateUtils;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieUtils;

/**
 * Created by Peter on 10/11/2017.
 */

public class TimeGoalieWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_UPDATE_GOAL_ENTRY =
            "us.mindbuilders.petemit.timegoalie.action.update_goal_entry_partial";
    public static final String ACTION_GET_GOALS_FOR_TODAY =
            "us.mindbuilders.petemit.timegoalie.action.get_goals_for_today";
    public static final String GOAL_ENTRY_ITEM =
            "us.mindbuilders.petemit.timegoalie.action.goalentry";


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        startActionGetGoalsForToday(context);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {


        if (intent != null) {
            final String action = intent.getAction();
            switch (action) {
                case ACTION_GET_GOALS_FOR_TODAY:
                    handleActionGetGoalsForTodayAndUpdateWidgets(context);
                    break;
                case ACTION_UPDATE_GOAL_ENTRY:
                    Bundle bundle = intent.getBundleExtra(context.getString(R.string.goalentrybundle_key));
                    GoalEntry goalEntry = bundle.getParcelable(context.getString(R.string.goalentry_key));
                    String goalname = bundle.getString(context.getString(R.string.goal_name_key));
                    int goalSeconds = bundle.getInt(context.getString(R.string.goal_seconds_key));

                    int goalType = bundle.getInt(context.getString(R.string.goal_type_key));
                    if (goalType == 2) {
                        goalEntry.setHasFinished(!goalEntry.isHasFinished());
                        goalEntry.setHasSucceeded(!goalEntry.getHasSucceeded());
                    } else {
                        goalEntry.setRunning(!goalEntry.isRunning());


                        PendingIntent goalDonePi = TimeGoalieAlarmReceiver.createTimeGoaliePendingIntent(
                                context, TimeGoalieAlarmReceiver.createAlarmDoneTimeGoalieAlarmIntent
                                        (context,
                                                goalname,
                                                (int) goalEntry.getGoal_id()
                                        ), (int) goalEntry.getGoal_id());

                        PendingIntent oneMinutePi = TimeGoalieAlarmReceiver.
                                createTimeGoaliePendingIntent(context,
                                        TimeGoalieAlarmReceiver.
                                                createOneMinuteWarningTimeGoalieAlarmIntent(
                                                        context,
                                                        goalname,
                                                        (int) goalEntry.getGoal_id()
                                                ),
                                        (int) goalEntry.getGoal_id());


                        if (goalEntry.isRunning()) {
                            //culprit
                            //start the secondly alarm
                            TimeGoalieAlarmManager.setTimeGoalAlarm(
                                    TimeGoalieDateUtils.createTargetSecondlyCalendarTime((int)
                                            TimeGoalieAlarmReceiver.SECONDLY_FREQUENCY/1000),
                                    context, null,
                                    TimeGoalieAlarmReceiver.createSecondlyTimeGoaliePendingIntent(
                                            context,
                                            TimeGoalieAlarmReceiver.
                                                    createEverySecondDbUpdateAlarmIntent(context)));


                            long newtime = goalSeconds - goalEntry.getSecondsElapsed();

                            if (!goalEntry.isHasFinished()) {


                                long hours = newtime / (60 * 60);
                                long minutes = (newtime - (hours * 60 * 60)) / 60;
                                long seconds = (newtime - (hours * 60 * 60) - (minutes * 60));

                                Log.e("Mindbuilders", "hours: " + hours);
                                Log.e("Mindbuilders", "minutes: " + minutes);
                                Log.e("Mindbuilders", "seconds: " + seconds);
                                long targetTime = TimeGoalieDateUtils.createTargetCalendarTime(
                                        (int) hours,
                                        (int) minutes,
                                        (int) seconds);

                                if (goalEntry.getTargetTime() == 0) {
                                    goalEntry.setTargetTime(targetTime);
                                }

                                if (!goalEntry.isHasFinished()) {
                                    TimeGoalieAlarmManager.setTimeGoalAlarm(
                                            targetTime,
                                            context, null,
                                            goalDonePi);
                                }

                                //Sets up the running out of time alert
                                //will not fire if it has been fired already for a goal.
                                if (goalType == 1) { //Limit Goal Type


                                    long targetTimeLimitGoal = TimeGoalieDateUtils.createTargetCalendarTime(
                                            (int) hours,
                                            (int) minutes,
                                            (int) seconds - TimeGoalieAlarmManager.ONE_MINUTE_WARNING_TIME * 60);

                                    if (oneMinutePi != null) {
                                        TimeGoalieAlarmManager.setTimeGoalAlarm(
                                                targetTimeLimitGoal,
                                                context, null,
                                                oneMinutePi);
                                    }

                                }

                            }
                        } else { //goal is not running
                            // stopping alarm
                            TimeGoalieAlarmManager.cancelTimeGoalAlarm(context, goalDonePi);
                            TimeGoalieAlarmManager.cancelTimeGoalAlarm(context, oneMinutePi);
                        }
                    }
            handleActionUpdateGoalEntry(context, goalEntry);
            break;
        }
    }
        super.

    onReceive(context, intent);

}

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews rv = getTimeGoalieRemoteView(context, appWidgetId, appWidgetManager);

        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    private static RemoteViews getTimeGoalieRemoteView(Context context, int appWidgetId,
                                                       AppWidgetManager appWidgetManager) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.time_goalie_widget);
        Intent intent = new Intent(context, TimeGoalieRvWidgetService.class);
        views.setRemoteAdapter(R.id.time_goalie_widget, intent);
        PendingIntent pi = getUpdateYesNoGoalPendingIntent(context, appWidgetId);
        views.setPendingIntentTemplate(R.id.time_goalie_widget, pi);
//        views.setPendingIntentTemplate(R.id.widget_yes_no_checkbox_off,pi);
//        views.setPendingIntentTemplate(R.id.widget_yes_no_checkbox_on,pi);
        views.setEmptyView(R.id.time_goalie_widget, R.id.empty_widget_tv);
        return views;
    }

    public static void updateTimeGoalieWidgets(Context context, AppWidgetManager appWidgetManager,
                                               int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);

        }
    }

    private static void setToEmptyView() {

    }


    public static void startActionGetGoalsForToday(Context context) {
        Intent intent = new Intent(context, TimeGoalieWidgetProvider.class);
        intent.setAction(ACTION_GET_GOALS_FOR_TODAY);
        context.sendBroadcast(intent);
    }

    public static Intent getUpdateYesNoGoalFillInIntent(Goal goal) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        if (goal.getGoalEntry() != null) {
            bundle.putInt("goal_type", (int) goal.getGoalTypeId());
            bundle.putParcelable("goalentry", goal.getGoalEntry());
            intent.putExtra("goalentrybundle", bundle);
        }
        return intent;
    }

    public static Intent getUpdateTimeGoalFillInIntent(Goal goal, Context context) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        if (goal.getGoalEntry() != null) {

            bundle.putInt("goal_type", (int) goal.getGoalTypeId());
            bundle.putParcelable("goalentry", goal.getGoalEntry());
            bundle.putString(context.getString(R.string.goal_name_key), goal.getName());
            bundle.putInt(context.getString(R.string.goal_seconds_key), (int) goal.getGoalSeconds());
            intent.putExtra("goalentrybundle", bundle);


            //not sure if I can do this or need this

//                    new Handler(Looper.getMainLooper()).post(new Runnable() {
//                        @Override
//                        public void run() {

            // TimeGoalieAlarmManager.startTimer(null, timeText, newtime, goal, context, null);
////                        }
////                    });
////                    TimeGoalieAlarmManager.startTimer(null, timeText, newtime, goal, context, null);
//
//                        }
//                    });


        }


        return intent;
    }


    public static PendingIntent getUpdateYesNoGoalPendingIntent(Context context, int appWidgetId) {
        Intent intent = new Intent(context, TimeGoalieWidgetProvider.class);
        intent.setAction(ACTION_UPDATE_GOAL_ENTRY);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        PendingIntent pi =
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pi;
    }

    private void handleActionGetGoalsForTodayAndUpdateWidgets(Context context) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,
                TimeGoalieWidgetProvider.class));

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.time_goalie_widget);
        TimeGoalieWidgetProvider.updateTimeGoalieWidgets(context, appWidgetManager,
                appWidgetIds);
    }

    private void handleActionUpdateGoalsPartially(Context context, Goal goal) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,
                TimeGoalieWidgetProvider.class));

        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.time_goalie_widget);
        CustomTextView timeText =
                new CustomTextView(context, rv, R.id.widget_time_tv);
        TimeGoalieUtils.setTimeTextLabel(goal, timeText, null);
    //    appWidgetManager.partiallyUpdateAppWidget(appWidgetIds, rv);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.time_goalie_widget);


    }


    private void handleActionUpdateGoalEntry(Context context, GoalEntry goalEntry) {
        new InsertNewGoalEntry(context).execute(goalEntry);

        startActionGetGoalsForToday(context);


    }


}
