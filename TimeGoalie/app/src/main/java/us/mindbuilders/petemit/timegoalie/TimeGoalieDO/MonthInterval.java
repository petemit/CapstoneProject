package us.mindbuilders.petemit.timegoalie.TimeGoalieDO;

/**
 * Created by Peter on 10/24/2017.
 */

public class MonthInterval {
    private String begOfMonth;
    private String endOfMonth;

    public MonthInterval(String beg, String end) {
        this.begOfMonth = beg;
        this.endOfMonth = end;
    }

    public String getBegOfMonth() {
        return begOfMonth;
    }

    public void setBegOfMonth(String begOfMonth) {
        this.begOfMonth = begOfMonth;
    }

    public String getEndOfMonth() {
        return endOfMonth;
    }

    public void setEndOfMonth(String endOfMonth) {
        this.endOfMonth = endOfMonth;
    }
}
