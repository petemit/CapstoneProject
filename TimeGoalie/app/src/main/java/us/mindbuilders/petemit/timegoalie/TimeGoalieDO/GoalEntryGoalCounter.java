package us.mindbuilders.petemit.timegoalie.TimeGoalieDO;

import us.mindbuilders.petemit.timegoalie.GoalRecyclerViewAdapter;

/**
 * Created by Peter on 10/10/2017.
 */

public class GoalEntryGoalCounter {
    private GoalRecyclerViewAdapter.GoalCounter gc;
    private GoalEntry goalEntry;
    private String date;

    public GoalEntryGoalCounter(GoalRecyclerViewAdapter.GoalCounter gc, String date){
        this.gc=gc;
        this.date=date;
    }

    public GoalRecyclerViewAdapter.GoalCounter getGc() {
        return gc;
    }

    public void setGc(GoalRecyclerViewAdapter.GoalCounter gc) {
        this.gc = gc;
    }

    public GoalEntry getGoalEntry() {
        return goalEntry;
    }

    public void setGoalEntry(GoalEntry goalEntry) {
        this.goalEntry = goalEntry;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
