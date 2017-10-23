package us.mindbuilders.petemit.timegoalie.TimeGoalieDO;

/**
 * Created by Peter on 10/23/2017.
 */

public class WeekInterval {
    private String beginningOfWeek;
    private String endOfWeek;

    public WeekInterval(String beg, String end) {
        this.beginningOfWeek = beg;
        this.endOfWeek = end;
    }

    public String getBeginningOfWeek() {
        return beginningOfWeek;
    }

    public void setBeginningOfWeek(String beginningOfWeek) {
        this.beginningOfWeek = beginningOfWeek;
    }

    public String getEndOfWeek() {
        return endOfWeek;
    }

    public void setEndOfWeek(String endOfWeek) {
        this.endOfWeek = endOfWeek;
    }
}
