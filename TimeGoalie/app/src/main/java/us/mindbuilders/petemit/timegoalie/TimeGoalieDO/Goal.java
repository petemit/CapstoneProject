package us.mindbuilders.petemit.timegoalie.TimeGoalieDO;

import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;

import us.mindbuilders.petemit.timegoalie.data.TimeGoalieContract;

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
    private GoalEntry goalEntry;
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

    public GoalEntry getGoalEntry() {
        return goalEntry;
    }

    public void setGoalEntry(GoalEntry goalEntry) {
        this.goalEntry = goalEntry;
    }

    public ArrayList<Day> getGoalDays() {
        return goalDays;
    }

    public void setGoalDays(ArrayList<Day> goalDays) {
        this.goalDays = goalDays;
    }

    public static ArrayList<Goal> createGoalListFromCursor(Cursor cursor) {
        ArrayList<Goal> goalList = new ArrayList<Goal>();
        while (cursor.moveToNext()) {
            Goal goal = new Goal();
            goal.setGoalId(cursor.getInt(cursor.getColumnIndex(TimeGoalieContract.Goals._ID)));
            goal.setName(cursor.getString(cursor.
                    getColumnIndex(TimeGoalieContract.Goals.GOALS_COLUMN_NAME)));
            goal.setHours((int) Long.parseLong(cursor.getString(cursor.getColumnIndex(
                    TimeGoalieContract.Goals.GOALS_COLUMN_TIMEGOALHOURS))));
            goal.setMinutes((int) Long.parseLong(cursor.getString(cursor.getColumnIndex(
                    TimeGoalieContract.Goals.GOALS_COLUMN_TIMEGOALMINUTES))));
            goal.setGoalTypeId(
                    cursor.getInt(
                            cursor.getColumnIndex(
                                    TimeGoalieContract.Goals.GOALS_COLUMN_GOALTYPEID)));
            GoalEntry goalEntry = new GoalEntry();
            goalEntry.setGoal_id(goal.getGoalId());
            goalEntry.setSecondsElapsed(
                    cursor.getInt(cursor.getColumnIndex(TimeGoalieContract
                    .GoalEntries.GOALENTRIES_COLUMN_SECONDSELAPSED)));
            goalEntry.setDate(cursor.getString(
                    cursor.getColumnIndex(TimeGoalieContract
                            .GoalEntries.GOALENTRIES_COLUMN_DATETIME)));

            goal.goalEntry=goalEntry;
            goalList.add(goal);
        }
        return goalList;
    }

    public long getGoalSeconds (){
        return ((getHours() * 60 * 60) + (getMinutes() * 60));
    }

}