package us.mindbuilders.petemit.timegoalie.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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

public class GoalMgmtService  {


//    public GoalMgmtService() {
//        super("GoalMgmtService");
//    }

    public static void startActionUpdateGoalEntry(Context context, GoalEntry goalEntry) {
//        Intent intent = new Intent(context, GoalMgmtService.class);
//        intent.setAction(ACTION_UPDATE_GOAL_ENTRY);
        new InsertNewGoalEntry(context).execute(goalEntry);
    }
//
//
//
//    @Override
//    protected void onHandleIntent(Intent intent) {
//        if (intent != null) {
//            final String action = intent.getAction();
//            switch (action) {
//                case ACTION_GET_GOALS_FOR_TODAY:
//                    handleActionGetGoalsForTodayAndUpdateWidgets();
//                    break;
//                case ACTION_UPDATE_GOAL_ENTRY:
//                    handleActionUpdateGoalEntry(intent);
//                    break;
//            }
//        }
//    }
//
//

}
