package us.mindbuilders.petemit.timegoalie.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
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

    public static void setTimeGoalAlarm(int hoursFromNow, int minutesFromNow,
                                        Context context, @Nullable Bundle extras) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TimeGoalieAlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, TIMEGOAL_ALARM_REQUEST_CODE,
                intent, 0); //need one shot?

        //create calendar
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.HOUR_OF_DAY, hoursFromNow);
        calendar.add(Calendar.MINUTE, minutesFromNow);

        alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
    }

    public static CountDownTimer makeCountdownTimer(long secondsInFuture,
                                                    long intervalInSeconds,
                                                    final TextView tv,
                                                    final GoalEntry goalEntry) {
        long milliInFuture = secondsInFuture * 1000;
        long intervalInSecondsInMilli = intervalInSeconds * 1000;
        CountDownTimer cdTimer = new CountDownTimer(milliInFuture, intervalInSecondsInMilli) {
            @Override
            public void onTick(long millisuntilfinished) {
                tv.setText(makeTimeTextFromMillis(millisuntilfinished));
                goalEntry.addSecondElapsed();
            }

            @Override
            public void onFinish() {

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
