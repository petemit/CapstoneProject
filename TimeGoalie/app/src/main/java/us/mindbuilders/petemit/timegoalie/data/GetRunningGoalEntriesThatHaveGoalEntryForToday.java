package us.mindbuilders.petemit.timegoalie.data;

import android.app.job.JobParameters;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import us.mindbuilders.petemit.timegoalie.BaseApplication;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieDateUtils;


/**
 * Created by Peter on 10/31/2017.
 */

public class GetRunningGoalEntriesThatHaveGoalEntryForToday extends AsyncTask<
        GetRunningGoalEntriesThatHaveGoalEntryForToday.TimeGoalieJobCallback,
        GetRunningGoalEntriesThatHaveGoalEntryForToday.TimeGoalieJobCallback, Cursor> {

    TimeGoalieJobCallback callback;
    Context context;
    JobParameters jobParameters;
    public GetRunningGoalEntriesThatHaveGoalEntryForToday(Context context, JobParameters jobParameters) {
        this.context = context;
        this.jobParameters = jobParameters;
    }

    @Override
    protected Cursor doInBackground(TimeGoalieJobCallback... timeGoalieJobCallbacks) {

        Cursor cursor = context.getContentResolver().query
                (TimeGoalieContract.getRunningGoalEntriesThatHaveGoalEntryForToday(),
                        null,
                        null,
                        new String[]{TimeGoalieDateUtils.
                                getSqlDateString(BaseApplication.getActiveCalendarDate())},
                        null);
        return cursor;
    }

    @Override
    protected void onPostExecute(Cursor cursor) {
        if(callback != null) {
            callback.callBack(cursor, jobParameters);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public interface TimeGoalieJobCallback {
        void callBack(Cursor cursor, JobParameters jobParameters);
    }
}
