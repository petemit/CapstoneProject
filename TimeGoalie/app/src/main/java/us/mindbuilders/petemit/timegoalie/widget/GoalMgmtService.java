package us.mindbuilders.petemit.timegoalie.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
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
 * Created by Peter on 10/11/2017.
 */

public class GoalMgmtService extends IntentService {

    public static final String ACTION_UPDATE_GOAL_ENTRY =
            "us.mindbuilders.petemit.timegoalie.action.update_goal_entry";
    public static final String ACTION_GET_GOALS_FOR_TODAY =
            "us.mindbuilders.petemit.timegoalie.action.get_goals_for_today";

    public GoalMgmtService() {
        super("GoalMgmtService");
    }

    public static void startActionUpdateGoalEntry(Context context, GoalEntry goalEntry) {
//        Intent intent = new Intent(context, GoalMgmtService.class);
//        intent.setAction(ACTION_UPDATE_GOAL_ENTRY);
        new InsertNewGoalEntry(context).execute(goalEntry);
    }

    public static void startActionGetGoalsForToday(Context context) {
        Intent intent = new Intent(context, GoalMgmtService.class);
        intent.setAction(ACTION_GET_GOALS_FOR_TODAY);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_GOALS_FOR_TODAY.equals(action)){
                handleActionGetGoalsForTodayAndUpdateWidgets();
            }
        }

    }

    private void handleActionGetGoalsForTodayAndUpdateWidgets() {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                TimeGoalieWidgetProvider.class));

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.time_goalie_widget);
        TimeGoalieWidgetProvider.updateTimeGoalieWidgets(getBaseContext(), appWidgetManager,
                appWidgetIds);
    }


}
