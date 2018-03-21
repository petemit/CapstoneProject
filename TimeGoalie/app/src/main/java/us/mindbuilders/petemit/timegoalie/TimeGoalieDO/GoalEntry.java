package us.mindbuilders.petemit.timegoalie.TimeGoalieDO;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import us.mindbuilders.petemit.timegoalie.data.TimeGoalieContract;

/**
 * Created by Peter on 9/23/2017.
 */

public class GoalEntry implements Parcelable {
    public static final Parcelable.Creator<GoalEntry> CREATOR = new Parcelable.Creator<GoalEntry>() {
        @Override
        public GoalEntry createFromParcel(Parcel in) {
            return new GoalEntry(in);
        }

        @Override
        public GoalEntry[] newArray(int size) {
            return new GoalEntry[size];
        }
    };
    private long id;
    private String date = "";
    private int goalAugment;
    private long goal_id;
    private int secondsElapsed;
    private int hasSucceeded;
    private int isRunning;
    private long targetTime;
    private long startedTime;
    private int isFinished;
    private boolean hasMoved;


    public GoalEntry(long id, long goal_id, String date) {
        this.id = id;
        this.date = date;
        this.goal_id = goal_id;

        //Hmm!  I guess I don't need this!  Cool!  The loader gets this for me.
        ///.... what have I been working on for the past hour...
//        if (date != null) {
//            updateSecondsElapsed(BaseApplication.getContext());
//        }
    }

    protected GoalEntry(Parcel in) {
        id = in.readLong();
        date = in.readString();
        goalAugment = in.readInt();
        goal_id = in.readLong();
        isFinished = in.readInt();
        secondsElapsed = in.readInt();
        hasSucceeded = in.readInt();
        isRunning = in.readInt();
        targetTime = in.readLong();
        setStartedTime(in.readLong());
    }

    public static ArrayList<GoalEntry> makeGoalEntryListFromCursor(Cursor cursor) {

        ArrayList<GoalEntry> goalEntries = new ArrayList<GoalEntry>();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                GoalEntry goalEntry = new GoalEntry(cursor.getLong(cursor.
                        getColumnIndex(TimeGoalieContract.GoalEntries._ID))
                        , cursor.getLong(
                        cursor.getColumnIndex(
                                TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_GOAL_ID))
                        , cursor.getString(
                        cursor.getColumnIndex(TimeGoalieContract
                                .GoalEntries.GOALENTRIES_COLUMN_DATETIME)));
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
                goalEntries.add(goalEntry);
            }
        }
        return goalEntries;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void updateSecondsElapsed(Context context) {

        Cursor cursor;
        cursor = context.getContentResolver().query(
                TimeGoalieContract.buildGetAGoalEntryByGoalId(getGoal_id()),
                null,
                null,
                new String[]{getDate()},
                null);


        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            this.setSecondsElapsed(cursor.getInt(cursor.getColumnIndex(
                    TimeGoalieContract.GoalEntries.GOALENTRIES_COLUMN_SECONDSELAPSED
            )));

        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public int getSecondsElapsed() {

        return secondsElapsed;

    }

    public void setSecondsElapsed(int secondsElapsed) {
        this.secondsElapsed = secondsElapsed;

    }

    public void addSecondElapsed() {
        setSecondsElapsed(getSecondsElapsed() + 1);
    }

    public void setSecondsElapsed(int secondsElapsed, boolean isAlarm) {
        this.secondsElapsed = secondsElapsed;
    }

    public long getGoal_id() {
        return goal_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isHasFinished() {
        return isFinished == 1;
    }

    public void setHasFinished(int running) {
        isFinished = running;
    }

    public void setHasFinished(boolean running) {
        if (running) {
            isFinished = 1;
        } else {
            isFinished = 0;
        }
    }

    public int getGoalAugment() {
        return goalAugment;
    }

    public void setGoalAugment(int goalAugment) {
        this.goalAugment = goalAugment;
    }

    public boolean getHasSucceeded() {
        return hasSucceeded == 1;
    }

    public void setHasSucceeded(int hasSucceeded) {
        this.hasSucceeded = hasSucceeded;
    }

    public void setHasSucceeded(boolean hasSucceeded) {
        if (hasSucceeded) {
            this.hasSucceeded = 1;
        } else {
            this.hasSucceeded = 0;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(date);
        dest.writeInt(goalAugment);
        dest.writeLong(goal_id);
        dest.writeInt(isFinished);
        dest.writeInt(secondsElapsed);
        dest.writeInt(hasSucceeded);
        dest.writeInt(isRunning);
        dest.writeLong(targetTime);
        dest.writeLong(getStartedTime());
    }

    public boolean isRunning() {
        return isRunning == 1;
    }

    public void setRunning(int running) {
        isRunning = running;
    }

    public void setRunning(boolean running) {
        if (running) {
            isRunning = 1;
        } else {
            isRunning = 0;
        }
    }

    public long getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(long targetTime) {
        this.targetTime = targetTime;
    }


    public boolean isHasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public long getStartedTime() {
        return startedTime;
    }

    public void setStartedTime(long startedTime) {
        this.startedTime = startedTime;
    }
}
