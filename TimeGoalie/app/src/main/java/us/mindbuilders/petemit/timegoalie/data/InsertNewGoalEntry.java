package us.mindbuilders.petemit.timegoalie.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.GoalEntry;

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
        ContentValues goalEntries_cv = new ContentValues();
        for (int i = 0 ; i < goalEntries.length ; i++ ) {
            goalEntries_cv.put(TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_GOAL_ID,
                    goalEntries[i].getGoal_id());
            goalEntries_cv.put(TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_DATETIME,
                    goalEntries[i].getDate());
            goalEntries_cv.put(TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_GOALAUGMENT,
                    goalEntries[i].getGoalAugment());
            goalEntries_cv.put(TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_SUCCEEDED,
                    goalEntries[i].getHasSucceeded());
            goalEntries_cv.put(TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_ISRUNNING,
                    (goalEntries[i].isRunning()) ? 1 : 0);
            goalEntries_cv.put(TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_ISFINISHED,
                    (goalEntries[i].isHasFinished()) ? 1 : 0);
            goalEntries_cv.put(TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_TARGETTIME,
                    goalEntries[i].getTargetTime());
//            if (goalEntries[0].isNeedsSecondsUpdate()){
            goalEntries_cv.put(TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_SECONDSELAPSED,
                    goalEntries[i].getSecondsElapsed());
         //   }

        }

       long goalEntry_id = ContentUris.parseId(context.getContentResolver()
               .insert(TimeGoalieContract.GoalEntries.CONTENT_URI,goalEntries_cv));
        goalEntries[0].setId(goalEntry_id);
        if (goalEntries.length > 0) {
            for (int i = 0; i < goalEntries.length; i++) {}

        }
        return null;
    }
}
