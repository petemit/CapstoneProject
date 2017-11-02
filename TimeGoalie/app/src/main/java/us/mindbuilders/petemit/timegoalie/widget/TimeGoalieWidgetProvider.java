package us.mindbuilders.petemit.timegoalie.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import us.mindbuilders.petemit.timegoalie.R;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.GoalEntry;
import us.mindbuilders.petemit.timegoalie.data.InsertNewGoalEntry;
import us.mindbuilders.petemit.timegoalie.services.TimeGoalieAlarmReceiver;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieAlarmManager;

/**
 * Created by Peter on 10/11/2017.
 */

public class TimeGoalieWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_UPDATE_GOAL_ENTRY =
            "us.mindbuilders.petemit.timegoalie.action.update_goal_entry";
    public static final String ACTION_GET_GOALS_FOR_TODAY =
            "us.mindbuilders.petemit.timegoalie.action.get_goals_for_today";
    public static final String GOAL_ENTRY_ITEM =
            "us.mindbuilders.petemit.timegoalie.action.goalentry";


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        startActionGetGoalsForToday(context);
        super.onUpdate(context,appWidgetManager,appWidgetIds);
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
                    Bundle bundle = intent.getBundleExtra("goalentrybundle");
                    GoalEntry goalEntry = (GoalEntry) bundle.getParcelable("goalentry");
                    int goalType = bundle.getInt("goal_type");
                    if (goalType == 2) {
                        goalEntry.setHasFinished(!goalEntry.isHasFinished());
                        goalEntry.setHasSucceeded(!goalEntry.getHasSucceeded());
                    }
                    else {
                        goalEntry.setRunning(!goalEntry.isRunning());

                        //culprit
                        //start the secondly alarm
                        TimeGoalieAlarmManager.setTimeGoalAlarm(
                                TimeGoalieAlarmReceiver.SECONDLY_FREQUENCY,
                                context, null,
                                TimeGoalieAlarmReceiver.createSecondlyTimeGoaliePendingIntent(
                                        context,
                                        TimeGoalieAlarmReceiver.
                                                createEverySecondDbUpdateAlarmIntent(context,
                                                        (int)goalEntry.getGoal_id()),
                                        (int)goalEntry.getGoal_id()));
                    }
                    handleActionUpdateGoalEntry(context, goalEntry);
                    break;
            }
        }
        super.onReceive(context, intent);
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews rv = getTimeGoalieRemoteView(context, appWidgetId, appWidgetManager);

        appWidgetManager.updateAppWidget(appWidgetId,rv);
    }

    private static RemoteViews getTimeGoalieRemoteView(Context context, int appWidgetId,
                                                       AppWidgetManager appWidgetManager) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.time_goalie_widget);
            Intent intent = new Intent(context, TimeGoalieRvWidgetService.class);
        views.setRemoteAdapter(R.id.time_goalie_widget,intent);
        PendingIntent pi = getUpdateYesNoGoalPendingIntent(context, appWidgetId);
        views.setPendingIntentTemplate(R.id.time_goalie_widget,pi);
//        views.setPendingIntentTemplate(R.id.widget_yes_no_checkbox_off,pi);
//        views.setPendingIntentTemplate(R.id.widget_yes_no_checkbox_on,pi);
        views.setEmptyView(R.id.time_goalie_widget, R.id.empty_widget_tv);
        return views;
    }

    public static void updateTimeGoalieWidgets(Context context, AppWidgetManager appWidgetManager,
                                               int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; i++) {
                updateAppWidget(context, appWidgetManager, appWidgetIds[i] );

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
            bundle.putInt("goal_type", (int)goal.getGoalTypeId());
            bundle.putParcelable("goalentry", goal.getGoalEntry());
            intent.putExtra("goalentrybundle", bundle);
        }
        return intent;
    }

    public static Intent getUpdateTimeGoalFillInIntent(Goal goal) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        if (goal.getGoalEntry() != null) {

            bundle.putInt("goal_type",(int)goal.getGoalTypeId());
            bundle.putParcelable("goalentry", goal.getGoalEntry());
            intent.putExtra("goalentrybundle", bundle);
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

    private void handleActionUpdateGoalEntry(Context context, GoalEntry goalEntry) {
        new InsertNewGoalEntry(context).execute(goalEntry);

        startActionGetGoalsForToday(context);


    }





}
