package us.mindbuilders.petemit.timegoalie.TimeGoalieDO;

import android.app.PendingIntent;
import android.os.CountDownTimer;

/**
 * Created by Peter on 9/30/2017.
 */

public class TimeGoalieAlarmObject {
    private long goal_id;
    private PendingIntent alarmDonePendingIntent;
    private PendingIntent oneMinuteWarningPendingIntent;
    private CountDownTimer countDownTimer;
    private int secondsElapsed;
    private long targetTime;
    private boolean isRunning;
    private int goalAugment;
    private boolean isHasFinished;
    private boolean isHasBeenWarned;
    private String date;
    private int hasSucceeded;

    public TimeGoalieAlarmObject(long goal_id, String date) {
        this.goal_id = goal_id;
        this.date = date;
    }

    public long getGoal_id() {
        return goal_id;
    }

    public void setGoal_id(long goal_id) {
        this.goal_id = goal_id;
    }

    public PendingIntent getAlarmDonePendingIntent() {
        return alarmDonePendingIntent;
    }

    public void setAlarmDonePendingIntent(PendingIntent pi) {
        this.alarmDonePendingIntent = pi;
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

    public long getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(long targetTime) {
        this.targetTime = targetTime;
    }

    public boolean isHasFinished() {
        return isHasFinished;
    }

    public void setHasFinished(boolean hasFinished) {
        isHasFinished = hasFinished;
    }

    public int getGoalAugment() {
        return goalAugment;
    }

    public void setGoalAugment(int goalAugment) {
        this.goalAugment = goalAugment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public PendingIntent getOneMinuteWarningPendingIntent() {
        return oneMinuteWarningPendingIntent;
    }

    public void setOneMinuteWarningPendingIntent(PendingIntent oneMinuteWarningPendingIntent) {
        this.oneMinuteWarningPendingIntent = oneMinuteWarningPendingIntent;
    }

    public boolean isHasBeenWarned() {
        return isHasBeenWarned;
    }

    public void setHasBeenWarned(boolean hasBeenWarned) {
        isHasBeenWarned = hasBeenWarned;
    }


    public int getHasSucceeded() {
        return hasSucceeded;
    }

    public void setHasSucceeded(int hasSucceeded) {
        this.hasSucceeded = hasSucceeded;
    }
}
