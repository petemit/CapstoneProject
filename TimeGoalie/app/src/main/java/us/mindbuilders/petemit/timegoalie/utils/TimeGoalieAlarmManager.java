package us.mindbuilders.petemit.timegoalie.utils;

import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

import us.mindbuilders.petemit.timegoalie.BaseApplication;
import us.mindbuilders.petemit.timegoalie.GoalRecyclerViewAdapter;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.GoalEntryGoalCounter;
import us.mindbuilders.petemit.timegoalie.data.GetSuccessfulGoalCount;
import us.mindbuilders.petemit.timegoalie.services.TimeGoalieAlarmReceiver;

/**
 * Created by Peter on 9/29/2017.
 */

public class TimeGoalieAlarmManager {

    public static final int ONE_MINUTE_WARNING_TIME = 1;

    public static void setTimeGoalAlarm(long futureTimeInMillis,
                                        Context context, @Nullable Bundle extras, PendingIntent alarmPendingIntent) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, futureTimeInMillis, alarmPendingIntent);
    }

    public static void cancelTimeGoalAlarm(Context context, PendingIntent alarmPendingIntent) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmMgr.cancel(alarmPendingIntent);
    }

    public static CountDownTimer makeCountdownTimer(final GoalRecyclerViewAdapter.GoalCounter goalCounter,
                                                    long secondsInFuture,
                                                    final long intervalInSeconds,
                                                    final long totalSeconds,
                                                    final TextView tv,
                                                    final Goal goal,
                                                    final int goalType,
                                                    final SeekBar seekbar) {

        long milliInFuture = secondsInFuture * 1000;

        long intervalInSecondsInMilli = intervalInSeconds * 1000;
        final CountDownTimer cdTimer = new CountDownTimer(milliInFuture, intervalInSecondsInMilli) {
            @Override
            public void onTick(long millisuntilfinished) {
                Log.e("mindbuilders1", goal.getName() + " tick " + goal.getGoalEntry().getSecondsElapsed());

                TimeGoalieUtils.setTimeTextLabel(goal, tv, null);

                Log.e("mindbuilders2", goal.getName() + " tick "
                        + goal.getGoalEntry().getSecondsElapsed());
            }

            @Override
            public void onFinish() {
                GoalEntryGoalCounter goalEntryGoalCounter =
                        new GoalEntryGoalCounter(goalCounter,
                                TimeGoalieDateUtils.getSqlDateString
                                        (BaseApplication.getActiveCalendarDate()));
                switch (goalType) {
                    case 0:
//                        if (!goalEntry.isHasFinished()) {
//                            new InsertNewGoalEntry(tv.getContext()).execute(goalEntry);
//                        }
                        goal.getGoalEntry().setHasSucceeded(1);
                        goal.getGoalEntry().setHasFinished(true);
                        goalEntryGoalCounter.setGoalEntry(goal.getGoalEntry());

                        new GetSuccessfulGoalCount(tv.getContext()).execute(goalEntryGoalCounter);



                        //  makeCountdownTimer(1000,intervalInSeconds,totalSeconds,tv,goalEntry,goalType,seekbar).start();
                        //  tv.setText("00:00:00");
                        break;
                    case 1:
//                        if (!goalEntry.isHasFinished()) {
//                            new InsertNewGoalEntry(tv.getContext()).execute(goalEntry);
//
//                        }
                        //  tv.setText(makeTimeTextFromMillis(totalSeconds / 1000));
                        goal.getGoalEntry().setHasSucceeded(0);
                        goal.getGoalEntry().setHasFinished(true);
                        goalEntryGoalCounter.setGoalEntry(goal.getGoalEntry());

                        new GetSuccessfulGoalCount(tv.getContext()).execute(goalEntryGoalCounter);
                        break;
                }




              /*  if (seekbar != null) {
                    seekbar.setProgress(10000);
                }*/

            }
        };
        return cdTimer;
    }


    public static String makeTimeTextFromMillis(long millis) {
        int seconds = (int) (millis) / 1000;
        int hours = seconds / (60 * 60);
        int minutes = (seconds - (hours * 60 * 60)) / 60;
        seconds = (seconds - (hours * 60 * 60) - (minutes * 60));

        return (String.format(Locale.US, "%02d", hours)
                + ":" + String.format(Locale.US, "%02d", minutes)
                + ":" + String.format(Locale.US, "%02d", seconds));
    }

    public static void startTimer(long totalSeconds,
                                  Goal goal,
                                  Context context) {
        if (goal.getGoalEntry().getDate() == null) {
            goal.getGoalEntry().setDate(TimeGoalieDateUtils.getSqlDateString());
        }


        long remainingSeconds = totalSeconds;// - secondsElapsed;
        Log.e("Mindbuilders", "remainingseconds: " + remainingSeconds);


        //     new InsertNewGoalEntry(time_tv.getContext()).execute(goal.getGoalEntry());

        // this will create the system alarm.  :-O !  It will not create it if the pi
        // already exists, or if the goal has already finished

        // preemptively cancel secondly
        //  TimeGoalieAlarmReceiver.cancelSecondlyAlarm(context,goal);






            //Sets up the running out of time alert
            //will not fire if it has been fired already for a goal.



    }

}
