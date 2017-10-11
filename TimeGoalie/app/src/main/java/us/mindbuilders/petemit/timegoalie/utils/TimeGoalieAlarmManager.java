package us.mindbuilders.petemit.timegoalie.utils;

import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.SeekBar;
import android.widget.TextView;

import us.mindbuilders.petemit.timegoalie.BaseApplication;
import us.mindbuilders.petemit.timegoalie.GoalRecyclerViewAdapter;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.GoalEntry;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.GoalEntryGoalCounter;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.TimeGoalieAlarmObject;
import us.mindbuilders.petemit.timegoalie.data.GetSuccessfulGoalCount;
import us.mindbuilders.petemit.timegoalie.data.InsertNewGoalEntry;
import us.mindbuilders.petemit.timegoalie.services.TimeGoalieAlarmReceiver;

/**
 * Created by Peter on 9/29/2017.
 */

public class TimeGoalieAlarmManager {

    private static final int ONE_MINUTE_WARNING_TIME = 1;

    public static void setTimeGoalAlarm(long futureTimeInMillis,
                                        Context context, @Nullable Bundle extras, PendingIntent alarmPendingIntent) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, futureTimeInMillis, alarmPendingIntent);
    }

    public static void cancelTimeGoalAlarm(Context context, PendingIntent alarmPendingIntent) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmMgr.cancel(alarmPendingIntent);
        Log.e("Mindbuilders", alarmMgr.getNextAlarmClock().getShowIntent().getCreatorPackage());
    }


    public static CountDownTimer makeCountdownTimer(final GoalRecyclerViewAdapter.GoalCounter goalCounter,
                                                    long secondsInFuture,
                                                    final long intervalInSeconds,
                                                    final long totalSeconds,
                                                    final TextView tv,
                                                    final GoalEntry goalEntry,
                                                    final int goalType,
                                                    final SeekBar seekbar) {
        long milliInFuture = secondsInFuture * 1000;
        final boolean hasFinished = goalEntry.isHasFinished();
        long intervalInSecondsInMilli = intervalInSeconds * 1000;
        final CountDownTimer cdTimer = new CountDownTimer(milliInFuture, intervalInSecondsInMilli) {
            @Override
            public void onTick(long millisuntilfinished) {

                switch (goalType) {
                    case 0: //Time Goal To Encourage
                        if (!goalEntry.isHasFinished()) {
                            tv.setText(makeTimeTextFromMillis(totalSeconds * 1000 - millisuntilfinished));
                        } else {
                            tv.setText(makeTimeTextFromMillis(goalEntry.getSecondsElapsed() * 1000 + totalSeconds));
                        }
                        break;
                    case 1: //Time Goal to Limit
                        if (!goalEntry.isHasFinished()) {
                            tv.setText(makeTimeTextFromMillis(millisuntilfinished));
                        } else {
                            tv.setText("-" + makeTimeTextFromMillis(-1 * (totalSeconds * 1000 - goalEntry.getSecondsElapsed() * 1000)));
                        }
                        break;
                }
                goalEntry.addSecondElapsed();
                new InsertNewGoalEntry(tv.getContext()).execute(goalEntry);

                //set Progress bar Progress
                if (seekbar != null && !goalEntry.isHasFinished()) {

                    ObjectAnimator animation = ObjectAnimator.ofInt(seekbar, "progress", seekbar.getProgress(), (int) ((1 - ((double) (millisuntilfinished / 1000) / totalSeconds)) * 100 * 100));
                    animation.setDuration(2000);
                    animation.setInterpolator(new LinearInterpolator());
                    animation.start();
                } else if (seekbar != null) {
                    seekbar.setProgress(10000);
                }

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
                        goalEntry.setHasSucceeded(1);
                        goalEntry.setHasFinished(true);
                        if (BaseApplication.getTimeGoalieAlarmObjectById(goalEntry.getGoal_id()) != null) {
                            TimeGoalieAlarmObject timeGoalieAlarmObject =
                                    BaseApplication.getTimeGoalieAlarmObjectById(
                                            goalEntry.getGoal_id());
                            timeGoalieAlarmObject.getCountDownTimer().cancel();
                            if (goalEntry.getDate().equals(TimeGoalieDateUtils.getSqlDateString())) {
                                timeGoalieAlarmObject.setCountDownTimer(makeCountdownTimer(goalCounter,
                                        1000,
                                        intervalInSeconds,
                                        totalSeconds,
                                        tv,
                                        goalEntry,
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
                        goalEntry.setHasSucceeded(0);
                        goalEntry.setHasFinished(true);
                        if (BaseApplication.getTimeGoalieAlarmObjectById(goalEntry.getGoal_id()) != null) {
                            TimeGoalieAlarmObject timeGoalieAlarmObject =
                                    BaseApplication.getTimeGoalieAlarmObjectById(
                                            goalEntry.getGoal_id());
                            timeGoalieAlarmObject.getCountDownTimer().cancel();
                            if (goalEntry.getDate().equals(TimeGoalieDateUtils.getSqlDateString())) {
                                timeGoalieAlarmObject.setCountDownTimer(makeCountdownTimer(goalCounter,
                                        1000,
                                        intervalInSeconds,
                                        totalSeconds,
                                        tv,
                                        goalEntry,
                                        goalType,
                                        seekbar).start());
                            }
                        }

                        break;
                }

                goalEntryGoalCounter.setGoalEntry(goalEntry);

                new GetSuccessfulGoalCount(tv.getContext()).execute(goalEntryGoalCounter);


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

        return (String.format("%02d", hours)
                + ":" + String.format("%02d", minutes)
                + ":" + String.format("%02d", seconds));
    }


    public static void startTimer(GoalRecyclerViewAdapter.GoalCounter goalCounter, TextView time_tv, long totalSeconds, Goal goal, View view, SeekBar seekbar) {
        if (goal.getGoalEntry().getDate() == null) {
            goal.getGoalEntry().setDate(TimeGoalieDateUtils.getSqlDateString());
        }
        TimeGoalieAlarmObject timeGoalieAlarmObject =
                BaseApplication.getTimeGoalieAlarmObjectById((goal.getGoalId()));

        long remainingSeconds = totalSeconds;// - secondsElapsed;
        Log.e("Mindbuilders", "remainingseconds: " + remainingSeconds);
        if (timeGoalieAlarmObject != null) {
            timeGoalieAlarmObject.setCountDownTimer(
                    TimeGoalieAlarmManager.makeCountdownTimer(goalCounter,
                            remainingSeconds,
                            1,
                            goal.getGoalSeconds(),
                            time_tv,
                            goal.getGoalEntry(),
                            (int) goal.getGoalTypeId(),
                            seekbar));
            timeGoalieAlarmObject.getCountDownTimer().start();
            timeGoalieAlarmObject.setRunning(true);
        } else {
            timeGoalieAlarmObject = new TimeGoalieAlarmObject(goal.getGoalId(),
                    TimeGoalieDateUtils.getSqlDateString());
            timeGoalieAlarmObject.setCountDownTimer(
                    TimeGoalieAlarmManager.makeCountdownTimer(goalCounter,
                            remainingSeconds,
                            1,
                            goal.getGoalSeconds(),
                            time_tv,
                            goal.getGoalEntry(),
                            (int) goal.getGoalTypeId(),
                            seekbar));
            timeGoalieAlarmObject.getCountDownTimer().start();
            timeGoalieAlarmObject.setRunning(true);
            BaseApplication.getTimeGoalieAlarmObjects().add(timeGoalieAlarmObject);
        }

        // this will create the system alarm.  :-O !  It will not create it if the pi
        // already exists, or if the goal has already finished

        if (timeGoalieAlarmObject != null && timeGoalieAlarmObject.getAlarmDonePendingIntent() == null &&
                !goal.getGoalEntry().isHasFinished()) {
            timeGoalieAlarmObject.setAlarmDonePendingIntent(TimeGoalieAlarmReceiver.createTimeGoaliePendingIntent(
                    view.getContext(), TimeGoalieAlarmReceiver.createAlarmDoneTimeGoalieAlarmIntent
                            (view.getContext(),
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
            if (timeGoalieAlarmObject.getTargetTime() == 0) {
                timeGoalieAlarmObject.setTargetTime(targetTime);
            }

            TimeGoalieAlarmManager.setTimeGoalAlarm(
                    targetTime,
                    view.getContext(), null,
                    timeGoalieAlarmObject.getAlarmDonePendingIntent());


            //Sets up the running out of time alert
            //will not fire if it has been fired already for a goal.
            if (goal.getGoalTypeId() == 1 && !timeGoalieAlarmObject.isHasBeenWarned()) { //Limit Goal Type
                timeGoalieAlarmObject.setOneMinuteWarningPendingIntent(TimeGoalieAlarmReceiver.
                        createTimeGoaliePendingIntent(view.getContext(),
                                TimeGoalieAlarmReceiver.createOneMinuteWarningTimeGoalieAlarmIntent(
                                        view.getContext(),
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
                            view.getContext(), null,
                            timeGoalieAlarmObject.getOneMinuteWarningPendingIntent());
                }

            }

        }
    }

}
