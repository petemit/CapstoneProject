package us.mindbuilders.petemit.timegoalie.TimeGoalieDO;

import android.app.PendingIntent;
import android.os.CountDownTimer;

/**
 * Created by Peter on 9/30/2017.
 */

public class TimeGoalieAlarmObject {
    private long goal_id;
    private PendingIntent pi;
    private CountDownTimer countDownTimer;
    private int secondsElapsed;
    private boolean isRunning;

    public long getGoal_id() {
        return goal_id;
    }

    public void setGoal_id(long goal_id) {
        this.goal_id = goal_id;
    }

    public PendingIntent getPi() {
        return pi;
    }

    public void setPi(PendingIntent pi) {
        this.pi = pi;
    }

    public CountDownTimer getCountDownTimer() {
        return countDownTimer;
    }

    public void setCountDownTimer(CountDownTimer countDownTimer) {
        this.countDownTimer = countDownTimer;
    }

    public int getSecondsElapsed() {
        return secondsElapsed;
    }

    public void setSecondsElapsed(int secondsElapsed) {
        this.secondsElapsed = secondsElapsed;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }
}
