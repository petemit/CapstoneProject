package us.mindbuilders.petemit.timegoalie;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;

import android.graphics.drawable.RotateDrawable;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


import android.view.animation.LinearInterpolator;

import android.widget.Button;
import android.widget.CompoundButton;

import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;


import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;

import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.TimeGoalieAlarmObject;
import us.mindbuilders.petemit.timegoalie.data.InsertNewGoalEntry;
import us.mindbuilders.petemit.timegoalie.services.TimeGoalieAlarmReceiver;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieAlarmManager;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieDateUtils;

/**
 * Data handler for goal recyclerview.  This is turning out to be the brains of this operation
 */

public class GoalRecyclerViewAdapter extends RecyclerView.Adapter<GoalRecyclerViewAdapter.GoalViewHolder> {

    // private final List<DummyContent.DummyItem> mValues;

    private View.OnClickListener onClickListener;
    private ArrayList<Goal> goalArrayList;


    //    public GoalRecyclerViewAdapter(List<DummyContent.DummyItem> items, View.OnClickListener onClickListener) {
//        mValues = items;
//        this.onClickListener = onClickListener;
//    }
    public GoalRecyclerViewAdapter(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
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

                if (BaseApplication.getTimeGoalieAlarmObjectById(goal.getGoalId()) != null) {
                    TimeGoalieAlarmObject timeGoalieAlarmObj = BaseApplication.getTimeGoalieAlarmObjectById(goal.getGoalId());
                    //Recalculate Elapsed Seconds

                    if (timeGoalieAlarmObj.getTargetTime() != 0 && timeGoalieAlarmObj.isRunning()) {


                        goal.getGoalEntry().setSecondsElapsed((TimeGoalieDateUtils.calculateSecondsElapsed(
                                timeGoalieAlarmObj.getTargetTime(),
                                TimeGoalieDateUtils.getCurrentTimeInMillis(),
                                goal.getHours(),
                                goal.getMinutes())),true);

//
//                        timeGoalieAlarmObj
//                                .setSecondsElapsed(TimeGoalieDateUtils.calculateSecondsElapsed(
//                                        timeGoalieAlarmObj.getTargetTime(),
//                                        TimeGoalieDateUtils.getCurrentTimeInMillis(),
//                                        goal.getHours(),
//                                        goal.getMinutes()
//                                ));
                    }
                    onBindElapsedSeconds = (BaseApplication.getTimeGoalieAlarmObjectById(goal.getGoalId()))
                            .getSecondsElapsed();
                }
                long remainingSeconds = (goal.getGoalSeconds() -
                        onBindElapsedSeconds);


                //Set initial Time Text labels:

                // if this is a more goal
                if (holder.tv_timeOutOf != null) {
                    holder.tv_timeOutOf.setText(" / " +TimeGoalieAlarmManager.makeTimeTextFromMillis(
                                    goal.getGoalSeconds()*1000
                    ));
                    holder.time_tv.setText(TimeGoalieAlarmManager.makeTimeTextFromMillis(0));
                    if (holder.seekbar != null) {
                        holder.seekbar.setProgress((int) ((1 - ((double) (remainingSeconds) / goal.getGoalSeconds())) * 100 * 100));
                    }

                }
                else {

                    if (remainingSeconds < 0) {
//                        holder.time_tv.setText(TimeGoalieAlarmManager.makeTimeTextFromMillis(0));
//                        if (holder.seekbar != null) {
//                            holder.seekbar.setProgress(10000);
//                        }
                    } else {
                        holder.time_tv.setText(TimeGoalieAlarmManager.makeTimeTextFromMillis(remainingSeconds * 1000));
                        //set Progress bar Progress
                        if (holder.seekbar != null) {
                            holder.seekbar.setProgress((int) ((1 - ((double) (remainingSeconds) / goal.getGoalSeconds())) * 100 * 100));
                        }
                    }
                }

                holder.startStopTimer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        TimeGoalieAlarmObject timeGoalieAlarmObject =
                                BaseApplication.getTimeGoalieAlarmObjectById((goal.getGoalId()));
                        long newtime = goal.getGoalSeconds();
                        if (timeGoalieAlarmObject != null) {
                            newtime = goal.getGoalSeconds() - timeGoalieAlarmObject.getSecondsElapsed();
                            Log.e("Mindbuilders", "newtime: " + newtime);
                        }
                        if (b) {
                            TimeGoalieAlarmManager.startTimer(holder.time_tv, newtime, goal,
                                    compoundButton, holder.seekbar);
                            holder.objanim.start();
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
                                timeGoalieAlarmObject.setRunning(false);
                            }
                            if (goal.getGoalEntry()!=null) {
                                goal.getGoalEntry().setDate(TimeGoalieDateUtils.getSqlDateString());
                                new InsertNewGoalEntry(
                                        compoundButton.getContext()).execute(goal.getGoalEntry());
                            }

                            holder.objanim.cancel();
                        }
                    }
                });

                if (BaseApplication.getTimeGoalieAlarmObjectById(goal.getGoalId()) != null) {
                    if (BaseApplication.getTimeGoalieAlarmObjectById(goal.getGoalId()).isRunning()) {
                        holder.startStopTimer.setChecked(false);
                        holder.startStopTimer.setChecked(true);
                    }
                }


            }// end start/stop
            holder.mView.setOnClickListener(onClickListener);


            if (holder.smallAdd != null) {
                final TimeGoalieAlarmObject timeGoalieAlarmObject =
                        BaseApplication.getTimeGoalieAlarmObjectById((goal.getGoalId()));

                int[] incrementValues = holder.smallAdd.getContext().getResources().getIntArray(R.array.incrementArray);
                // edit buttons
                Button[] addButtons = new Button[]{holder.smallAdd, holder.mediumAdd, holder.largeAdd};
                Button[] subtractButtons = new Button[]{holder.smallSubtract, holder.mediumSubtract, holder.largeSubtract};

                for (int i = 0; i < incrementValues.length; i++) {
                    final int value = incrementValues[i];

                    addButtons[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            boolean isRunning=false;

                            if (timeGoalieAlarmObject != null ) {
                                goal.setMinutes(goal.getMinutes() + value);

                                isRunning = BaseApplication.getTimeGoalieAlarmObjectById(goal.getGoalId()).isRunning();
                                if (timeGoalieAlarmObject.getCountDownTimer() != null) {
                                    timeGoalieAlarmObject.getCountDownTimer().cancel();
                                    timeGoalieAlarmObject.setCountDownTimer(null);
                                }
                                if (timeGoalieAlarmObject.getPi() != null) {
                                    TimeGoalieAlarmManager.cancelTimeGoalAlarm(view.getContext(),
                                            timeGoalieAlarmObject.getPi());
                                    timeGoalieAlarmObject.setPi(null);

                                }
                                timeGoalieAlarmObject.setTargetTime(0);
                                notifyDataSetChanged();

                            } else {
                                goal.setMinutes(goal.getMinutes() + value);
                                notifyDataSetChanged();
                            }
                        }
                    });

                    subtractButtons[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            boolean isRunning=false;


                            if (timeGoalieAlarmObject != null) {
                                if ((goal.getHours()*60 + goal.getMinutes()) > value) {
                                    goal.setMinutes(goal.getMinutes() - value);
                                }

                                isRunning = BaseApplication.getTimeGoalieAlarmObjectById(goal.getGoalId()).isRunning();
                                if (timeGoalieAlarmObject.getCountDownTimer() != null) {
                                    timeGoalieAlarmObject.getCountDownTimer().cancel();
                                    timeGoalieAlarmObject.setCountDownTimer(null);
                                }
                                if (timeGoalieAlarmObject.getPi() != null) {
                                    TimeGoalieAlarmManager.cancelTimeGoalAlarm(view.getContext(),
                                            timeGoalieAlarmObject.getPi());
                                    timeGoalieAlarmObject.setPi(null);

                                }
                                timeGoalieAlarmObject.setTargetTime(0);
                                notifyDataSetChanged();


                            } else {
                                goal.setMinutes(goal.getMinutes() - value);
                                notifyDataSetChanged();
                            }
                        }
                    });
                }
            } //end add edit buttons

        } //end if itemviewcount
    }//end BindViewHolder

    /* This will actually link the static list of Alarms and Countdowntimers in the baseapplication
       to the textview
    */


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
        private Button smallAdd;
        private Button mediumAdd;
        private Button largeAdd;
        private Button smallSubtract;
        private Button mediumSubtract;
        private Button largeSubtract;
        private SeekBar seekbar;
        private ObjectAnimator objanim;
        private TextView tv_timeOutOf;


        public GoalViewHolder(View view) {
            super(view);
            mView = view;
            tv_goaltitle = view.findViewById(R.id.tv_goal_title);
            pencil = view.findViewById(R.id.pencil_button);
            editButtons =  view.findViewById(R.id.edit_button_ll);
            startStopTimer =  view.findViewById(R.id.start_stop);
            time_tv =  view.findViewById(R.id.timeTextView);

            smallAdd =  view.findViewById(R.id.plus_small);
            mediumAdd =  view.findViewById(R.id.plus_medium);
            largeAdd =  view.findViewById(R.id.plus_large);
            smallSubtract =  view.findViewById(R.id.minus_small);
            mediumSubtract =  view.findViewById(R.id.minus_medium);
            largeSubtract =  view.findViewById(R.id.minus_large);
            seekbar =  view.findViewById(R.id.goalProgressBar);
            tv_timeOutOf = view.findViewById(R.id.timeTextView_outOf);


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
            if (seekbar!=null ) {
                seekbar.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return true;
                    }
                });
                seekbar.setMax(seekbar.getMax()*100);

//                AnimatedVectorDrawable anim = (AnimatedVectorDrawable) view.getResources().getDrawable(R.drawable.anim_soccerball_small,null);
//                iv.setImageDrawable(anim);

                RotateDrawable rt= new RotateDrawable();

                rt.setDrawable(view.getResources().getDrawable(R.drawable.soccerball_small,null));
                rt.setFromDegrees(0f);
                rt.setToDegrees(70f);
                objanim = ObjectAnimator.ofInt(rt, "level", 10000);
                objanim.setInterpolator(new LinearInterpolator());
                objanim.setDuration(1000);
                objanim.setRepeatCount(ValueAnimator.INFINITE);
                seekbar.setThumb(rt);
//                RotateAnimation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//                animation.setDuration(500);
//                animation.setRepeatMode(Animation.INFINITE);
//                iv.startAnimation(animation);
//                anim.start();



            }


        }

        @Override
        public String toString() {
            return super.toString() + " '" + tv_goaltitle.getText() + "'";
        }
    }
}

