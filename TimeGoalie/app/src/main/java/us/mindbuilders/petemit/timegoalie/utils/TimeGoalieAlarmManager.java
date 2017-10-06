package us.mindbuilders.petemit.timegoalie.utils;

import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.GoalEntry;
import us.mindbuilders.petemit.timegoalie.services.TimeGoalieAlarmReceiver;

/**
 * Created by Peter on 9/29/2017.
 */

public class TimeGoalieAlarmManager {

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


    public static CountDownTimer makeCountdownTimer(long secondsInFuture,
                                                    long intervalInSeconds,
                                                    final long totalSeconds,
                                                    final TextView tv,
                                                    final GoalEntry goalEntry,
                                                    final int goalType,
                                                    final SeekBar seekbar) {
        long milliInFuture = secondsInFuture * 1000;
        long intervalInSecondsInMilli = intervalInSeconds * 1000;
        final CountDownTimer cdTimer = new CountDownTimer(milliInFuture, intervalInSecondsInMilli) {
            @Override
            public void onTick(long millisuntilfinished) {

                switch (goalType) {
                    case 0: //Time Goal To Encourage
                        if (!goalEntry.isHasFinished()) {
                            tv.setText(makeTimeTextFromMillis(totalSeconds * 1000 - millisuntilfinished));
                        }
                        else {
                            tv.setText(makeTimeTextFromMillis(goalEntry.getSecondsElapsed()*1000+totalSeconds));
                        }
                        break;
                    case 1: //Time Goal to Limit
                        if (!goalEntry.isHasFinished()) {
                            tv.setText(makeTimeTextFromMillis(millisuntilfinished));
                        }
                        else {
                            tv.setText("-"+makeTimeTextFromMillis(-1*(totalSeconds*1000-goalEntry.getSecondsElapsed()*1000)));
                        }
                        break;
                }

                goalEntry.addSecondElapsed();

                //set Progress bar Progress
                if (seekbar != null && !goalEntry.isHasFinished()) {

                    ObjectAnimator animation = ObjectAnimator.ofInt(seekbar, "progress", seekbar.getProgress(), (int) ((1 - ((double) (millisuntilfinished / 1000) / totalSeconds)) * 100 * 100));
                    animation.setDuration(2000);
                    animation.setInterpolator(new LinearInterpolator());
                    animation.start();
                }

            }

            @Override
            public void onFinish() {

                switch (goalType) {
                    case 0:
                        goalEntry.setHasFinished(true);
                        this.start();
                      //  tv.setText("00:00:00");
                        break;
                    case 1:
                      //  tv.setText(makeTimeTextFromMillis(totalSeconds / 1000));
                        goalEntry.setHasFinished(true);
                        this.start();
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

        return (String.format("%02d", hours)
                + ":" + String.format("%02d", minutes)
                + ":" + String.format("%02d", seconds));
    }
}
