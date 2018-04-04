package us.mindbuilders.petemit.timegoalie.services;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import us.mindbuilders.petemit.timegoalie.BaseApplication;
import us.mindbuilders.petemit.timegoalie.GoalListViewCallback;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.GoalEntry;
import us.mindbuilders.petemit.timegoalie.data.InsertNewGoalEntry;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieAlarmManager;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieDateUtils;



/**
 * Created by Peter on 3/20/2018.
 */

public class TimeGoalieGoalEntryController {

    private static final int SECONDS_BUFFER = 1; //This makes it end neatly on the time target.
    private GoalListViewCallback viewCallback;
    private Handler engine;
    private ArrayList<Goal> goals;
    private boolean isEngineRunning = false;
    private RunQueue queue = new RunQueue();
    Runnable runnable =  new Runnable() {
        @Override
        public void run() {
            // Run every second until there are no goal entries running
            if (updateGoalEntries(goals)) {
                engine.postDelayed(this, tick);
            }
            else {
                engine.removeCallbacks(this);
                isEngineRunning = false;
            }
        }
    };
    private static int tick = 1000;



    public TimeGoalieGoalEntryController() {
        engine = new Handler();
    }

    public void startEngine(ArrayList<Goal> goalList) {
        goals = goalList;
        if (!isEngineRunning) {
//            queue.queue(runnable);
//            engine.post(queue);
            engine.post(runnable);
        }
    }

    synchronized public boolean updateGoalEntries (ArrayList<Goal> goals) {

        isEngineRunning = true;
        boolean keepEngineRunning = false;
        if (goals == null) {
            return false;
        }
        for (int i = 0; i < goals.size(); i++) {
            Goal goal = goals.get(i);
            if (null == goal.getGoalEntry()) {
                continue;
            }

            GoalEntry entry = goal.getGoalEntry();

            //First off.. is Goal Running?
            if (entry.isRunning()) {
                Log.e("controller", String.valueOf(TimeGoalieDateUtils.getCurrentTimeInMillis()));
                keepEngineRunning = true;

                //Increment Goal
                addSecondToGoal(entry, i);
                break;

            }
        }
        return keepEngineRunning;
    }

    public void setGoalListViewCallback(GoalListViewCallback callback) {
        this.viewCallback = callback;
    }

    public void addSecondToGoal(GoalEntry goalEntry, int position) {
        //goalEntry.setSecondsElapsed(TimeGoalieDateUtils.calculateSecondsElapsed(goalEntry.getStartedTime(),goalEntry.getSecondsElapsed()));
       // new InsertNewGoalEntry(BaseApplication.getContext()).execute(goalEntry);
        if (null != viewCallback) {
            viewCallback.update(position);
        }
    }

