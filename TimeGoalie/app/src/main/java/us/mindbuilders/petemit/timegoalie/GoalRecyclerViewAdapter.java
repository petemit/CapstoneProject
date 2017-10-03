package us.mindbuilders.petemit.timegoalie;


import android.app.AlarmManager;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.HashMap;

import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;

import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.GoalEntry;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.TimeGoalieAlarmObject;
import us.mindbuilders.petemit.timegoalie.services.TimeGoalieAlarmReceiver;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieAlarmManager;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieDateUtils;

/**
 * Created by Peter on 9/15/2017.
 */

public class GoalRecyclerViewAdapter extends RecyclerView.Adapter<GoalRecyclerViewAdapter.GoalViewHolder> {

    // private final List<DummyContent.DummyItem> mValues;

    private View.OnClickListener onClickListener;
    private ArrayList<Goal> goalArrayList;
    private HashMap<Long, Boolean> startStopButtonStateMap;


    //    public GoalRecyclerViewAdapter(List<DummyContent.DummyItem> items, View.OnClickListener onClickListener) {
//        mValues = items;
//        this.onClickListener = onClickListener;
//    }
    public GoalRecyclerViewAdapter(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        startStopButtonStateMap = new HashMap<Long, Boolean>();
    }

    @Override
    public GoalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.goal_list_content_more, parent, false);
                break;
            case 1:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.goal_list_content_less, parent, false);
                break;
            case 2:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.goal_list_content_yes_no, parent, false);
                break;
        }
        return new GoalViewHolder(view);
    }

    public void swapCursor(ArrayList<Goal> goalArrayList) {
        this.goalArrayList = goalArrayList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final GoalViewHolder holder, int position) {
        if (getItemCount() > 0) {
            final Goal goal = goalArrayList.get(position);
            //cursor.moveToPosition(position);
            holder.tv_goaltitle.setText(goal.getName());
            if (holder.startStopTimer != null) {
                long onBindElapsedSeconds = 0;
                // long onBindElapsedSeconds = goal.getGoalEntry().getSecondsElapsed();
                if (BaseApplication.getTimeGoalieAlarmObjectById(goal.getGoalId()) != null) {
                    TimeGoalieAlarmObject timeGoalieAlarmObj = BaseApplication.getTimeGoalieAlarmObjectById(goal.getGoalId());
                    //Recalculate Elapsed Seconds
                    if (timeGoalieAlarmObj.getTargetTime()!=0) {
                        timeGoalieAlarmObj
                                .setSecondsElapsed(TimeGoalieDateUtils.calculateSecondsElapsed(
                                        timeGoalieAlarmObj.getTargetTime(),
                                        TimeGoalieDateUtils.getCurrentTimeInMillis(),
                                        goal.getHours(),
                                        goal.getMinutes()
                                ));
                    }
                    onBindElapsedSeconds = (BaseApplication.getTimeGoalieAlarmObjectById(goal.getGoalId()))
                            .getSecondsElapsed();

                }
                long remainingSeconds = ((goal.getHours() * 60 * 60) + (goal.getMinutes() * 60)) -
                        onBindElapsedSeconds;
                final long totalSeconds = ((goal.getHours() * 60 * 60) + (goal.getMinutes() * 60));
                Log.e("Mindbuilders", "remainingSeconds: " + remainingSeconds);
                if (remainingSeconds<0) {
                    holder.time_tv.setText(TimeGoalieAlarmManager.makeTimeTextFromMillis(0));
                }
                else {
                    holder.time_tv.setText(TimeGoalieAlarmManager.makeTimeTextFromMillis(remainingSeconds * 1000));
                }

                holder.startStopTimer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        TimeGoalieAlarmObject timeGoalieAlarmObject =
                                BaseApplication.getTimeGoalieAlarmObjectById((goal.getGoalId()));
                        long newtime = totalSeconds;
                        if (timeGoalieAlarmObject != null) {
                            newtime = totalSeconds - timeGoalieAlarmObject.getSecondsElapsed();
                            Log.e("Mindbuilders", "newtime: " + newtime);
                        }
                        if (b) {
                            startTimer(holder.time_tv, newtime, goal, compoundButton);
                        } else {
                            if (timeGoalieAlarmObject != null) {
                                if (timeGoalieAlarmObject.getCountDownTimer() != null) {
                                    timeGoalieAlarmObject.getCountDownTimer().cancel();
                                    timeGoalieAlarmObject.setCountDownTimer(null);
                                }
                                if (timeGoalieAlarmObject.getPi() != null) {
                                    TimeGoalieAlarmManager.cancelTimeGoalAlarm(
                                            compoundButton.getContext(),
                                            timeGoalieAlarmObject.getPi());
                                    timeGoalieAlarmObject.getPi().cancel();
                                    timeGoalieAlarmObject.setPi(null);
                                }
                            }
                        }
                    }
                });

                if (BaseApplication.getTimeGoalieAlarmObjectById(goal.getGoalId()) != null) {
                    if (BaseApplication.getTimeGoalieAlarmObjectById(goal.getGoalId()).isRunning()) {
                        holder.startStopTimer.setChecked(false);
                        holder.startStopTimer.setChecked(true);
                    }
                }


                //if the map is not null, then set the state of the button.
