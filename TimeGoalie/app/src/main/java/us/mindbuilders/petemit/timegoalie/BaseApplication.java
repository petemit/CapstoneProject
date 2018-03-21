package us.mindbuilders.petemit.timegoalie;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Day;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.GoalEntry;
import us.mindbuilders.petemit.timegoalie.data.InsertNewGoal;
import us.mindbuilders.petemit.timegoalie.data.InsertNewGoalEntry;
import us.mindbuilders.petemit.timegoalie.data.TimeGoalieContract;
import us.mindbuilders.petemit.timegoalie.services.TimeGoalieGoalEntryController;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieDateUtils;
import us.mindbuilders.petemit.timegoalie.widget.TimeGoalieWidgetProvider;

//import com.facebook.stetho.Stetho;

/**
 * Created by Peter on 9/22/2017.
 */

public class BaseApplication extends Application {
    private static Calendar activeCalendarDate = Calendar.getInstance();
    private static Context context;
    private static GoalActivityListListener goalActivityListListener;
    private static long lastTimeSecondUpdated;
    private static Handler secondlyHandler;
    private static Runnable runnable;
    private static boolean handlerRunning;
    private static TimeGoalieGoalEntryController goalEntryController;

    public static void createHandler(final long millis) {
        secondlyHandler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                handlerRunning = true;
                Cursor cursor = context.getContentResolver().query
                        (TimeGoalieContract.getRunningGoalEntriesThatHaveGoalEntryForToday(),
                                null,
                                null,
                                new String[]{TimeGoalieDateUtils.
                                        getSqlDateString(activeCalendarDate)},
                                null);

                if (cursor != null && cursor.getCount() > 0) {
                    ArrayList<GoalEntry> goalEntries = GoalEntry.makeGoalEntryListFromCursor(cursor);
                    for (GoalEntry goalEntry : goalEntries
                            ) {

                        if (goalEntry.isRunning()) {

                            goalEntry.addSecondElapsed();

                            if (BaseApplication.getGoalActivityListListener() != null) {
                                BaseApplication.getGoalActivityListListener().notifyChanges(goalEntry);
                            }

                            new InsertNewGoalEntry(context).execute(goalEntry);

                            Intent updateWidgetintent = new Intent(context,
                                    TimeGoalieWidgetProvider.class);
                            updateWidgetintent.setAction(
                                    TimeGoalieWidgetProvider.ACTION_GET_GOALS_FOR_TODAY);
                            context.sendBroadcast(updateWidgetintent);

                            Log.e("alarm", goalEntry.getGoal_id() + " : "
                                    + goalEntry.getSecondsElapsed() + "");

                            BaseApplication.setLastTimeSecondUpdated(
                                    TimeGoalieDateUtils.getCurrentTimeInMillis());

                        }


                    }//end for


                }
                ///end if second has elapsed.
                else if (cursor == null || cursor.getCount() == 0) {
                    secondlyHandler.removeCallbacks(this);
                }
                secondlyHandler.postDelayed(this, millis);
                handlerRunning = false;
            }
        };
        secondlyHandler.postDelayed(runnable, millis);
    }

    public static void destroyHandler() {
        secondlyHandler.removeCallbacks(runnable);
        secondlyHandler = null;
    }


    public static GoalActivityListListener getGoalActivityListListener() {
        return goalActivityListListener;
    }

    public static void setGoalActivityListListener(GoalActivityListListener goalActivityListListener) {
        BaseApplication.goalActivityListListener = goalActivityListListener;
    }

    public static long getLastTimeSecondUpdated() {
        return lastTimeSecondUpdated;
    }

    public static void setLastTimeSecondUpdated(long lastTimeSecondUpdated) {
        BaseApplication.lastTimeSecondUpdated = lastTimeSecondUpdated;
    }

    public static Handler getSecondlyHandler() {

        return secondlyHandler;
    }

    public static void setSecondlyHandler(Handler secondlyHandler) {
        BaseApplication.secondlyHandler = secondlyHandler;
    }

    public static boolean isHandlerRunning() {
        return handlerRunning;
    }

    public static void setHandlerRunning(boolean handlerRunning) {
        BaseApplication.handlerRunning = handlerRunning;
    }

    public static Calendar getActiveCalendarDate() {
        if (activeCalendarDate == null) {
            return Calendar.getInstance();
        }
        return activeCalendarDate;
    }

    public static void setActiveCalendarDate(Calendar activeCalendarDate) {
        BaseApplication.activeCalendarDate = activeCalendarDate;

    }



    public static Context getContext() {
        return context;
    }

    public static TimeGoalieGoalEntryController getGoalEntryController() {
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

        //Get the logic engine ready
        goalEntryController = new TimeGoalieGoalEntryController();


        setContext(getBaseContext());

        //Only do this if we are in the debug build
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
