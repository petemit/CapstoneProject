package us.mindbuilders.petemit.timegoalie.utils;

import android.view.View;
import android.widget.TextView;

import us.mindbuilders.petemit.timegoalie.BaseApplication;
import us.mindbuilders.petemit.timegoalie.R;
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
    public static long getRemainingSeconds(Goal goal) {
        long onBindElapsedSeconds = 0;
        if (goal.getGoalEntry() != null) {
//
//            if (goal.getGoalEntry().getTargetTime() != 0 && goal.getGoalEntry().isRunning()) {
//
//
//                goal.getGoalEntry().setSecondsElapsed((TimeGoalieDateUtils.calculateSecondsElapsed(
//                       goal.getGoalEntry().getTargetTime(),
//                        TimeGoalieDateUtils.getCurrentTimeInMillis(),
//                        goal.getHours(),
//                        goal.getMinutes(),
//                        goal.getGoalEntry().getGoalAugment())), true);
//            }
            onBindElapsedSeconds = (goal.getGoalEntry().getSecondsElapsed());
        }
        long remainingSeconds = (goal.getGoalSeconds() -
                onBindElapsedSeconds);
        return remainingSeconds;
    }

    public static void setTimeTextLabel(Goal goal, TextView tv_timeText, TextView tv_timeOutOf) {

        if (goal.getGoalEntry() != null) {
            long remainingSeconds = getRemainingSeconds(goal);
          //  long remainingSeconds = goal.getGoalSeconds() - goal.getGoalEntry().getSecondsElapsed();

            switch ((int)goal.getGoalTypeId()) {
                case 0: //Time Goal To Encourage
                    if (tv_timeOutOf != null) {
                        tv_timeOutOf.setText(" / " + TimeGoalieAlarmManager.makeTimeTextFromMillis(
                                goal.getGoalSeconds() * 1000
                        ));
                        tv_timeOutOf.setVisibility(View.VISIBLE);
                    }


                    //  holder.time_tv.setText(TimeGoalieAlarmManager.makeTimeTextFromMillis(0));
                    if (goal.getGoalEntry() != null) {
                        tv_timeText.setText(TimeGoalieAlarmManager.
                                makeTimeTextFromMillis(goal.getGoalEntry().getSecondsElapsed() * 1000));
                    } else {
                        tv_timeText.setText(TimeGoalieAlarmManager.makeTimeTextFromMillis(0));
                    }
                    break;
                case 1: //Time Goal to Limit

                    if (tv_timeOutOf != null) {
                        tv_timeOutOf.setText(tv_timeText.getContext().getString(R.string.less_left_thing));
                    }

                    if (remainingSeconds < 0) {
                        tv_timeText.setText("-" + TimeGoalieAlarmManager.makeTimeTextFromMillis(-1 *
                                (goal.getGoalSeconds() * 1000 - goal.getGoalEntry().getSecondsElapsed() * 1000)));

                    } else {
                        tv_timeText.setText(TimeGoalieAlarmManager.makeTimeTextFromMillis(remainingSeconds * 1000));
                        }

                    break;
            }
            //Recalculate Elapsed Seconds


            //Set initial Time Text labels:

            // if this is a more goal
            if (goal.getGoalTypeId() == 0) {


            } else {


            }
        }
    }
}
