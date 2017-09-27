package us.mindbuilders.petemit.timegoalie.TimeGoalieDO;

import java.sql.Date;
import java.util.ArrayList;

/**
 * Created by Peter on 9/23/2017.
 */

public class Goal {
    private long goalId;
    private String name;
    private int hours;
    private int minutes;
    private long goalTypeId;
    private int isDaily;
    private int isWeekly;
    private String creationDate;
    private int isDisabled;
    private ArrayList<Date> datesAccomplished;
    private ArrayList<GoalEntry> goalEntries;
    private ArrayList<Day> goalDays;


    public long getGoalId() {
        return goalId;
    }

    public void setGoalId(long goalId) {
        this.goalId = goalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public long getGoalTypeId() {
        return goalTypeId;
    }

    public void setGoalTypeId(long goalTypeId) {
        this.goalTypeId = goalTypeId;
    }

    public int getIsDaily() {
        return isDaily;
    }

    public void setIsDaily(int isDaily) {
        this.isDaily = isDaily;
    }

    public int getIsWeekly() {
        return isWeekly;
    }

    public void setIsWeekly(int isWeekly) {
        this.isWeekly = isWeekly;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String isTodayOnly) {
        this.creationDate = isTodayOnly;
    }

    public int getIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(int isDisabled) {
        this.isDisabled = isDisabled;
    }

    public ArrayList<Date> getDatesAccomplished() {
        return datesAccomplished;
    }

    public void setDatesAccomplished(ArrayList<Date> datesAccomplished) {
        this.datesAccomplished = datesAccomplished;
    }

    public ArrayList<GoalEntry> getGoalEntries() {
        return goalEntries;
    }

    public void setGoalEntries(ArrayList<GoalEntry> goalEntries) {
        this.goalEntries = goalEntries;
    }

    public ArrayList<Day> getGoalDays() {
        return goalDays;
    }

    public void setGoalDays(ArrayList<Day> goalDays) {
        this.goalDays = goalDays;
    }
}