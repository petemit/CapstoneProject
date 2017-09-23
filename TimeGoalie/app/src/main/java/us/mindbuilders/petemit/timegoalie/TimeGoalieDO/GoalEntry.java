package us.mindbuilders.petemit.timegoalie.TimeGoalieDO;

import java.sql.Date;

/**
 * Created by Peter on 9/23/2017.
 */

public class GoalEntry {
    private Date date;
    private int secondsElapsed;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getSecondsElapsed() {
        return secondsElapsed;
    }

    public void setSecondsElapsed(int secondsElapsed) {
        this.secondsElapsed = secondsElapsed;
    }
}