    public void startGoal(GoalEntry goalEntry, long newtime, Goal goal) {
        if (TimeGoalieDateUtils.calculateSecondsElapsed(goalEntry.getStartedTime(),goalEntry.getSecondsElapsed()) >= goal.getGoalSeconds()) {
            resumeGoalAfterFinished(goalEntry, goal);
            return;
        }
        else {
            goalEntry.setHasFinished(0);
            if (goal.getGoalTypeId() == 0) {
                goalEntry.setHasSucceeded(false);
            }
            if (goal.getGoalTypeId() == 1) {
                goalEntry.setHasSucceeded(true);
            }
        }

        if (!goalEntry.isRunning()) {
            goalEntry.setRunning(true);
            goalEntry.setStartedTime(TimeGoalieDateUtils.getCurrentTimeInMillis());
        }

        //Create the target time:

        long hours = newtime / (60 * 60);
        long minutes = (newtime - (hours * 60 * 60)) / 60;
        long seconds = (newtime - (hours * 60 * 60) - (minutes * 60));

        long targetTime = TimeGoalieDateUtils.createTargetCalendarTime(
                (int) hours,
                (int) minutes,
                (int) seconds);


       // if (goalEntry.getTargetTime() == 0) {
            goalEntry.setTargetTime(targetTime);
        //}

        //delete existing Alarm
        TimeGoalieAlarmManager.cancelTimeGoalAlarm(BaseApplication.getContext(),TimeGoalieAlarmReceiver.createTimeGoaliePendingIntent(
                        BaseApplication.getContext(),TimeGoalieAlarmReceiver.createAlarmDoneTimeGoalieAlarmIntent(
                                BaseApplication.getContext(), goal.getName() ,(int)goalEntry.getGoal_id()),(int)goalEntry.getGoal_id()
                ));

        //Create Finish Alarm

        TimeGoalieAlarmManager.setTimeGoalAlarm(targetTime,BaseApplication.getContext(),
                null,TimeGoalieAlarmReceiver.createTimeGoaliePendingIntent(
                        BaseApplication.getContext(),TimeGoalieAlarmReceiver.createAlarmDoneTimeGoalieAlarmIntent(
                                BaseApplication.getContext(), goal.getName() ,(int)goalEntry.getGoal_id()),(int)goalEntry.getGoal_id()
                ));

        //Create one Minute Warning Alarm if it's a time limit goal

        if (goal.getGoalTypeId()==1) {//Time Limit Goal

            //delete existing One Minute Warning Alarm
            TimeGoalieAlarmManager.cancelTimeGoalAlarm(BaseApplication.getContext(),TimeGoalieAlarmReceiver.createTimeGoaliePendingIntent(
                    BaseApplication.getContext(),TimeGoalieAlarmReceiver.createOneMinuteWarningTimeGoalieAlarmIntent(
                            BaseApplication.getContext(), goal.getName() ,(int)goalEntry.getGoal_id()),(int)goalEntry.getGoal_id()
            ));

            //Create One Minute Warning Alarm

            TimeGoalieAlarmManager.setTimeGoalAlarm(targetTime-TimeGoalieAlarmReceiver.ONE_MINUTE_WARNING_MILLIS,BaseApplication.getContext(),
                    null,TimeGoalieAlarmReceiver.createTimeGoaliePendingIntent(
                            BaseApplication.getContext(),TimeGoalieAlarmReceiver.createOneMinuteWarningTimeGoalieAlarmIntent(
                                    BaseApplication.getContext(), goal.getName() ,(int)goalEntry.getGoal_id()),(int)goalEntry.getGoal_id()
                    ));
        }

        new InsertNewGoalEntry(BaseApplication.getContext()).execute(goalEntry);
        //Secondly Alarm... not sure if I want to do this anymore

//        TimeGoalieAlarmManager.setTimeGoalAlarm(
//                TimeGoalieDateUtils.createTargetSecondlyCalendarTime((int)
//                        TimeGoalieAlarmReceiver.SECONDLY_FREQUENCY / 1000),
//                context, null,
//                TimeGoalieAlarmReceiver.createSecondlyTimeGoaliePendingIntent(
//                        context,
//                        TimeGoalieAlarmReceiver.
//                                createEverySecondDbUpdateAlarmIntent(context)));

    }

    public void stopGoal(GoalEntry goalEntry, Goal goal) {
        goalEntry.setSecondsElapsed(TimeGoalieDateUtils.calculateSecondsElapsed(goalEntry.getStartedTime(),goalEntry.getSecondsElapsed()));
        goalEntry.setStartedTime(0);
        goalEntry.setRunning(false);
        new InsertNewGoalEntry(BaseApplication.getContext()).execute(goalEntry);


        deleteExistingFinishAlarm(goal);

        if (goal.getGoalTypeId()==1) {//Time Limit Goal
            deleteExistingOneMinuteAlarm(goal);
        }



    }

    private Goal findGoalInList(int goalId) {
        Goal goal = null;

        if (goals != null) {
            for (Goal thisgoal: goals
                    ) {
                if (thisgoal.getGoalId()==goalId) {
                    goal = thisgoal;
                }
            }
        }
        return goal;
    }

    private void fetchGoalList() {
        //not implemented
    }