//                if (startStopButtonStateMap.get(goal.getGoalId()) != null) {
//                    holder.startStopTimer.performClick();
//                    //  holder.startStopTimer.setChecked(startStopButtonStateMap.get(goal.getGoalId()));
//                }

            }
            holder.mView.setOnClickListener(onClickListener);
        }
    }

    /* This will actually link the static list of Alarms and Countdowntimers in the baseapplication
       to the textview
    */
    public void startTimer(TextView time_tv, long totalSeconds, Goal goal, CompoundButton compoundButton) {

        TimeGoalieAlarmObject timeGoalieAlarmObject =
                BaseApplication.getTimeGoalieAlarmObjectById((goal.getGoalId()));
        long remainingSeconds = totalSeconds;// - secondsElapsed;
        Log.e("Mindbuilders", "remainingseconds: " + remainingSeconds);
        if (timeGoalieAlarmObject != null) {
            timeGoalieAlarmObject.setCountDownTimer(
                    TimeGoalieAlarmManager.makeCountdownTimer(
                            remainingSeconds,
                            1,
                            time_tv,
                            goal.getGoalEntry()));
            timeGoalieAlarmObject.getCountDownTimer().start();
            timeGoalieAlarmObject.setRunning(true);
        } else {
            timeGoalieAlarmObject = new TimeGoalieAlarmObject();
            timeGoalieAlarmObject.setCountDownTimer(
                    TimeGoalieAlarmManager.makeCountdownTimer(
                            remainingSeconds,
                            1,
                            time_tv,
                            goal.getGoalEntry()));
            timeGoalieAlarmObject.getCountDownTimer().start();
            timeGoalieAlarmObject.setRunning(true);
            timeGoalieAlarmObject.setGoal_id(goal.getGoalId());
            BaseApplication.getTimeGoalieAlarmObjects().add(timeGoalieAlarmObject);
        }

        // this will create the system alarm.  :-O !  It will not create it if the pi
        // already exists.

        if (timeGoalieAlarmObject != null && timeGoalieAlarmObject.getPi() == null) {
            timeGoalieAlarmObject.setPi(TimeGoalieAlarmReceiver.createTimeGoaliePendingIntent(
                    compoundButton.getContext(), (int) goal.getGoalId(), goal.getName()));

            long hours = remainingSeconds / (60 * 60);
            long minutes = (remainingSeconds - (hours * 60 * 60)) / 60;
            long seconds = (remainingSeconds - (hours * 60 * 60) - (minutes * 60));

            Log.e("Mindbuilders", "hours: " + hours);
            Log.e("Mindbuilders", "minutes: " + minutes);
            Log.e("Mindbuilders", "seconds: " + seconds);

            long targetTime=TimeGoalieDateUtils.createTargetCalendarTime(
                    (int) hours,
                    (int) minutes,
                    (int) seconds);

            //sound the alarm!!
            if (timeGoalieAlarmObject.getTargetTime()==0) {
                timeGoalieAlarmObject.setTargetTime(targetTime);
            }

            TimeGoalieAlarmManager.setTimeGoalAlarm(
                    targetTime,
                    compoundButton.getContext(), null,
                    timeGoalieAlarmObject.getPi());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (int) goalArrayList.get(position).getGoalTypeId();
    }

    @Override
    public int getItemCount() {
        if (goalArrayList != null) {
            return goalArrayList.size();
        } else {
            return 0;
        }
    }

    public class GoalViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        private ToggleButton pencil;
        private LinearLayout editButtons;
        private TextView tv_goaltitle;
        private ToggleButton startStopTimer;
        private TextView time_tv;

        public GoalViewHolder(View view) {
            super(view);
            mView = view;
            tv_goaltitle = (TextView) view.findViewById(R.id.tv_goal_title);
            pencil = (ToggleButton) view.findViewById(R.id.pencil_button);
            editButtons = (LinearLayout) view.findViewById(R.id.edit_button_ll);
            startStopTimer = (ToggleButton) view.findViewById(R.id.start_stop);
            time_tv = (TextView) view.findViewById(R.id.timeTextView);

            if (pencil != null) {
                pencil.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editButtons.getVisibility() != View.VISIBLE) {
                            editButtons.setVisibility(View.VISIBLE);
                        } else {
                            editButtons.setVisibility(View.GONE);
                        }
                    }
                });
            }


        }

        @Override
        public String toString() {
            return super.toString() + " '" + tv_goaltitle.getText() + "'";
        }
    }
}

