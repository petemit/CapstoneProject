package us.mindbuilders.petemit.timegoalie.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * All the utilities I'll be using with this app
 */

public class TimeGoalieDateUtils {

    private static GregorianCalendar gcal = new GregorianCalendar();

    public static long getDayIdFromToday() {
        return gcal.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public static String getSqlDateString() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(new java.util.Date());
        return date;
    }

    public static long createTargetCalendarTime(int hours, int minutes, int seconds) {
        //create calendar
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        calendar.add(Calendar.MINUTE, minutes);
        calendar.add(Calendar.SECOND, seconds);
        return calendar.getTimeInMillis();
    }

    public static int calculateSecondsElapsed(long futuremillis, long currentmillis, int hours, int minutes) {
        long millisleft = futuremillis - currentmillis;
        long timeElapsed = ((hours * 60 * 60) + (minutes * 60)) -
                (millisleft/1000);
        return ((int) timeElapsed);
    }

    public static long getCurrentTimeInMillis() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public static String getNicelyFormattedDate(Calendar cal) {
       DateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy");
        String date = df.format(cal.getTime());
        return date;
    }

    public static String getSqlDateString(Calendar cal) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(cal.getTime());
        return date;
    }

}