    public void succeedGoalById(int goalId) {
        Goal goal = findGoalInList(goalId);
        if (null != goal) {
            GoalEntry goalEntry;
            if (goal != null) {
                goalEntry = goal.getGoalEntry();
            }
            else {
                return;
            }
            goalEntry.setHasFinished(true);
            goalEntry.setHasSucceeded(true);
            stopGoal(goalEntry, goal);
            //todo just broad update. :(

            if (viewCallback != null) {
                viewCallback.update(0);
            }
        }
    }

    public void finishGoalById(int goalId) {

        Goal goal = findGoalInList(goalId);
        if (null != goal) {
            GoalEntry goalEntry;
            if (goal != null) {
                goalEntry = goal.getGoalEntry();
            }
            else {
                return;
            }
            goalEntry.setHasFinished(true);
            stopGoal(goalEntry, goal);

            //todo just broad update. :(
            if (viewCallback != null) {
                viewCallback.update(0);
            }
        }

    }

    public void stopGoalById(int goalId) {

        Goal goal = findGoalInList(goalId);
        GoalEntry goalEntry;
        if (goal != null) {
            goalEntry = goal.getGoalEntry();
        }
        else {
            return;
        }

        stopGoal(goalEntry, goal);

    }

    public void updateGoal(Context context, GoalEntry goalEntry) {
        new InsertNewGoalEntry(context).execute(goalEntry);
    }

    public void resumeGoalAfterFinished(GoalEntry goalEntry, Goal goal) {

        goalEntry.setRunning(true);
        goalEntry.setSecondsElapsed(TimeGoalieDateUtils.calculateSecondsElapsed(goalEntry.getStartedTime(),goalEntry.getSecondsElapsed()));
        goalEntry.setStartedTime(TimeGoalieDateUtils.getCurrentTimeInMillis());
        new InsertNewGoalEntry(BaseApplication.getContext()).execute(goalEntry);

        if (!isEngineRunning) {
            engine.post(runnable);
        }
    }

    public void resumeGoalAfterFinishedWithElapsedTime(int goalId) {
        Goal goal = findGoalInList(goalId);
        goal.getGoalEntry().setRunning(true);
        goal.getGoalEntry().setSecondsElapsed((int)(goal.getGoalEntry().getSecondsElapsed()+((TimeGoalieDateUtils.getCurrentTimeInMillis()-goal.getGoalEntry().getTargetTime())/1000)));
        new InsertNewGoalEntry(BaseApplication.getContext()).execute(goal.getGoalEntry());

        if (!isEngineRunning) {
            engine.post(runnable);
        }
    }


    public void deleteExistingFinishAlarm(Goal goal) {
        //delete existing Finish Alarm
        TimeGoalieAlarmManager.cancelTimeGoalAlarm(BaseApplication.getContext(),TimeGoalieAlarmReceiver.createTimeGoaliePendingIntent(
                BaseApplication.getContext(),TimeGoalieAlarmReceiver.createAlarmDoneTimeGoalieAlarmIntent(
                        BaseApplication.getContext(), goal.getName() ,(int)goal.getGoalId()),(int)goal.getGoalId()
        ));
    }

    public void deleteExistingOneMinuteAlarm(Goal goal) {
        //delete existing One Minute Warning Alarm
        TimeGoalieAlarmManager.cancelTimeGoalAlarm(BaseApplication.getContext(), TimeGoalieAlarmReceiver.createTimeGoaliePendingIntent(
                BaseApplication.getContext(), TimeGoalieAlarmReceiver.createOneMinuteWarningTimeGoalieAlarmIntent(
                        BaseApplication.getContext(), goal.getName(),(int)goal.getGoalId()),(int)goal.getGoalId()
        ));
    }

    class RunQueue implements Runnable{


        private List list = new ArrayList();

        public void queue(Runnable task)
        {
            list.add(task);
        }
        @Override
        public void run()
        {
            while(list.size() > 0)
            {
                Runnable task = (Runnable)list.get(0);

                list.remove(0);
                task.run();
            }
        }
    }
}
