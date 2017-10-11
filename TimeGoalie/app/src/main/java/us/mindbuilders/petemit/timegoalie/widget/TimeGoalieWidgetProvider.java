package us.mindbuilders.petemit.timegoalie.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

/**
 * Created by Peter on 10/11/2017.
 */

public class TimeGoalieWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                    long goal_id, int goal_type, int appWidgetId) {
        RemoteViews rv = getTimeGoalieRemoteView(context, goal_id, goal_type);

        appWidgetManager.updateAppWidget(appWidgetId,rv);
    }

    private static RemoteViews getTimeGoalieRemoteView(Context context, long goal_id, int goal_type) {
        return null;
    }
}
