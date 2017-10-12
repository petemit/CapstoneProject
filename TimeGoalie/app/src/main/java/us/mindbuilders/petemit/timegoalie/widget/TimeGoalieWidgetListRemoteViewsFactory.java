package us.mindbuilders.petemit.timegoalie.widget;

import android.content.Context;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

import us.mindbuilders.petemit.timegoalie.BaseApplication;
import us.mindbuilders.petemit.timegoalie.R;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;
import us.mindbuilders.petemit.timegoalie.data.TimeGoalieContract;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieDateUtils;

/**
 * Created by Peter on 10/11/2017.
 */

public class TimeGoalieWidgetListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context context;
    private ArrayList<Goal> goalData;

    public TimeGoalieWidgetListRemoteViewsFactory(Context context){
        this.context = context;
    }
    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

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
            goalData = Goal.createGoalListFromCursor(cursor);
            cursor.close();
        }

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return goalData.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        if (goalData != null && goalData.size() > 0 ){
            Goal goal = goalData.get(i);

            RemoteViews views=null;

            if (goal.getGoalTypeId()==2) { //yes/no goal
                views = new RemoteViews(context.getPackageName(),
                        R.layout.time_goalie_widget_item);
            }

            if (goal.getGoalTypeId()==2) { //yes/no goal
                views = new RemoteViews(context.getPackageName(),
                        R.layout.time_goalie_widget_item_yes_no);
            }

            if (views != null) {
                views.setTextViewText(R.id.widget_goal_tv, goal.getName());
            }

            return views;
        }
        return null;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
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
