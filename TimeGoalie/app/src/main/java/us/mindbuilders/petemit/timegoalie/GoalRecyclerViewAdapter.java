package us.mindbuilders.petemit.timegoalie;


import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
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
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieAlarmManager;

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
        startStopButtonStateMap= new HashMap<Long, Boolean>();
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

                long onBindElapsedSeconds = goal.getGoalEntry().getSecondsElapsed();
                final long totalSeconds = ((goal.getHours() * 60 * 60) + (goal.getMinutes() * 60)) -
                        onBindElapsedSeconds;
                holder.startStopTimer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        TimeGoalieAlarmObject timeGoalieAlarmObject =
                                BaseApplication.getTimeGoalieAlarmObjectById((goal.getGoalId()));
                        if (b) {
                            long secondsElapsed = goal.getGoalEntry().getSecondsElapsed();
                            long remainingSeconds = totalSeconds - secondsElapsed;
                            if (timeGoalieAlarmObject != null) {
                                timeGoalieAlarmObject.setCountDownTimer(
                                        TimeGoalieAlarmManager.makeCountdownTimer(
                                                remainingSeconds,
                                                1,
                                                holder.time_tv, goal.getGoalEntry()));
                                timeGoalieAlarmObject.getCountDownTimer().start();
                            }
                            else{
                                timeGoalieAlarmObject = new TimeGoalieAlarmObject();
                                timeGoalieAlarmObject.setCountDownTimer(
                                        TimeGoalieAlarmManager.makeCountdownTimer(
                                                remainingSeconds,
                                                1,
                                                holder.time_tv, goal.getGoalEntry()));
                                timeGoalieAlarmObject.getCountDownTimer().start();
                                BaseApplication.getTimeGoalieAlarmObjects().add(timeGoalieAlarmObject);

                            }
                        } else {
                            if (timeGoalieAlarmObject.getCountDownTimer() != null) {
                                timeGoalieAlarmObject.getCountDownTimer().cancel();
                            }
                        }

                    }
                });
                //if the map is not null, then set the state of the button.
                if (startStopButtonStateMap.get(goal.getGoalId())!=null){
                    holder.startStopTimer.performClick();
                  //  holder.startStopTimer.setChecked(startStopButtonStateMap.get(goal.getGoalId()));
                }

            }
            holder.mView.setOnClickListener(onClickListener);
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
        // public final TextView mIdView;
        private TextView tv_goaltitle;
        private ToggleButton startStopTimer;
        private TextView time_tv;

        public GoalViewHolder(View view) {
            super(view);
            mView = view;
            //mIdView = (TextView) view.findViewById(R.id.id);
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

