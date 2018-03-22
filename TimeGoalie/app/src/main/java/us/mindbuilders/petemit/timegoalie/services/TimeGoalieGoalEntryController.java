package us.mindbuilders.petemit.timegoalie.services;

import java.util.ArrayList;

import us.mindbuilders.petemit.timegoalie.GoalListViewCallback;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.GoalEntry;


/**
 * Created by Peter on 3/20/2018.
 */

public class TimeGoalieGoalEntryController {

    private GoalListViewCallback viewCallback;

    public void updateGoalEntries (ArrayList<Goal> goals) {
        for (Goal goal: goals
             ) {
            GoalEntry entry = null;
            if (null == goal.getGoalEntry()) {
                return;
            }
            entry = goal.getGoalEntry();

            //First off.. is Goal Running?
            if (entry.isRunning()) {
                //
            }
        }

    }

    public void setGoalListViewCallback(GoalListViewCallback callback) {
        this.viewCallback = callback;
    }
}
