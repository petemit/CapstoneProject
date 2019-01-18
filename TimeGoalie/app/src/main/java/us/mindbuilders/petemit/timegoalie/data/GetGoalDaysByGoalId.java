package us.mindbuilders.petemit.timegoalie.data;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import java.util.ArrayList;

import us.mindbuilders.petemit.timegoalie.R;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Day;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.GoalEntry;

/**
 * Created by Peter on 10/16/2017.
 */

public class GetGoalDaysByGoalId extends AsyncTask<Goal, Void, ArrayList<Day>> {
    private Context context;
    private String[] dayNames;
    myCallback callback;

    public interface myCallback {
        void show(ArrayList<Day> days);
    }

    public GetGoalDaysByGoalId(Context context, myCallback callback) {
        this.context = context;
        this.callback = callback;
        dayNames = context.getResources().getStringArray(R.array.days_of_the_week);
    }

    @Override
    protected ArrayList<Day> doInBackground(Goal... goals) {

        Cursor cursor = null;
        if (goals[0] != null) {
            cursor = context.getContentResolver().query(ContentUris.withAppendedId(TimeGoalieContract.GoalsDays.CONTENT_URI.buildUpon()
                            .appendPath("getGoalDays").build(), goals[0].getGoalId())
                    , null
                    , null
                    , null,
                    null);

        }
        ArrayList<Day> days = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Day day = new Day();
                day.setName(dayNames[Integer.parseInt(cursor.getString(cursor.getColumnIndex(
                        TimeGoalieContract.GoalsDays.GOALS_DAYS_COLUMN_DAY_ID))) - 1]);
                days.add(day);
            }

            if (cursor != null) {
                cursor.close();
            }

        }
       return days;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        context = null;
        callback = null;
    }

    @Override
    protected void onPostExecute(ArrayList<Day> days) {
        super.onPostExecute(days);
        callback.show(days);
        context = null;
        this.callback = null;
    }
}
