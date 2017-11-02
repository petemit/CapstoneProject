package us.mindbuilders.petemit.timegoalie.data;

/**
 * Created by Peter on 10/10/2017.
 */

import android.database.Cursor;

import us.mindbuilders.petemit.timegoalie.GoalRecyclerViewAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.GoalEntry;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.GoalEntryGoalCounter;

/**
 * Created by Peter on 9/27/2017.
 */

public class GetSuccessfulGoalCount extends AsyncTask<GoalEntryGoalCounter, Void, Void> {

    Context context;
    Cursor cursor;
    GoalEntry goalEntry;
    private String date;
    GoalRecyclerViewAdapter.GoalCounter goalCounter;

    public GetSuccessfulGoalCount(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(GoalEntryGoalCounter... goalEntryGoalCounters) {
        if (goalEntryGoalCounters[0] != null) {
            goalCounter = goalEntryGoalCounters[0].getGc();
            date = goalEntryGoalCounters[0].getDate();
            if (goalEntryGoalCounters[0].getGoalEntry() != null) {
                goalEntry = goalEntryGoalCounters[0].getGoalEntry();
            }
        }
        if (goalEntry != null) {
            ContentValues goalEntries_cv = new ContentValues();

            goalEntries_cv.put(TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_GOAL_ID,
                    goalEntry.getGoal_id());
            goalEntries_cv.put(TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_DATETIME,
                    goalEntry.getDate());
            goalEntries_cv.put(TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_GOALAUGMENT,
                    goalEntry.getGoalAugment());
            goalEntries_cv.put(TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_SUCCEEDED,
                    goalEntry.getHasSucceeded());
            goalEntries_cv.put(TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_ISRUNNING,
                    (goalEntry.isRunning()) ? 1 : 0);
            goalEntries_cv.put(TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_ISFINISHED,
                    (goalEntry.isHasFinished()) ? 1 : 0);
            goalEntries_cv.put(TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_TARGETTIME,
                    goalEntry.getTargetTime());

            goalEntries_cv.put(TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_SECONDSELAPSED,
                    goalEntry.getSecondsElapsed());

            context.getContentResolver().insert(TimeGoalieContract.GoalEntries.CONTENT_URI,
                    goalEntries_cv);
        }


        cursor = context.getContentResolver().query
                (TimeGoalieContract.getSuccessfulGoalsForToday(date),
                null,
                null,
                null,
                null);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (cursor != null && goalCounter != null) {
            goalCounter.updateGoalCounter(cursor.getCount());
        }
        if (cursor != null) {
            cursor.close();
        }
    }
}
