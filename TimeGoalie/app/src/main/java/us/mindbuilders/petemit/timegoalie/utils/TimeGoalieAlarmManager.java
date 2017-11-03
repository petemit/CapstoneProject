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
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.TimeGoalieAlarmObject;
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
                // TODO: 10/18/2017 am I really going to do this?
                //  goalEntry.addSecondElapsed();
                //     new InsertNewGoalEntry(tv.getContext()).execute(goalEntry);

                //set Progress bar Progress
                if (seekbar != null && !goal.getGoalEntry().isHasFinished()) {

                    ObjectAnimator animation = ObjectAnimator.ofInt(seekbar, "progress",
                            seekbar.getProgress(), (int)
                                    ((1 - ((double) (millisuntilfinished / 1000) / totalSeconds))
                                            * 100 * 100));
                    animation.setDuration(2000);
                    animation.setInterpolator(new LinearInterpolator());
                    animation.start();
                }
                if (seekbar != null && goal.getGoalEntry().isHasFinished()) {
                    seekbar.setProgress(10000);
                }
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

                        if (BaseApplication.getTimeGoalieAlarmObjectById(goal.getGoalEntry()
                                .getGoal_id()) != null) {
                            TimeGoalieAlarmObject timeGoalieAlarmObject =
                                    BaseApplication.getTimeGoalieAlarmObjectById(
                                            goal.getGoalEntry().getGoal_id());
                            timeGoalieAlarmObject.getCountDownTimer().cancel();
                            if (goal.getGoalEntry().getDate().equals(TimeGoalieDateUtils
                                    .getSqlDateString())) {
                                timeGoalieAlarmObject.setCountDownTimer(makeCountdownTimer(goalCounter,
                                        1000,
                                        intervalInSeconds,
                                        totalSeconds,
                                        tv,
                                        goal,
                                        goalType,
                                        seekbar).start());
                            }
                        }

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
                        if (BaseApplication.getTimeGoalieAlarmObjectById(goal.getGoalEntry().getGoal_id()) != null) {
                            TimeGoalieAlarmObject timeGoalieAlarmObject =
                                    BaseApplication.getTimeGoalieAlarmObjectById(
                                            goal.getGoalEntry().getGoal_id());
                            timeGoalieAlarmObject.getCountDownTimer().cancel();
                            if (goal.getGoalEntry().getDate().equals(TimeGoalieDateUtils.getSqlDateString())) {
                                timeGoalieAlarmObject.setCountDownTimer(makeCountdownTimer(goalCounter,
                                        1000,
                                        intervalInSeconds,
                                        totalSeconds,
                                        tv,
                                        goal,
                                        goalType,
                                        seekbar).start());
                            }
                        }

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


    public static void startTimer(GoalRecyclerViewAdapter.GoalCounter goalCounter,
                                  TextView time_tv,
                                  long totalSeconds,
                                  Goal goal,
                                  Context context,
                                  SeekBar seekbar) {
        if (goal.getGoalEntry().getDate() == null) {
            goal.getGoalEntry().setDate(TimeGoalieDateUtils.getSqlDateString());
        }
        TimeGoalieAlarmObject timeGoalieAlarmObject =
                BaseApplication.getTimeGoalieAlarmObjectById((goal.getGoalId()));

        long remainingSeconds = totalSeconds;// - secondsElapsed;
        Log.e("Mindbuilders", "remainingseconds: " + remainingSeconds);
        if (timeGoalieAlarmObject != null) {
            if (seekbar != null) {
                timeGoalieAlarmObject.setCountDownTimer(
                        TimeGoalieAlarmManager.makeCountdownTimer(goalCounter,
                                remainingSeconds,
                                1,
                                goal.getGoalSeconds(),
                                time_tv,
                                goal,
                                (int) goal.getGoalTypeId(),
                                seekbar));
                timeGoalieAlarmObject.getCountDownTimer().start();
            }
            goal.getGoalEntry().setRunning(true);

        } else {
            if (seekbar != null) {
                timeGoalieAlarmObject = new TimeGoalieAlarmObject(goal.getGoalId(),
                        TimeGoalieDateUtils.getSqlDateString());
                timeGoalieAlarmObject.setCountDownTimer(
                        TimeGoalieAlarmManager.makeCountdownTimer(goalCounter,
                                remainingSeconds,
                                1,
                                goal.getGoalSeconds(),
                                time_tv,
                                goal,
                                (int) goal.getGoalTypeId(),
                                seekbar));
                timeGoalieAlarmObject.getCountDownTimer().start();
            }
            goal.getGoalEntry().setRunning(true);

            BaseApplication.getTimeGoalieAlarmObjects().add(timeGoalieAlarmObject);
        }

        //     new InsertNewGoalEntry(time_tv.getContext()).execute(goal.getGoalEntry());

        // this will create the system alarm.  :-O !  It will not create it if the pi
        // already exists, or if the goal has already finished

        // preemptively cancel secondly
        //  TimeGoalieAlarmReceiver.cancelSecondlyAlarm(context,goal);

        if (timeGoalieAlarmObject != null && timeGoalieAlarmObject.getAlarmDonePendingIntent() == null &&
                !goal.getGoalEntry().isHasFinished()) {
            timeGoalieAlarmObject.setAlarmDonePendingIntent(TimeGoalieAlarmReceiver.createTimeGoaliePendingIntent(
                    context, TimeGoalieAlarmReceiver.createAlarmDoneTimeGoalieAlarmIntent
                            (context,
                                    goal.getName(),
                                    (int) goal.getGoalId()
                            ), (int) goal.getGoalId()));


            long hours = remainingSeconds / (60 * 60);
            long minutes = (remainingSeconds - (hours * 60 * 60)) / 60;
            long seconds = (remainingSeconds - (hours * 60 * 60) - (minutes * 60));

            Log.e("Mindbuilders", "hours: " + hours);
            Log.e("Mindbuilders", "minutes: " + minutes);
            Log.e("Mindbuilders", "seconds: " + seconds);


            long targetTime = TimeGoalieDateUtils.createTargetCalendarTime(
                    (int) hours,
                    (int) minutes,
                    (int) seconds);


            //sound the alarm!!
//            if (timeGoalieAlarmObject.getTargetTime() == 0) {
//                timeGoalieAlarmObject.setTargetTime(targetTime);
//            }

            if (goal.getGoalEntry().getTargetTime() == 0) {
                goal.getGoalEntry().setTargetTime(targetTime);
            }

            if (!goal.getGoalEntry().isHasFinished()) {
                TimeGoalieAlarmManager.setTimeGoalAlarm(
                        targetTime,
                        context, null,
                        timeGoalieAlarmObject.getAlarmDonePendingIntent());
            }


            if (BaseApplication.getSecondlyHandler() != null) {
                BaseApplication.destroyHandler();
            }
            BaseApplication.createHandler(TimeGoalieAlarmReceiver.SECONDLY_FREQUENCY);

            TimeGoalieAlarmManager.setTimeGoalAlarm(
                    TimeGoalieDateUtils.createTargetSecondlyCalendarTime((int)
                            TimeGoalieAlarmReceiver.SECONDLY_FREQUENCY / 1000),
                    context, null,
                    TimeGoalieAlarmReceiver.createSecondlyTimeGoaliePendingIntent(
                            context,
                            TimeGoalieAlarmReceiver.
                                    createEverySecondDbUpdateAlarmIntent(context)));


            //Sets up the running out of time alert
            //will not fire if it has been fired already for a goal.
            if (goal.getGoalTypeId() == 1 && !timeGoalieAlarmObject.isHasBeenWarned()) { //Limit Goal Type
                timeGoalieAlarmObject.setOneMinuteWarningPendingIntent(TimeGoalieAlarmReceiver.
                        createTimeGoaliePendingIntent(context,
                                TimeGoalieAlarmReceiver.createOneMinuteWarningTimeGoalieAlarmIntent(
                                        context,
                                        goal.getName(),
                                        (int) goal.getGoalId()
                                ),
                                (int) goal.getGoalId()));

                long targetTimeLimitGoal = TimeGoalieDateUtils.createTargetCalendarTime(
                        (int) hours,
                        (int) minutes,
                        (int) seconds - ONE_MINUTE_WARNING_TIME * 60);

                if (timeGoalieAlarmObject.getOneMinuteWarningPendingIntent() != null) {
                    TimeGoalieAlarmManager.setTimeGoalAlarm(
                            targetTimeLimitGoal,
                            context, null,
                            timeGoalieAlarmObject.getOneMinuteWarningPendingIntent());
                }

            }

        }
    }

}
