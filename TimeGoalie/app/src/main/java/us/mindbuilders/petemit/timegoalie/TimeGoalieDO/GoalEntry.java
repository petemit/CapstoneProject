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
    private long id;
    private String date = "";
    private int goalAugment;
    private long goal_id;
    private int secondsElapsed;
    private int hasSucceeded;
    private int isRunning;
    private long targetTime;
    private int isFinished;
    private boolean needsSecondsUpdate;


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

//        if (BaseApplication.getTimeGoalieAlarmObjectById(goal_id,date) != null) {
//            return BaseApplication.getTimeGoalieAlarmObjectById(goal_id,date).getSecondsElapsed();
//        }
//        return 0;
        //return secondsElapsed;

        return secondsElapsed;

    }

    public void addSecondElapsed() {
//        if (BaseApplication.getTimeGoalieAlarmObjectById(goal_id,date) != null) {
//            BaseApplication.getTimeGoalieAlarmObjectById(goal_id,date ).setSecondsElapsed(
//                    BaseApplication.getTimeGoalieAlarmObjectById(goal_id,date).getSecondsElapsed() + 1
//            );
//        }

        setSecondsElapsed(getSecondsElapsed() + 1);
    }

    public void setSecondsElapsed(int secondsElapsed) {
//        if (BaseApplication.getTimeGoalieAlarmObjectById(goal_id,date) != null) {
//        } else {
//            TimeGoalieAlarmObject timeGoalieAlarmObject = new TimeGoalieAlarmObject(goal_id, date);
//            timeGoalieAlarmObject.setSecondsElapsed(secondsElapsed);
//            timeGoalieAlarmObject.setGoal_id(goal_id);
//            BaseApplication.getTimeGoalieAlarmObjects().add(timeGoalieAlarmObject
//            );
//        }
        this.secondsElapsed = secondsElapsed;

    }

    public void setSecondsElapsed(int secondsElapsed, boolean isAlarm) {
//        if (BaseApplication.getTimeGoalieAlarmObjectById(goal_id, date) != null) {
//            if (isAlarm) {
//                TimeGoalieAlarmObject timeGoalieAlarmObject =
//                        BaseApplication.getTimeGoalieAlarmObjectById(goal_id,date);
//                timeGoalieAlarmObject.setSecondsElapsed(secondsElapsed);
//            }
//        } else {
//            TimeGoalieAlarmObject timeGoalieAlarmObject = new TimeGoalieAlarmObject(goal_id, date);
//            timeGoalieAlarmObject.setSecondsElapsed(secondsElapsed);
//            BaseApplication.getTimeGoalieAlarmObjects().add(timeGoalieAlarmObject
//            );
//        }
        this.secondsElapsed = secondsElapsed;
    }

    public long getGoal_id() {
        return goal_id;
    }

    public void setGoal_id(long goal_id) {
        this.goal_id = goal_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

//    public boolean isHasFinished() {
//        if (BaseApplication.getTimeGoalieAlarmObjectById(goal_id, date) != null) {
//            return BaseApplication.getTimeGoalieAlarmObjectById(goal_id, date).isHasFinished();
//        }
//
//        return hasFinished;
//    }
//
//    public void setHasFinished(boolean hasFinished) {
//        if (BaseApplication.getTimeGoalieAlarmObjectById(goal_id, date) != null) {
//            BaseApplication.getTimeGoalieAlarmObjectById(goal_id, date).setHasFinished(hasFinished);
//        }
//    }

    public boolean isHasFinished() {
        return isFinished == 1;
    }

    public void setHasFinished(boolean running) {
        if (running) {
            isFinished = 1;
        } else {
            isFinished = 0;
        }
    }

    public void setHasFinished(int running) {
        isFinished = running;
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

    public void setHasSucceeded(boolean hasSucceeded) {
        if (hasSucceeded) {
            this.hasSucceeded = 1;
        } else {
            this.hasSucceeded = 0;
        }
    }

    public void setHasSucceeded(int hasSucceeded) {
        this.hasSucceeded = hasSucceeded;
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
    }

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

    public boolean isRunning() {
        return isRunning == 1;
    }

    public void setRunning(boolean running) {
        if (running) {
            isRunning = 1;
        } else {
            isRunning = 0;
        }
    }

    public void setRunning(int running) {
        isRunning = running;
    }

    public long getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(long targetTime) {
        this.targetTime = targetTime;
    }

    public boolean isNeedsSecondsUpdate() {
        return needsSecondsUpdate;
    }

    public void setNeedsSecondsUpdate(boolean needsSecondsUpdate) {
        this.needsSecondsUpdate = needsSecondsUpdate;
    }

    public static ArrayList<GoalEntry> makeGoalEntryListFromCursor(Cursor cursor) {

        ArrayList<GoalEntry> goalEntries = new ArrayList<GoalEntry>();
        if (cursor != null && cursor.getCount() > 0) {
            while(cursor.moveToNext()) {
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
}
