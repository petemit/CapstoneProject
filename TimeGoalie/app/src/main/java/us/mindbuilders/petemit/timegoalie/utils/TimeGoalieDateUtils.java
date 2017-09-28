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

    public static long getDayIdFromToday(){
        return gcal.get(Calendar.DAY_OF_WEEK)-1;
    }

    public static String getSqlDateString(){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(new java.util.Date());
        return date;
    }


}
