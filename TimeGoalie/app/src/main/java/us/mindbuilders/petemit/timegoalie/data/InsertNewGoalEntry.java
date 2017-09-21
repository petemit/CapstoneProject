package us.mindbuilders.petemit.timegoalie.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.GoalEntry;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieDateUtils;

/**
 * Created by Peter on 9/27/2017.
 */

public class InsertNewGoalEntry extends AsyncTask<GoalEntry, Void, Void> {

    Context context;

    public InsertNewGoalEntry(Context context){
        this.context=context;
    }

    @Override
    protected Void doInBackground(GoalEntry... goalEntries) {

        if (goalEntries.length > 0) {
            for (int i = 0; i < goalEntries.length; i++) {
                GoalEntry goalEntry = goalEntries[i];
                ContentValues goal_entry_cv = new ContentValues();
                String date = goalEntry.getDate();
                int secondsElapsed = goalEntry.getSecondsElapsed();
                long goal_id = goalEntry.getGoal_id();

                goal_entry_cv.put(TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_GOAL_ID, goalEntry.getGoal_id());
                goal_entry_cv.put(TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_SECONDSELAPSED, goalEntry.getSecondsElapsed());
                goal_entry_cv.put(TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_DATETIME, goalEntry.getDate());



                long goalEntry_id = ContentUris.parseId(context.getContentResolver()
                        .insert(TimeGoalieContract.GoalEntries.CONTENT_URI, goal_entry_cv));
            }

        }
        return null;
    }
}
