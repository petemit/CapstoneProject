package us.mindbuilders.petemit.timegoalie.data;

import android.content.Context;
import android.os.AsyncTask;

import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;

/**
 * Created by Peter on 10/25/2017.
 */

public class DeleteGoal extends AsyncTask<Goal, Void, Void> {
    private Context context;
    public DeleteGoal(Context context) {
        this.context = context;
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
            }
        }
        return null;
    }
}
