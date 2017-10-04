package us.mindbuilders.petemit.timegoalie;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlarmManager;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RotateDrawable;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
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
                // long onBindElapsedSeconds = goal.getGoalEntry().getSecondsElapsed();
                if (BaseApplication.getTimeGoalieAlarmObjectById(goal.getGoalId()) != null) {
                    TimeGoalieAlarmObject timeGoalieAlarmObj = BaseApplication.getTimeGoalieAlarmObjectById(goal.getGoalId());
                    //Recalculate Elapsed Seconds

                    if (timeGoalieAlarmObj.getTargetTime() != 0 && timeGoalieAlarmObj.isRunning()) {
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
                long remainingSeconds = (goal.getGoalSeconds() -
                        onBindElapsedSeconds);

                Log.e("Mindbuilders", "remainingSeconds: " + remainingSeconds);
                if (remainingSeconds < 0) {
                    holder.time_tv.setText(TimeGoalieAlarmManager.makeTimeTextFromMillis(0));
                    if (holder.seekbar != null) {
                        holder.seekbar.setProgress(0);
                    }
                } else {
                    holder.time_tv.setText(TimeGoalieAlarmManager.makeTimeTextFromMillis(remainingSeconds * 1000));
                    //set Progress bar Progress
                    if (holder.seekbar != null) {
                        holder.seekbar.setProgress((int)((1-((double)(remainingSeconds)/goal.getGoalSeconds()))*100*100));
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
                            startTimer(holder.time_tv, newtime, goal, compoundButton, holder.seekbar);
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
                                goal.setMinutes(goal.getMinutes() - value);

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
            }
        }
    }

    /* This will actually link the static list of Alarms and Countdowntimers in the baseapplication
       to the textview
    */
    public void startTimer(TextView time_tv, long totalSeconds, Goal goal, View view, SeekBar seekbar) {

        TimeGoalieAlarmObject timeGoalieAlarmObject =
                BaseApplication.getTimeGoalieAlarmObjectById((goal.getGoalId()));
        long remainingSeconds = totalSeconds;// - secondsElapsed;
        Log.e("Mindbuilders", "remainingseconds: " + remainingSeconds);
        if (timeGoalieAlarmObject != null) {
            timeGoalieAlarmObject.setCountDownTimer(
                    TimeGoalieAlarmManager.makeCountdownTimer(
                            remainingSeconds,
                            1,
                            goal.getGoalSeconds(),
                            time_tv,
                            goal.getGoalEntry(),
                            seekbar));
            timeGoalieAlarmObject.getCountDownTimer().start();
            timeGoalieAlarmObject.setRunning(true);
        } else {
            timeGoalieAlarmObject = new TimeGoalieAlarmObject();
            timeGoalieAlarmObject.setCountDownTimer(
                    TimeGoalieAlarmManager.makeCountdownTimer(
                            remainingSeconds,
                            1,
                            goal.getGoalSeconds(),
                            time_tv,
                            goal.getGoalEntry(),
                            seekbar));
            timeGoalieAlarmObject.getCountDownTimer().start();
            timeGoalieAlarmObject.setRunning(true);
            timeGoalieAlarmObject.setGoal_id(goal.getGoalId());
            BaseApplication.getTimeGoalieAlarmObjects().add(timeGoalieAlarmObject);
        }

        // this will create the system alarm.  :-O !  It will not create it if the pi
        // already exists.

        if (timeGoalieAlarmObject != null && timeGoalieAlarmObject.getPi() == null) {
            timeGoalieAlarmObject.setPi(TimeGoalieAlarmReceiver.createTimeGoaliePendingIntent(
                    view.getContext(), (int) goal.getGoalId(), goal.getName()));

            long hours = remainingSeconds / (60 * 60);
            long minutes = (remainingSeconds - (hours * 60 * 60)) / 60;
            long seconds = (remainingSeconds - (hours * 60 * 60) - (minutes * 60));

            Log.e("Mindbuilders", "hours: " + hours);
            Log.e("Mindbuilders", "minutes: " + minutes);
            Log.e("Mindbuilders", "seconds: " + seconds);

            long targetTime = TimeGoalieDateUtils.createTargetCalendarTime(
                    (int) hours,
                    (int) minutes,
                    (int) seconds);

            //sound the alarm!!
            if (timeGoalieAlarmObject.getTargetTime() == 0) {
                timeGoalieAlarmObject.setTargetTime(targetTime);
            }

            TimeGoalieAlarmManager.setTimeGoalAlarm(
                    targetTime,
                    view.getContext(), null,
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
        private Button smallAdd;
        private Button mediumAdd;
        private Button largeAdd;
        private Button smallSubtract;
        private Button mediumSubtract;
        private Button largeSubtract;
        private SeekBar seekbar;
        private ObjectAnimator objanim;


        public GoalViewHolder(View view) {
            super(view);
            mView = view;
            tv_goaltitle = (TextView) view.findViewById(R.id.tv_goal_title);
            pencil = (ToggleButton) view.findViewById(R.id.pencil_button);
            editButtons = (LinearLayout) view.findViewById(R.id.edit_button_ll);
            startStopTimer = (ToggleButton) view.findViewById(R.id.start_stop);
            time_tv = (TextView) view.findViewById(R.id.timeTextView);

            smallAdd = (Button) view.findViewById(R.id.plus_small);
            mediumAdd = (Button) view.findViewById(R.id.plus_medium);
            largeAdd = (Button) view.findViewById(R.id.plus_large);
            smallSubtract = (Button) view.findViewById(R.id.minus_small);
            mediumSubtract = (Button) view.findViewById(R.id.minus_medium);
            largeSubtract = (Button) view.findViewById(R.id.minus_large);
            seekbar = (SeekBar) view.findViewById(R.id.goalProgressBar);


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

