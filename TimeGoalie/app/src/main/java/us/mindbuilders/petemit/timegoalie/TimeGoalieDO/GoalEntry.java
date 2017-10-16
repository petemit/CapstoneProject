package us.mindbuilders.petemit.timegoalie.TimeGoalieDO;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.sql.Date;

import us.mindbuilders.petemit.timegoalie.BaseApplication;
import us.mindbuilders.petemit.timegoalie.data.InsertNewGoalEntry;

/**
 * Created by Peter on 9/23/2017.
 */

public class GoalEntry implements Parcelable{
    private long id;
    private String date = "";
    private int goalAugment;
    private long goal_id;
    private boolean hasFinished;
    private int secondsElapsed;
    private int hasSucceeded;

    public GoalEntry(long goal_id, String date) {
        this.date = date;
        this.goal_id = goal_id;

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSecondsElapsed() {

        if (BaseApplication.getTimeGoalieAlarmObjectById(goal_id,date) != null) {
            return BaseApplication.getTimeGoalieAlarmObjectById(goal_id,date).getSecondsElapsed();
        }
        return 0;
    }

    public void addSecondElapsed() {
        if (BaseApplication.getTimeGoalieAlarmObjectById(goal_id,date) != null) {
            BaseApplication.getTimeGoalieAlarmObjectById(goal_id,date ).setSecondsElapsed(
                    BaseApplication.getTimeGoalieAlarmObjectById(goal_id,date).getSecondsElapsed() + 1
            );
        }
    }

    public void setSecondsElapsed(int secondsElapsed) {
        if (BaseApplication.getTimeGoalieAlarmObjectById(goal_id,date) != null) {
        } else {
            TimeGoalieAlarmObject timeGoalieAlarmObject = new TimeGoalieAlarmObject(goal_id, date);
            timeGoalieAlarmObject.setSecondsElapsed(secondsElapsed);
            timeGoalieAlarmObject.setGoal_id(goal_id);
            BaseApplication.getTimeGoalieAlarmObjects().add(timeGoalieAlarmObject
            );
        }
    }

    public void setSecondsElapsed(int secondsElapsed, boolean isAlarm) {
        if (BaseApplication.getTimeGoalieAlarmObjectById(goal_id, date) != null) {
            if (isAlarm) {
                TimeGoalieAlarmObject timeGoalieAlarmObject =
                        BaseApplication.getTimeGoalieAlarmObjectById(goal_id,date);
                timeGoalieAlarmObject.setSecondsElapsed(secondsElapsed);
            }
        } else {
            TimeGoalieAlarmObject timeGoalieAlarmObject = new TimeGoalieAlarmObject(goal_id, date);
            timeGoalieAlarmObject.setSecondsElapsed(secondsElapsed);
            BaseApplication.getTimeGoalieAlarmObjects().add(timeGoalieAlarmObject
            );
        }
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

    public boolean isHasFinished() {
        if (BaseApplication.getTimeGoalieAlarmObjectById(goal_id ,date) != null) {
            return BaseApplication.getTimeGoalieAlarmObjectById(goal_id,date).isHasFinished();
        }

        return hasFinished;
    }

    public void setHasFinished(boolean hasFinished) {
        if (BaseApplication.getTimeGoalieAlarmObjectById(goal_id,date) != null) {
            BaseApplication.getTimeGoalieAlarmObjectById(goal_id,date).setHasFinished(hasFinished);
        }
    }

    public int getGoalAugment() {
        if (BaseApplication.getTimeGoalieAlarmObjectById(goal_id,date) != null) {
            return BaseApplication.getTimeGoalieAlarmObjectById(goal_id,date).getGoalAugment();
        }
        return 0;
    }

    public void setGoalAugment(int goalAugment) {
        if (BaseApplication.getTimeGoalieAlarmObjectById(goal_id,date) != null) {
            BaseApplication.getTimeGoalieAlarmObjectById(goal_id,date).setGoalAugment(goalAugment);
        }
    }

    public int getHasSucceeded() {
        return hasSucceeded;
    }

    public void setHasSucceeded(int hasSucceeded) {
        this.hasSucceeded = hasSucceeded;
    }


    protected GoalEntry(Parcel in) {
        id = in.readLong();
        date = in.readString();
        goalAugment = in.readInt();
        goal_id = in.readLong();
        hasFinished = in.readByte() != 0x00;
        secondsElapsed = in.readInt();
        hasSucceeded = in.readInt();
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
        dest.writeByte((byte) (hasFinished ? 0x01 : 0x00));
        dest.writeInt(secondsElapsed);
        dest.writeInt(hasSucceeded);
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
}
