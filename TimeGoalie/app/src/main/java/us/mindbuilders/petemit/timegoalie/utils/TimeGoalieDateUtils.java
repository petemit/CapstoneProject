package us.mindbuilders.petemit.timegoalie.utils;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * All the utilities I'll be using with this app
 */

public class TimeGoalieDateUtils {

    private static GregorianCalendar gcal = new GregorianCalendar();

    public static long getDayIdFromToday(){
        return gcal.get(Calendar.DAY_OF_WEEK);
    }


}
