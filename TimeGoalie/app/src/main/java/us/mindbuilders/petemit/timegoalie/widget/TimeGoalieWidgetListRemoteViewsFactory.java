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
import us.mindbuilders.petemit.timegoalie.data.TimeGoalieContract;
import us.mindbuilders.petemit.timegoalie.utils.CustomTextView;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieDateUtils;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieUtils;

/**
 * Created by Peter on 10/11/2017.
 */

public class TimeGoalieWidgetListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private static int counter = 0;
    private Context context;
    private ArrayList<Goal> goalData;
    private RemoteViews lastView;

    public TimeGoalieWidgetListRemoteViewsFactory(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        Log.e("dang", counter + "");
        counter++;
        if (goalData != null) {
            goalData = null;
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
        goalData.clear();

    }

    @Override
    public int getCount() {
        if (goalData != null) {
            return goalData.size();
        } else {
            return 0;
        }
    }

    @Override
    public RemoteViews getViewAt(int i) {
        if (goalData != null && goalData.size() > 0) {
            final Goal goal = goalData.get(i);

            RemoteViews views = null;


            if (goal.getGoalTypeId() == 2) { //yes/no goal
                views = new RemoteViews(context.getPackageName(),
                        R.layout.time_goalie_widget_item_yes_no);
                if (!goal.getGoalEntry().getHasSucceeded()) {

                    views.setViewVisibility(R.id.widget_yes_no_checkbox_off, View.VISIBLE);
                    views.setViewVisibility(R.id.widget_yes_no_checkbox_on, View.GONE);
                } else {
                    views.setViewVisibility(R.id.widget_yes_no_checkbox_off, View.GONE);
                    views.setViewVisibility(R.id.widget_yes_no_checkbox_on, View.VISIBLE);
                }

                Log.e("myMindbuilders-before", goal.getName() + " " + goal.getGoalId()
                        + " " + Boolean.toString(goal.getGoalEntry().getHasSucceeded()));
                views.setOnClickFillInIntent(R.id.widget_yes_no_checkbox,
                        TimeGoalieWidgetProvider.getUpdateYesNoGoalFillInIntent(goal));
                Log.e("myMindbuilders-after", goal.getName() + " " + goal.getGoalId()
                        + " " + Boolean.toString(goal.getGoalEntry().getHasSucceeded()));

            } else { //Time limit Goal
                views = new RemoteViews(context.getPackageName(),
                        R.layout.time_goalie_widget_item);
                final CustomTextView timeText =
                        new CustomTextView(context, views, R.id.widget_time_tv);
                CustomTextView timeOutOfText =
                        new CustomTextView(context, views, R.id.widget_time_out_of_tv);

                //Set up the time goal if goals are running
                if (goal.getGoalEntry().isRunning()) {
                    Log.e("myMindbuilders-after", goal.getName() + " " + goal.getGoalId()
                            + " " + Boolean.toString(goal.getGoalEntry().isRunning()));
                    views.setTextViewText(R.id.start_stop, context.getString(R.string.stop));

                } else {
                    views.setTextViewText(R.id.start_stop, context.getString(R.string.start));
                }

                Log.e("myMindbuilders-before", goal.getName() + " " + goal.getGoalId()
                        + " " + Boolean.toString(goal.getGoalEntry().isRunning()));
                views.setOnClickFillInIntent(R.id.start_stop, TimeGoalieWidgetProvider.
                        getUpdateTimeGoalFillInIntent(goal, context));
                TimeGoalieUtils.setTimeTextLabel(goal, timeText, timeOutOfText);


//
//
            }

            //  if (views != null) {
            views.setTextViewText(R.id.widget_goal_tv, goal.getName());
            //   }
            if (goalData.size() == 1) {
                lastView = views;
            } else {
                lastView = null;
            }
            return views;
        } else

        {
            //implement return empty views
            return null;
        }

    }

    @Override
    public RemoteViews getLoadingView() {
        return lastView;
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
