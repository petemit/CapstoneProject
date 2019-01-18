package us.mindbuilders.petemit.timegoalie;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import java.util.ArrayList;
import java.util.Calendar;

import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Day;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.GoalEntry;
import us.mindbuilders.petemit.timegoalie.data.InsertNewGoal;
import us.mindbuilders.petemit.timegoalie.services.TimeGoalieGoalEntryController;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieDateUtils;

//import com.facebook.stetho.Stetho;

/**
 * Created by Peter on 9/22/2017.
 */

public class BaseApplication extends Application {
    private static Calendar activeCalendarDate = Calendar.getInstance();
    private static Context context;
    private static TimeGoalieGoalEntryController goalEntryController;
    public static final String CHANNEL_ID = "time_goalie_alarms";

    public static Calendar getActiveCalendarDate() {
        if (activeCalendarDate == null) {
            return Calendar.getInstance();
        }
        return activeCalendarDate;
    }

    public static void setActiveCalendarDate(Calendar activeCalendarDate) {
        BaseApplication.activeCalendarDate = activeCalendarDate;

    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }



    public static Context getContext() {
        return context;
    }

    public static boolean checkGoalEntryController() {
        if (goalEntryController != null) {
            return true;
        }
        return false;
    }

    public static TimeGoalieGoalEntryController getGoalEntryController() {
        if (goalEntryController == null) {
            goalEntryController = new TimeGoalieGoalEntryController();
        }
        return goalEntryController;
    }

    public static void setGoalEntryController(TimeGoalieGoalEntryController goalEntryController) {
        BaseApplication.goalEntryController = goalEntryController;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        //Get the logic engine ready
        if (goalEntryController == null) {
            goalEntryController = new TimeGoalieGoalEntryController();
        }


        setContext(getBaseContext());
        //This is for the widget... and only for the widget... dang widget.
        goalEntryController.startSecondlyAlarm();



      //  Only do this if we are in the debug build
        if (BuildConfig.DEBUG) {
            //StethoEnabler
            getDatabasePath("timeGoalie.db").delete();
            //dummy goal
            Goal goal = new Goal();
            goal.setName("Today Only Goal");
            goal.setHours(1);
            goal.setMinutes(30);
            goal.setGoalTypeId(0);
            goal.setCreationDate(TimeGoalieDateUtils.getSqlDateString());
            goal.setIsDaily(0);
            goal.setIsWeekly(0);
            Goal goal1 = new Goal();
            goal1.setName("Today Only Goal2");
            goal1.setHours(0);
            goal1.setMinutes(1);
            goal1.setGoalTypeId(0);
            goal1.setCreationDate(TimeGoalieDateUtils.getSqlDateString());
            goal1.setIsDaily(0);
            goal1.setIsWeekly(0);
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
            Goal goal7 = new Goal();
            goal7.setName("Enough Teethos!");
            goal7.setGoalTypeId(2);
            goal7.setCreationDate(TimeGoalieDateUtils.getSqlDateString());
            goal7.setIsDaily(1);
            goal7.setIsWeekly(0);
            Goal goal4 = new Goal();
            goal4.setName("Take Nap");
            goal4.setGoalTypeId(1);
            goal4.setCreationDate(TimeGoalieDateUtils.getSqlDateString());
            goal4.setIsDaily(1);
            goal4.setIsWeekly(0);
            goal4.setHours(0);
            goal4.setMinutes(1);
            Goal goal3 = new Goal();
            goal3.setName("Brush Teeth");
            goal3.setGoalTypeId(2);
            goal3.setCreationDate(TimeGoalieDateUtils.getSqlDateString());
            goal3.setIsDaily(1);
            goal3.setIsWeekly(0);
            Goal goal5 = new Goal();
            goal5.setName("Dust Teeth");
            goal5.setGoalTypeId(2);
            goal5.setCreationDate(TimeGoalieDateUtils.getSqlDateString());
            goal5.setIsDaily(1);
            goal5.setIsWeekly(0);
            Goal goal6 = new Goal();
            goal6.setName("Enough Teeth!");
            goal6.setGoalTypeId(2);
            goal6.setCreationDate(TimeGoalieDateUtils.getSqlDateString());
            goal6.setIsDaily(1);
            goal6.setIsWeekly(0);


            new InsertNewGoal(getBaseContext()).execute(goal);
            new InsertNewGoal(getBaseContext()).execute(goal1);
            new InsertNewGoal(getBaseContext()).execute(goal2);
            new InsertNewGoal(getBaseContext()).execute(goal4);
            new InsertNewGoal(getBaseContext()).execute(goal3);
            new InsertNewGoal(getBaseContext()).execute(goal5);
            new InsertNewGoal(getBaseContext()).execute(goal6);
            new InsertNewGoal(getBaseContext()).execute(goal7);
        }

        StethoEnabler.enable(this);
    }

    public interface GoalActivityListListener {
        void notifyChanges(GoalEntry goalEntry);
    }
}
