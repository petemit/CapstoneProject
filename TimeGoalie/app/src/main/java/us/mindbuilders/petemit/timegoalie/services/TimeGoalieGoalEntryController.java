package us.mindbuilders.petemit.timegoalie.services;

import android.os.Handler;

import java.util.ArrayList;

import us.mindbuilders.petemit.timegoalie.BaseApplication;
import us.mindbuilders.petemit.timegoalie.GoalListViewCallback;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.GoalEntry;
import us.mindbuilders.petemit.timegoalie.data.InsertNewGoalEntry;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieDateUtils;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieUtils;


/**
 * Created by Peter on 3/20/2018.
 */

public class TimeGoalieGoalEntryController {

    private GoalListViewCallback viewCallback;
    private Handler engine;
    private ArrayList<Goal> goals;
    private boolean isEngineRunning = false;
    Runnable runnable =  new Runnable() {
        @Override
        public void run() {
            // Run every second until there are no goal entries running
            if (updateGoalEntries(goals)) {
                engine.postDelayed(this, tick);
            }
            else {
                engine.removeCallbacks(this);
            }
        }
    };
    private static int tick = 1000;


    public TimeGoalieGoalEntryController() {
        engine = new Handler();
    }

    public void startEngine(ArrayList<Goal> goalList) {
        goals = goalList;
        engine.post(runnable);
    }

    synchronized public boolean updateGoalEntries (ArrayList<Goal> goals) {

        isEngineRunning = true;
        boolean keepEngineRunning = false;
        if (goals == null) {
            return false;
        }
        for (int i = 0; i < goals.size(); i++) {
            Goal goal = goals.get(i);
            if (null == goal.getGoalEntry()) {
                continue;
            }

            GoalEntry entry = goal.getGoalEntry();

            //First off.. is Goal Running?
            if (entry.isRunning()) {
                keepEngineRunning = true;
                //Increment Goal
                addSecondToGoal(entry, i);

            }
        }
        isEngineRunning = false;
        return keepEngineRunning;
    }

    public void setGoalListViewCallback(GoalListViewCallback callback) {
        this.viewCallback = callback;
    }

    public void addSecondToGoal(GoalEntry goalEntry, int position) {
        //goalEntry.setSecondsElapsed(TimeGoalieDateUtils.calculateSecondsElapsed(goalEntry.getStartedTime(),goalEntry.getSecondsElapsed()));
       // new InsertNewGoalEntry(BaseApplication.getContext()).execute(goalEntry);
        if (null != viewCallback) {
            viewCallback.update(position);
        }
    }

    public void startGoal(GoalEntry goalEntry) {
        goalEntry.setRunning(true);
        goalEntry.setStartedTime(TimeGoalieDateUtils.getCurrentTimeInMillis());
        if (!isEngineRunning) {
            startEngine(goals);
        }
        new InsertNewGoalEntry(BaseApplication.getContext()).execute(goalEntry);
    }

    public void stopGoal(GoalEntry goalEntry) {
        goalEntry.setSecondsElapsed(TimeGoalieDateUtils.calculateSecondsElapsed(goalEntry.getStartedTime(),goalEntry.getSecondsElapsed()));
        goalEntry.setRunning(false);
        new InsertNewGoalEntry(BaseApplication.getContext()).execute(goalEntry);
    }
}
