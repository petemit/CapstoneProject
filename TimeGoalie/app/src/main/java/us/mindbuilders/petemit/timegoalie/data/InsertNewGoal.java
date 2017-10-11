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

public class InsertNewGoal extends AsyncTask<Goal, Void, Goal> {
    Context context;

    public InsertNewGoal(Context context) {
        this.context = context;
    }

    @Override
    protected Goal doInBackground(Goal... goals) {
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
            goals[0].setGoalId(goal_id);


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
            return goals[0];
        }
        return null;
    }

    @Override
    protected void onPostExecute(Goal goal) {
        super.onPostExecute(goal);

        if (goal!=null && ((goal.getIsDaily()==0&&goal.getIsWeekly()==0)||goal.getIsDaily()==1)) {
            GoalEntry todayGoalEntry= new GoalEntry(goal.getGoalId(), TimeGoalieDateUtils.getSqlDateString());
            todayGoalEntry.setSecondsElapsed(0);
            if (goal.getGoalTypeId()==1) {
                todayGoalEntry.setHasSucceeded(1);
            }
            new InsertNewGoalEntry(context).execute(todayGoalEntry);
        }
    }
}

