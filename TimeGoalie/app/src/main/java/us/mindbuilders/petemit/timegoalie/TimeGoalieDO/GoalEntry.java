package us.mindbuilders.petemit.timegoalie.TimeGoalieDO;

import java.sql.Date;

import us.mindbuilders.petemit.timegoalie.BaseApplication;

/**
 * Created by Peter on 9/23/2017.
 */

public class GoalEntry {
    private long id;
    private String date;
    private long goal_id;

    public GoalEntry(){
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSecondsElapsed() {

        if (BaseApplication.getTimeGoalieAlarmObjectById(goal_id)!=null) {
            return BaseApplication.getTimeGoalieAlarmObjectById(goal_id).getSecondsElapsed();
        }
        return 0;
    }

    public void addSecondElapsed() {
        if (BaseApplication.getTimeGoalieAlarmObjectById(goal_id)!=null) {
            BaseApplication.getTimeGoalieAlarmObjectById(goal_id).setSecondsElapsed(
                    BaseApplication.getTimeGoalieAlarmObjectById(goal_id).getSecondsElapsed()+1
            );
        }
    }

    public void setSecondsElapsed(int secondsElapsed) {
        if (BaseApplication.getTimeGoalieAlarmObjectById(goal_id)!=null) {
        }
        else {
            TimeGoalieAlarmObject timeGoalieAlarmObject = new TimeGoalieAlarmObject();
            timeGoalieAlarmObject.setSecondsElapsed(secondsElapsed);
            timeGoalieAlarmObject.setGoal_id(goal_id);
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
}
