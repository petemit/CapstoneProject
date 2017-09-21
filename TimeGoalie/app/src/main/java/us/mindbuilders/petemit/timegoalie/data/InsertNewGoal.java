package us.mindbuilders.petemit.timegoalie.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;

/**
 * Created by Peter on 9/27/2017.
 */

public class InsertNewGoal extends AsyncTask<Goal, Void, Void> {
    Context context;

    public InsertNewGoal(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Goal... goals) {
        if (goals.length == 1) {
            Goal goal = goals[0];
            ContentValues goal_cv = new ContentValues();
            String date = goal.getCreationDate();

            goal_cv.put(TimeGoalieContract.Goals.GOALS_COLUMN_NAME, goal.getName());
            goal_cv.put(TimeGoalieContract.Goals.GOALS_COLUMN_GOALTYPEID, goal.getGoalTypeId());
            goal_cv.put(TimeGoalieContract.Goals.GOALS_COLUMN_ISDAILY, goal.getIsDaily());
            if (date != null) {
                goal_cv.put(TimeGoalieContract.Goals.GOALS_COLUMN_CREATIONDATE, date);
            }
            goal_cv.put(TimeGoalieContract.Goals.GOALS_COLUMN_ISWEEKLY, goal.getIsWeekly());
            goal_cv.put(TimeGoalieContract.Goals.GOALS_COLUMN_TIMEGOALHOURS, goal.getHours());
            goal_cv.put(TimeGoalieContract.Goals.GOALS_COLUMN_TIMEGOALMINUTES, goal.getMinutes());

            long goal_id = ContentUris.parseId(context.getContentResolver()
                    .insert(TimeGoalieContract.Goals.CONTENT_URI, goal_cv));
            if (goal.getGoalDays()!=null) {
                for (int i = 0; i < goal.getGoalDays().size(); i++) {
                    ContentValues goal_day_cv = new ContentValues();

                    goal_day_cv.put(TimeGoalieContract.GoalsDays.GOALS_DAYS_COLUMN_GOAL_ID, goal_id);
                    goal_day_cv.put(TimeGoalieContract.GoalsDays.GOALS_DAYS_COLUMN_DAY_ID,
                            goal.getGoalDays().get(i).getSequence());
                    context.getContentResolver().insert(TimeGoalieContract.GoalsDays.CONTENT_URI,
                            goal_day_cv);
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}

