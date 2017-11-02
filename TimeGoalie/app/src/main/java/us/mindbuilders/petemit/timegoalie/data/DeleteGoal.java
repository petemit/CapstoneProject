package us.mindbuilders.petemit.timegoalie.data;

import android.content.Context;
import android.os.AsyncTask;

import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.GoalEntryGoalCounter;

/**
 * Created by Peter on 10/25/2017.
 */

public class DeleteGoal extends AsyncTask<Goal, Void, Void> {
    private Context context;
    private GoalEntryGoalCounter goalEntryGoalCounter;
    public DeleteGoal(Context context, GoalEntryGoalCounter goalEntryGoalCounter) {
        this.context = context;
        this.goalEntryGoalCounter = goalEntryGoalCounter;
    }
    @Override
    protected Void doInBackground(Goal... goals) {
        if (goals != null) {
            for (int i = 0; i < goals.length; i++) {
                Goal goal = goals[i];
                context.getContentResolver().delete(
                        TimeGoalieContract.buildGetaGoalByIdUri(goal.getGoalId()),
                        null,
                        null);
                context.getContentResolver().delete(
                        TimeGoalieContract.buildGetAllGoalEntriesByGoalId(goal.getGoalId()),
                        null,
                        null);
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        new GetSuccessfulGoalCount(context).execute(goalEntryGoalCounter);
    }
}
