package us.mindbuilders.petemit.timegoalie.utils;

import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import us.mindbuilders.petemit.timegoalie.BaseApplication;
import us.mindbuilders.petemit.timegoalie.R;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Day;
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
            onBindElapsedSeconds =  TimeGoalieDateUtils.calculateSecondsElapsed(goal.getGoalEntry().getStartedTime() ,
                    goal.getGoalEntry().getSecondsElapsed());
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

    public static String getCommaSeparatedList(Goal goal, String goaldaysCommaSeparated) {
        if (goal.getGoalDays() != null && goal.getGoalDays().size() > 0) {

            for (int i = 0; i < goal.getGoalDays().size(); i++) {
                if (i == 0) {
                    goaldaysCommaSeparated.concat(goal.getGoalDays().get(i).getName());
                } else {
                    goaldaysCommaSeparated.concat(",");
                    goaldaysCommaSeparated.concat(goal.getGoalDays().get(i).getName());
                }
            }
        }
        return goaldaysCommaSeparated;
    }

    String[] dayNames;


    public static ArrayList<Day> parseCommaSeparated(String[] dayNames, String list) {
        ArrayList<Day> days = new ArrayList<>();
        String[] strings = list.split(",");
        for (int i = 0; i < strings.length; i++) {
            Day day = new Day();
            day.setSequence(getDaySeq(dayNames, strings[i]));
            day.setName(strings[i]);
            days.add(day);
        }
        return days;
    }

    public static int getDaySeq(String[] dayNames, String day) {
        for (int i = 0; i < dayNames.length; i++) {
            if (day.equals(dayNames[i])){
                return i+1;
            }
        }
        return 0;
    }
}
