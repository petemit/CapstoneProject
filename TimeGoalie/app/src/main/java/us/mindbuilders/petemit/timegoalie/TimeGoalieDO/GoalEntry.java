package us.mindbuilders.petemit.timegoalie.TimeGoalieDO;

import java.sql.Date;

/**
 * Created by Peter on 9/23/2017.
 */

public class GoalEntry {
    private long id;
    private String date;
    private int secondsElapsed;
    private long goal_id;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSecondsElapsed() {
        return secondsElapsed;
    }

    public void setSecondsElapsed(int secondsElapsed) {
        this.secondsElapsed = secondsElapsed;
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
