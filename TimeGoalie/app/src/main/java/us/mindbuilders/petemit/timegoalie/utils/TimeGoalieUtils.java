package us.mindbuilders.petemit.timegoalie.utils;

import android.view.View;
import android.widget.TextView;

import us.mindbuilders.petemit.timegoalie.BaseApplication;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.TimeGoalieAlarmObject;

/**
 * Created by Peter on 10/12/2017.
 */

public class TimeGoalieUtils {

    public static TimeGoalieAlarmObject getTimeGoalieAlarmObjectByDate(Goal goal) {

        if (BaseApplication.getTimeGoalieAlarmObjectById(goal.getGoalId(),
                TimeGoalieDateUtils.getSqlDateString(
                        BaseApplication.getActiveCalendarDate())) != null) {
            TimeGoalieAlarmObject timeGoalieAlarmObj =
                    BaseApplication.getTimeGoalieAlarmObjectById(goal.getGoalId(),
                            TimeGoalieDateUtils.
                                    getSqlDateString(BaseApplication.getActiveCalendarDate()));
            return timeGoalieAlarmObj;
        } else {




            return null;
        }

    }
    public static long getRemainingSeconds(TimeGoalieAlarmObject timeGoalieAlarmObj, Goal goal) {
        long onBindElapsedSeconds = 0;
        if (timeGoalieAlarmObj != null) {

            if (timeGoalieAlarmObj.getTargetTime() != 0 && timeGoalieAlarmObj.isRunning()) {


                goal.getGoalEntry().setSecondsElapsed((TimeGoalieDateUtils.calculateSecondsElapsed(
                        timeGoalieAlarmObj.getTargetTime(),
                        TimeGoalieDateUtils.getCurrentTimeInMillis(),
                        goal.getHours(),
                        goal.getMinutes(),
                        goal.getGoalEntry().getGoalAugment())), true);
            }
            onBindElapsedSeconds = (timeGoalieAlarmObj.getSecondsElapsed());
        }
        long remainingSeconds = (goal.getGoalSeconds() -
                onBindElapsedSeconds);
        return remainingSeconds;
    }

    public static void setTimeTextLabel(Goal goal, TimeGoalieAlarmObject timeGoalieAlarmObj,
                                        TextView tv_timeText, TextView tv_timeOutOf) {

        if (timeGoalieAlarmObj != null) {
            //Recalculate Elapsed Seconds
            long remainingSeconds = getRemainingSeconds(timeGoalieAlarmObj, goal);

            //Set initial Time Text labels:

            // if this is a more goal
            if (goal.getGoalTypeId() == 0) {
                tv_timeOutOf.setText(" / " + TimeGoalieAlarmManager.makeTimeTextFromMillis(
                        goal.getGoalSeconds() * 1000
                ));

                //  holder.time_tv.setText(TimeGoalieAlarmManager.makeTimeTextFromMillis(0));
                if (goal.getGoalEntry() != null) {
                    tv_timeText.setText(TimeGoalieAlarmManager.
                            makeTimeTextFromMillis(goal.getGoalEntry().getSecondsElapsed() * 1000));
                } else {
                    tv_timeText.setText(TimeGoalieAlarmManager.makeTimeTextFromMillis(0));
                }

            } else {

                if (remainingSeconds < 0) {

                } else {
                    if (tv_timeOutOf != null) {
                        tv_timeOutOf.setVisibility(View.GONE);
                    }
                    tv_timeText.setText(TimeGoalieAlarmManager.makeTimeTextFromMillis(remainingSeconds * 1000));
                }
            }
        }
    }
}
