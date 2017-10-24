package us.mindbuilders.petemit.timegoalie.data;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.GoalEntry;

/**
 * Created by Peter on 10/16/2017.
 */

public class GetGoalEntryByDateAndGoal extends AsyncTask<GoalEntry, Void, Void> {
    private Context context;

    public GetGoalEntryByDateAndGoal(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(GoalEntry... goalEntries) {
        Cursor cursor = null;
        if (goalEntries[0] != null) {
            cursor = context.getContentResolver().query(
                    TimeGoalieContract.buildGetAGoalEntryByGoalId(goalEntries[0].getGoal_id()),
                    null,
                    null,
                    new String[]{goalEntries[0].getDate()},
                    null);
        }

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            GoalEntry newGoalEntry = new GoalEntry(cursor.getLong(
                    cursor.getColumnIndex(TimeGoalieContract.GoalEntries._ID)
            ), goalEntries[0].getGoal_id(), cursor.getString(
                    cursor.getColumnIndex(
                            TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_DATETIME)));
            newGoalEntry.setGoalAugment(cursor.getInt(cursor.getColumnIndex(
                    TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_GOALAUGMENT
            )));
            goalEntries[0].setSecondsElapsed(newGoalEntry.getSecondsElapsed());
        }

        if (cursor != null) {
            cursor.close();
        }
        return null;
    }
}
