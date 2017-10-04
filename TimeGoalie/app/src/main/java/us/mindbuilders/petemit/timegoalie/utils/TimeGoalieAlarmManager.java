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
    public static int TIMEGOAL_ALARM_REQUEST_CODE = 1238;

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
                                                    final SeekBar seekbar) {
        long milliInFuture = secondsInFuture * 1000;
        long intervalInSecondsInMilli = intervalInSeconds * 1000;
        CountDownTimer cdTimer = new CountDownTimer(milliInFuture, intervalInSecondsInMilli) {
            @Override
            public void onTick(long millisuntilfinished) {
                tv.setText(makeTimeTextFromMillis(millisuntilfinished));
                goalEntry.addSecondElapsed();


                //set Progress bar Progress
                if (seekbar != null) {

                    ObjectAnimator animation = ObjectAnimator.ofInt(seekbar, "progress", seekbar.getProgress(), (int)((1-((double)(millisuntilfinished/1000)/totalSeconds))*100*100));
                    animation.setDuration(2000);
                    animation.setInterpolator(new LinearInterpolator());
                    animation.start();
                }

            }

            @Override
            public void onFinish() {
                tv.setText("00:00:00");
                if (seekbar != null) {
                    seekbar.setProgress(100);
                }

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
