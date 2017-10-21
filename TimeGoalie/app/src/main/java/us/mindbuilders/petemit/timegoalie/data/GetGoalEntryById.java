package us.mindbuilders.petemit.timegoalie.data;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.GoalEntry;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.GoalEntryGoalCounter;

/**
 * Created by Peter on 10/16/2017.
 */

public class GetGoalEntryById extends AsyncTask<GoalEntry, Void, Void> {
    private Context context;
    public GetGoalEntryById (Context context){
        this.context=context;
    }
    @Override
    protected Void doInBackground(GoalEntry... goalEntries) {
        Cursor cursor=null;
        if (goalEntries[0] != null) {
            cursor = context.getContentResolver().query(TimeGoalieContract
                            .buildGetAGoalEntryByGoalEntryId(goalEntries[0].getGoal_id()),
                    null,
                    null,
                    null,
                    null);
        }
        if (cursor != null && cursor.getCount()==1) {
            cursor.moveToFirst();
            goalEntries[0].setSecondsElapsed(cursor.getInt(cursor.getColumnIndex(
                    TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_SECONDSELAPSED)));
        }

        if (cursor != null) {
            cursor.close();
        }
        return null;
    }
}
