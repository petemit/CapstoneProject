package us.mindbuilders.petemit.timegoalie.TimeGoalieDO;

import android.animation.ObjectAnimator;
import android.database.Cursor;
import android.view.animation.Animation;

import java.sql.Date;
import java.util.ArrayList;

import us.mindbuilders.petemit.timegoalie.data.TimeGoalieContract;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieDateUtils;

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
    private ObjectAnimator seekbarAnimation;
    private boolean changingSeekbar;

    public static ArrayList<Goal> createGoalListWithGoalEntriesFromCursor(Cursor cursor) {
        ArrayList<Goal> goalList = new ArrayList<Goal>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Goal goal = new Goal();
                goal.setGoalId(cursor.getInt(cursor.getColumnIndex(TimeGoalieContract.Goals._ID)));
                if (goal.getGoalId() != 0) {
                    goal.setName(cursor.getString(cursor.
                            getColumnIndex(TimeGoalieContract.Goals.GOALS_COLUMN_NAME)));
                    goal.setHours((int) Long.parseLong(cursor.getString(cursor.getColumnIndex(
                            TimeGoalieContract.Goals.GOALS_COLUMN_TIMEGOALHOURS))));
                    goal.setMinutes((int) Long.parseLong(cursor.getString(cursor.getColumnIndex(
                            TimeGoalieContract.Goals.GOALS_COLUMN_TIMEGOALMINUTES))));
                    goal.setIsDaily(Integer.parseInt(cursor.getString(cursor.getColumnIndex(
                            TimeGoalieContract.Goals.GOALS_COLUMN_ISDAILY
                    ))));
                    goal.setIsWeekly(Integer.parseInt(cursor.getString(cursor.getColumnIndex(
                            TimeGoalieContract.Goals.GOALS_COLUMN_ISWEEKLY
                    ))));
                    goal.setGoalTypeId(
                            cursor.getInt(
                                    cursor.getColumnIndex(
                                            TimeGoalieContract.Goals.GOALS_COLUMN_GOALTYPEID)));
                    GoalEntry goalEntry = new GoalEntry(cursor.getLong(cursor.
                            getColumnIndex(TimeGoalieContract.GoalEntries._ID))
                            , goal.getGoalId(), cursor.getString(
                            cursor.getColumnIndex(TimeGoalieContract
                                    .GoalEntries.GOALENTRIES_COLUMN_DATETIME)));
                    if (goalEntry.getDate() == null) {
                        goalEntry.setDate(TimeGoalieDateUtils.getSqlDateString());
                    }
                    goalEntry.setSecondsElapsed(
                            cursor.getInt(cursor.getColumnIndex(TimeGoalieContract
                                    .GoalEntries.GOALENTRIES_COLUMN_SECONDSELAPSED)), true);
                    goalEntry.setGoalAugment(cursor.getInt(
                            cursor.getColumnIndex(TimeGoalieContract
                                    .GoalEntries.GOALENTRIES_COLUMN_GOALAUGMENT)));
                    goalEntry.setHasSucceeded(cursor.getInt(
                            cursor.getColumnIndex(TimeGoalieContract.
                                    GoalEntries.GOALENTRIES_COLUMN_SUCCEEDED)
                    ));
                    goalEntry.setHasFinished(
                            cursor.getInt(cursor.getColumnIndex(TimeGoalieContract
                                    .GoalEntries.GOALENTRIES_COLUMN_ISFINISHED))
                    );
                    goalEntry.setRunning(cursor.getInt(cursor.getColumnIndex(TimeGoalieContract.
                            GoalEntries.GOALENTRIES_COLUMN_ISRUNNING)));

                    goalEntry.setTargetTime((cursor.getLong(cursor.getColumnIndex(TimeGoalieContract.
                            GoalEntries.GOALENTRIES_COLUMN_TARGETTIME))));
                    goalEntry.setStartedTime((cursor.getLong(cursor.getColumnIndex(TimeGoalieContract.
                            GoalEntries.GOALENTRIES_COLUMN_STARTEDTIME))));
                    goal.goalEntry = goalEntry;
                    goalList.add(goal);
                }
            }
        }
        return goalList;
    }

    public static ArrayList<Goal> createGoalListFromCursor(Cursor cursor) {
        ArrayList<Goal> goalList = new ArrayList<Goal>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Goal goal = new Goal();
                goal.setGoalId(cursor.getInt(cursor.getColumnIndex(TimeGoalieContract.Goals._ID)));
                goal.setName(cursor.getString(cursor.
                        getColumnIndex(TimeGoalieContract.Goals.GOALS_COLUMN_NAME)));
                goal.setHours((int) Long.parseLong(cursor.getString(cursor.getColumnIndex(
                        TimeGoalieContract.Goals.GOALS_COLUMN_TIMEGOALHOURS))));
                goal.setMinutes((int) Long.parseLong(cursor.getString(cursor.getColumnIndex(
                        TimeGoalieContract.Goals.GOALS_COLUMN_TIMEGOALMINUTES))));
                goal.setIsDaily(Integer.parseInt(cursor.getString(cursor.getColumnIndex(
                        TimeGoalieContract.Goals.GOALS_COLUMN_ISDAILY
                ))));
                goal.setIsWeekly(Integer.parseInt(cursor.getString(cursor.getColumnIndex(
                        TimeGoalieContract.Goals.GOALS_COLUMN_ISWEEKLY
                ))));
                goal.setGoalTypeId(
                        cursor.getInt(
                                cursor.getColumnIndex(
                                        TimeGoalieContract.Goals.GOALS_COLUMN_GOALTYPEID)));
                goalList.add(goal);
            }
        }
        return goalList;
    }

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

    public long getGoalSeconds() {
        int goalAugment = 0;
        if (this.goalEntry != null) {
            goalAugment = this.getGoalEntry().getGoalAugment();
        }

        return ((getHours() * 60 * 60) + (getMinutes() * 60) + goalAugment);
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public ObjectAnimator getSeekbarAnimation() {
        return seekbarAnimation;
    }

    public void setSeekbarAnimation(ObjectAnimator seekbarAnimation) {
        this.seekbarAnimation = seekbarAnimation;
    }

    public boolean isChangingSeekbar() {
        return changingSeekbar;
    }

    public void setChangingSeekbar(boolean changingSeekbar) {
        this.changingSeekbar = changingSeekbar;
    }
}