package us.mindbuilders.petemit.timegoalie.TimeGoalieDO;

import java.sql.Date;

import us.mindbuilders.petemit.timegoalie.BaseApplication;
import us.mindbuilders.petemit.timegoalie.data.InsertNewGoalEntry;

/**
 * Created by Peter on 9/23/2017.
 */

public class GoalEntry {
    private long id;
    private String date = "";
    private int goalAugment;
    private long goal_id;
    private boolean hasFinished;

    public GoalEntry(long goal_id, String date) {
        this.date = date;
        this.goal_id = goal_id;

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSecondsElapsed() {

        if (BaseApplication.getTimeGoalieAlarmObjectById(goal_id,date) != null) {
            return BaseApplication.getTimeGoalieAlarmObjectById(goal_id,date).getSecondsElapsed();
        }
        return 0;
    }

    public void addSecondElapsed() {
        if (BaseApplication.getTimeGoalieAlarmObjectById(goal_id,date) != null) {
            BaseApplication.getTimeGoalieAlarmObjectById(goal_id,date ).setSecondsElapsed(
                    BaseApplication.getTimeGoalieAlarmObjectById(goal_id,date).getSecondsElapsed() + 1
            );
        }
    }

    public void setSecondsElapsed(int secondsElapsed) {
        if (BaseApplication.getTimeGoalieAlarmObjectById(goal_id,date) != null) {
        } else {
            TimeGoalieAlarmObject timeGoalieAlarmObject = new TimeGoalieAlarmObject(goal_id, date);
            timeGoalieAlarmObject.setSecondsElapsed(secondsElapsed);
            timeGoalieAlarmObject.setGoal_id(goal_id);
            BaseApplication.getTimeGoalieAlarmObjects().add(timeGoalieAlarmObject
            );
        }
    }

    public void setSecondsElapsed(int secondsElapsed, boolean isAlarm) {
        if (BaseApplication.getTimeGoalieAlarmObjectById(goal_id, date) != null) {
            if (isAlarm) {
                TimeGoalieAlarmObject timeGoalieAlarmObject =
                        BaseApplication.getTimeGoalieAlarmObjectById(goal_id,date);
                timeGoalieAlarmObject.setSecondsElapsed(secondsElapsed);
            }
        } else {
            TimeGoalieAlarmObject timeGoalieAlarmObject = new TimeGoalieAlarmObject(goal_id, date);
            timeGoalieAlarmObject.setSecondsElapsed(secondsElapsed);
            BaseApplication.getTimeGoalieAlarmObjects().add(timeGoalieAlarmObject
            );
        }
    }

    public long getGoal_id() {
        return goal_id;
    }

    public void setGoal_id(long goal_id) {
        this.goal_id = goal_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isHasFinished() {
        if (BaseApplication.getTimeGoalieAlarmObjectById(goal_id ,date) != null) {
            return BaseApplication.getTimeGoalieAlarmObjectById(goal_id,date).isHasFinished();
        }

        return hasFinished;
    }

    public void setHasFinished(boolean hasFinished) {
        if (BaseApplication.getTimeGoalieAlarmObjectById(goal_id,date) != null) {
            BaseApplication.getTimeGoalieAlarmObjectById(goal_id,date).setHasFinished(hasFinished);
        }
    }

    public int getGoalAugment() {
        if (BaseApplication.getTimeGoalieAlarmObjectById(goal_id,date) != null) {
            return BaseApplication.getTimeGoalieAlarmObjectById(goal_id,date).getGoalAugment();
        }
        return 0;
    }

    public void setGoalAugment(int goalAugment) {
        if (BaseApplication.getTimeGoalieAlarmObjectById(goal_id,date) != null) {
            BaseApplication.getTimeGoalieAlarmObjectById(goal_id,date).setGoalAugment(goalAugment);
        }
    }
}
