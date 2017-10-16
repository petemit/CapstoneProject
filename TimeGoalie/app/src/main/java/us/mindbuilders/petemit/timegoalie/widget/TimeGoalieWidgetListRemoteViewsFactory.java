package us.mindbuilders.petemit.timegoalie.widget;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

import us.mindbuilders.petemit.timegoalie.BaseApplication;
import us.mindbuilders.petemit.timegoalie.R;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.TimeGoalieAlarmObject;
import us.mindbuilders.petemit.timegoalie.data.TimeGoalieContract;
import us.mindbuilders.petemit.timegoalie.utils.CustomTextView;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieDateUtils;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieUtils;

/**
 * Created by Peter on 10/11/2017.
 */

public class TimeGoalieWidgetListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context context;
    private ArrayList<Goal> goalData;

    public TimeGoalieWidgetListRemoteViewsFactory(Context context){
        this.context = context;
        Log.e("findme","I got here now");
    }
    @Override
    public void onCreate() {
        Log.e("findme","I got here again");

    }

    @Override
    public void onDataSetChanged() {
        Log.e("findme","I got here");
        if (goalData != null) {
            goalData=null;
        }
        Cursor cursor = context.getContentResolver().query(
                TimeGoalieContract.getGoalsThatHaveGoalEntryForToday(),
                null,
                null,
                new String[]{TimeGoalieDateUtils.
                        getSqlDateString(BaseApplication.getActiveCalendarDate())},
                null
        );
        if (cursor != null) {
            goalData = Goal.createGoalListWithGoalEntriesFromCursor(cursor);
            cursor.close();
        }

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (goalData != null) {
            return goalData.size();
        }
        else {
            return 0;
        }
    }

    @Override
    public RemoteViews getViewAt(int i) {
        if (goalData != null && goalData.size() > 0 ){
            Goal goal = goalData.get(i);
            TimeGoalieAlarmObject timeGoalieAlarmObj =
                    TimeGoalieUtils.getTimeGoalieAlarmObjectByDate(goal);

            RemoteViews views=null;



            if (goal.getGoalTypeId()==2) { //yes/no goal
                views = new RemoteViews(context.getPackageName(),
                        R.layout.time_goalie_widget_item_yes_no);
                if (goal.getGoalEntry().getHasSucceeded()==0) {
                    views.setViewVisibility(R.id.widget_yes_no_checkbox_off, View.VISIBLE);
                    views.setViewVisibility(R.id.widget_yes_no_checkbox_on, View.GONE);
                }
                else{
                    views.setViewVisibility(R.id.widget_yes_no_checkbox_off, View.GONE);
                    views.setViewVisibility(R.id.widget_yes_no_checkbox_on, View.VISIBLE);
                }

                views.setOnClickFillInIntent(R.id.widget_yes_no_checkbox_off,
                        TimeGoalieWidgetProvider.getUpdateYesNoGoalFillInIntent(goal.getGoalEntry()));

                views.setOnClickFillInIntent(R.id.widget_yes_no_checkbox_on,
                        TimeGoalieWidgetProvider.getUpdateYesNoGoalFillInIntent(goal.getGoalEntry()));
            }

            else{
                views = new RemoteViews(context.getPackageName(),
                        R.layout.time_goalie_widget_item);
                CustomTextView timeText =
                        new CustomTextView(context,views,R.id.widget_time_tv);
                CustomTextView timeOutOfText =
                        new CustomTextView(context,views,R.id.widget_time_out_of_tv);
                TimeGoalieUtils.setTimeTextLabel(goal,timeGoalieAlarmObj,timeText,timeOutOfText);

            }

            if (views != null) {
                views.setTextViewText(R.id.widget_goal_tv, goal.getName());
                Log.e("findme", goal.getName());
            }

            return views;
        }
        else {
            //implement return empty views
            return null;
        }
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
