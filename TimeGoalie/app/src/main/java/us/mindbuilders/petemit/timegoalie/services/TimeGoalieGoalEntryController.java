package us.mindbuilders.petemit.timegoalie.services;

import java.util.ArrayList;

import us.mindbuilders.petemit.timegoalie.GoalListViewCallback;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.GoalEntry;
import us.mindbuilders.petemit.timegoalie.data.TimeGoalieContract;

/**
 * Created by Peter on 3/20/2018.
 */

public class TimeGoalieGoalEntryController {

    private GoalListViewCallback viewCallback;

    public void updateGoalEntries (ArrayList<GoalEntry> entries) {

    }

    public void setGoalListViewCallback(GoalListViewCallback callback) {
        this.viewCallback = callback;
    }
}
