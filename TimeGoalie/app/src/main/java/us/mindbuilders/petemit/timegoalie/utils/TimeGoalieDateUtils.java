package us.mindbuilders.petemit.timegoalie.utils;

import android.util.Log;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * All the utilities I'll be using with this app
 */

public class TimeGoalieDateUtils {

    private static GregorianCalendar gcal = new GregorianCalendar();
    public static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat monthFormat = new SimpleDateFormat("MMM");

    public static long getDayIdFromToday() {
        return gcal.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public static String getSqlDateString() {
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

    public static int calculateSecondsElapsed(long futuremillis, long currentmillis, int hours, int minutes, int augment) {
        long millisleft = futuremillis - currentmillis;
        long timeElapsed = ((hours * 60 * 60) + (minutes * 60)) -
                (millisleft/1000) + augment;
        return ((int) timeElapsed);
    }

    public static long getCurrentTimeInMillis() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public static String getNicelyFormattedDate(Calendar cal) {
     //  DateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy");//  I like this one better
        DateFormat df = new SimpleDateFormat("MMM d, yyyy");
        String date = df.format(cal.getTime());
        return date;
    }

    public static String getSqlDateString(Calendar cal) {
        String date = df.format(cal.getTime());
        return date;
    }

    public static String getMonthFromStringDate(String s){
        String returnString="";
        try {
            returnString = monthFormat.format(df.parse(s));
        }
        catch (ParseException p){
            Log.e("TimeGoalieDateUtils", p.toString());
        }
        return returnString;
    }

}
