package us.mindbuilders.petemit.timegoalie.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.ArrayList;

import us.mindbuilders.petemit.timegoalie.R;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;

/**
 * Created by Peter on 10/11/2017.
 */

public class TimeGoalieWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        RemoteViews rv = getTimeGoalieRemoteView(context);

        appWidgetManager.updateAppWidget(appWidgetId,rv);
    }

    private static RemoteViews getTimeGoalieRemoteView(Context context) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.time_goalie_widget);
            Intent intent = new Intent(context, TimeGoalieWidgetListRemoteViewsFactory.class);
        views.setRemoteAdapter(R.id.time_goalie_widget,intent);
        views.setEmptyView(R.id.time_goalie_widget, R.id.empty_widget_tv);

        return null;
    }

    public static void updateTimeGoalieWidgets(Context context, AppWidgetManager appWidgetManager,
                                               int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; i++) {
                updateAppWidget(context, appWidgetManager, appWidgetIds[i] );

        }
    }


    private static void setToEmptyView() {

    }
}
