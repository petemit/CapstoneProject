package us.mindbuilders.petemit.timegoalie.utils;

import android.view.View;
import android.widget.TextView;

import us.mindbuilders.petemit.timegoalie.BaseApplication;
import us.mindbuilders.petemit.timegoalie.R;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;


/**
 * Created by Peter on 10/12/2017.
 */

public class TimeGoalieUtils {


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

            switch ((int) goal.getGoalTypeId()) {
                case 0: //Time Goal To Encourage
                    if (tv_timeOutOf != null) {
                        String result = " / " + TimeGoalieAlarmManager.makeTimeTextFromMillis(
                                goal.getGoalSeconds() * 1000);
                        tv_timeOutOf.setText(result);
                        tv_timeOutOf.setVisibility(View.VISIBLE);
                    }


                    //  holder.time_tv.setText(TimeGoalieAlarmManager.makeTimeTextFromMillis(0));
                    if (goal.getGoalEntry() != null) {
                        int secondsElapsed =  TimeGoalieDateUtils.calculateSecondsElapsed(goal.getGoalEntry().getStartedTime() ,
                                goal.getGoalEntry().getSecondsElapsed());
                        tv_timeText.setText(TimeGoalieAlarmManager.
                                makeTimeTextFromMillis(secondsElapsed * 1000));
                    } else {
                        tv_timeText.setText(TimeGoalieAlarmManager.makeTimeTextFromMillis(0));
                    }
                    break;
                case 1: //Time Goal to Limit

                    if (tv_timeOutOf != null) {
                        tv_timeOutOf.setText(tv_timeText.getContext().getString(R.string.less_left_thing));
                    }

                    if (remainingSeconds < 0) {
                        int secondsElapsed =  TimeGoalieDateUtils.calculateSecondsElapsed(goal.getGoalEntry().getStartedTime() ,
                                goal.getGoalEntry().getSecondsElapsed());
                        tv_timeText.setText("-" + TimeGoalieAlarmManager.makeTimeTextFromMillis(-1 *
                                (goal.getGoalSeconds() * 1000 - secondsElapsed * 1000)));

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
