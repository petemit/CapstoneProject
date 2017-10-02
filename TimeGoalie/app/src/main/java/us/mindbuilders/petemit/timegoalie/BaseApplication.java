package us.mindbuilders.petemit.timegoalie;

import android.app.Application;
import android.util.Log;

import com.facebook.stetho.Stetho;

import java.util.ArrayList;

import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Day;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.TimeGoalieAlarmObject;
import us.mindbuilders.petemit.timegoalie.data.InsertNewGoal;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieAlarmManager;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieDateUtils;

/**
 * Created by Peter on 9/22/2017.
 */

public class BaseApplication extends Application {
    private static ArrayList<TimeGoalieAlarmObject> timeGoalieAlarmObjects;

    public static ArrayList<TimeGoalieAlarmObject> getTimeGoalieAlarmObjects() {
        return timeGoalieAlarmObjects;
    }

    public static void setTimeGoalieAlarmObjects(ArrayList<TimeGoalieAlarmObject> timeGoalieAlarmObjects) {
        BaseApplication.timeGoalieAlarmObjects = timeGoalieAlarmObjects;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setTimeGoalieAlarmObjects(new ArrayList<TimeGoalieAlarmObject>());
        getDatabasePath("timeGoalie.db").delete();
        Stetho.initializeWithDefaults(this);

        //dummy goal
        Goal goal = new Goal();
        goal.setName("Today Only Goal");
        goal.setHours(1);
        goal.setMinutes(30);
        goal.setGoalTypeId(0);
        goal.setCreationDate(TimeGoalieDateUtils.getSqlDateString());
        goal.setIsDaily(0);
        goal.setIsWeekly(0);
        Goal goal2 = new Goal();
        goal2.setName("Thursday Only Goal");
        goal2.setHours(2);
        goal2.setMinutes(30);
        goal2.setGoalTypeId(1);
        goal2.setCreationDate(TimeGoalieDateUtils.getSqlDateString());
        goal2.setIsDaily(0);
        goal2.setIsWeekly(1);
        ArrayList<Day> dayArrayList = new ArrayList<Day>();
        Day thu = new Day();
        thu.setName("Thu");
        thu.setSequence(4);
        dayArrayList.add(thu);
        goal2.setGoalDays(dayArrayList);
        Goal goal3 = new Goal();
        goal3.setName("Brush Teeth");
        goal3.setGoalTypeId(2);
        goal3.setCreationDate(TimeGoalieDateUtils.getSqlDateString());
        goal3.setIsDaily(1);
        goal3.setIsWeekly(0);
        Goal goal4 = new Goal();
        goal4.setName("Take Nap");
        goal4.setGoalTypeId(1);
        goal4.setCreationDate(TimeGoalieDateUtils.getSqlDateString());
        goal4.setIsDaily(1);
        goal4.setIsWeekly(0);
        goal4.setHours(0);
        goal4.setMinutes(1);


        new InsertNewGoal(getBaseContext()).execute(goal);
        new InsertNewGoal(getBaseContext()).execute(goal2);
        new InsertNewGoal(getBaseContext()).execute(goal3);
        new InsertNewGoal(getBaseContext()).execute(goal4);
    }

    public static TimeGoalieAlarmObject getTimeGoalieAlarmObjectById(long goal_id){
        for (int i = 0 ; i < timeGoalieAlarmObjects.size() ; i ++) {
            if (timeGoalieAlarmObjects.get(i).getGoal_id()==goal_id){
                Log.e("check",goal_id+"");
                return timeGoalieAlarmObjects.get(i);

            }
        }
        return null;
    }



}
