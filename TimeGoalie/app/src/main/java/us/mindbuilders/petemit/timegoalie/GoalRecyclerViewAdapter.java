package us.mindbuilders.petemit.timegoalie;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;

import android.content.Context;
import android.graphics.drawable.RotateDrawable;

import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;


import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;

import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.GoalEntry;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.GoalEntryGoalCounter;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.TimeGoalieAlarmObject;
import us.mindbuilders.petemit.timegoalie.data.GetSuccessfulGoalCount;
import us.mindbuilders.petemit.timegoalie.data.InsertNewGoalEntry;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieAlarmManager;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieDateUtils;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieUtils;

/**
 * Data handler for goal recyclerview.  This is turning out to be the brains of this operation
 */

public class GoalRecyclerViewAdapter extends RecyclerView.Adapter<GoalRecyclerViewAdapter.GoalViewHolder> {

    // private final List<DummyContent.DummyItem> mValues;

    private View.OnClickListener onClickListener;
    private ArrayList<Goal> goalArrayList;
    private boolean isToday;
    private GoalCounter goalCounter;
    private Context context;
    private GoalEntryGoalCounter goalEntryGoalCounter;


    public interface GoalCounter {
        void updateGoalCounter(int add);
    }

    //    public GoalRecyclerViewAdapter(List<DummyContent.DummyItem> items, View.OnClickListener onClickListener) {
//        mValues = items;
//        this.onClickListener = onClickListener;
//    }
    public GoalRecyclerViewAdapter(View.OnClickListener onClickListener, GoalCounter goalCounter, Context context) {
        this.onClickListener = onClickListener;
        this.goalCounter = goalCounter;
        this.context = context;

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
        for (TimeGoalieAlarmObject tgoal : BaseApplication.getTimeGoalieAlarmObjects()) {
            if (tgoal.getCountDownTimer() != null)
                tgoal.getCountDownTimer().cancel();
        }
        this.goalArrayList = goalArrayList;
        notifyDataSetChanged();
    }

    public void swapCursor(ArrayList<Goal> goalArrayList, boolean isToday) {
        for (TimeGoalieAlarmObject tgoal : BaseApplication.getTimeGoalieAlarmObjects()) {
            if (tgoal.getCountDownTimer() != null)
                tgoal.getCountDownTimer().cancel();
        }
        this.goalArrayList = goalArrayList;
        this.isToday = isToday;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final GoalViewHolder holder, int position) {
        if (getItemCount() > 0) {
            final Goal goal = goalArrayList.get(position);

            goalEntryGoalCounter = new GoalEntryGoalCounter(goalCounter,
                    TimeGoalieDateUtils.getSqlDateString(BaseApplication.getActiveCalendarDate()));


            //cursor.moveToPosition(position);
            holder.tv_goaltitle.setText(goal.getName());
            if (holder.spinningBallAnim != null) {
                holder.spinningBallAnim.cancel();
            }

            if (true) {
                if (goal.getGoalEntry() != null) {
                    if (goal.getGoalEntry().getHasSucceeded()==1) {
                       new InsertNewGoalEntry(context).execute(goal.getGoalEntry());
                        new GetSuccessfulGoalCount(context).execute(goalEntryGoalCounter);
                    }
                }
            }

            if (goal.getGoalTypeId() == 1) { // if this is GoalType Limit goal
                if (goal.getGoalEntry() != null) {
                    if (!goal.getGoalEntry().isHasFinished() &&
                            goal.getGoalEntry().getHasSucceeded() != 1) {

                        goal.getGoalEntry().setHasSucceeded(1);
                        new InsertNewGoalEntry(context).execute(goal.getGoalEntry());
                        new GetSuccessfulGoalCount(context).execute(goalEntryGoalCounter);
                    } else if (goal.getGoalEntry().isHasFinished() &&
                            goal.getGoalEntry().getHasSucceeded() != 0) {
                        goal.getGoalEntry().setHasSucceeded(0);
                        new InsertNewGoalEntry(context).execute(goal.getGoalEntry());
                        new GetSuccessfulGoalCount(context).execute(goalEntryGoalCounter);
                    }

                }
                if (holder.seekbar != null) {
                    holder.seekbar.setProgressDrawable(holder.seekbar.getResources().
                            getDrawable(R.drawable.seekbar_reverse, null));
                }
            } else if (goal.getGoalTypeId() == 2) { // yes no
                if (goal.getGoalEntry().getHasSucceeded()==1) {
                    holder.goalCheckBox.setChecked(true);
                }
            }
//            } else if (goal.getGoalTypeId() == 2) {//yes no goal
//                if (goal.getGoalEntry() != null) {
//                    if (goal.getGoalEntry().getHasSucceeded() == 1) {
//                        goalCounter.updateGoalCounter(true);
//                    }
//                }
//            }


            //if statement checks to see if this is a time goal by the existence of a start/stop button
            if (holder.startStopTimer != null) {

                //setup initial timer text
                long onBindElapsedSeconds = 0;

                TimeGoalieAlarmObject timeGoalieAlarmObject =
                        TimeGoalieUtils.getTimeGoalieAlarmObjectByDate(goal);

                long remainingSeconds = TimeGoalieUtils.getRemainingSeconds(timeGoalieAlarmObject,goal);

                //Set initial Time Text labels:
                TimeGoalieUtils.setTimeTextLabel(goal,timeGoalieAlarmObject,holder.time_tv,holder.tv_timeOutOf);
                // if this is a more goal
                if (goal.getGoalTypeId()==0) {

                    if (holder.seekbar != null) {
                        holder.seekbar.setProgress((int) ((1 - ((double) (remainingSeconds) / goal.getGoalSeconds())) * 100 * 100));
                    }

                } else {
                    if (remainingSeconds < 0) {
                    } else {

                        //set Progress bar Progress
                        if (holder.seekbar != null) {
                            holder.seekbar.setProgress((int) ((1 - ((double) (remainingSeconds) / goal.getGoalSeconds())) * 100 * 100));
                        }
                    }
                }
                if (isToday) {
                    holder.startStopTimer.setVisibility(View.VISIBLE);
                    holder.startStopTimer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            TimeGoalieAlarmObject timeGoalieAlarmObject =
                                    BaseApplication.getTimeGoalieAlarmObjectById((goal.getGoalId()));

                            if (goal.getGoalEntry() != null) {
                                goal.getGoalEntry().setDate(TimeGoalieDateUtils.getSqlDateString());
                                if (goal.getGoalTypeId() == 1 && !goal.getGoalEntry().isHasFinished()) {
                                    goal.getGoalEntry().setHasSucceeded(1);
                                }
                                new InsertNewGoalEntry(
                                        compoundButton.getContext()).execute(goal.getGoalEntry());
                            }

                            long newtime = goal.getGoalSeconds();
                            if (timeGoalieAlarmObject != null) {
                                newtime = goal.getGoalSeconds() - timeGoalieAlarmObject.getSecondsElapsed();
                                Log.e("Mindbuilders", "newtime: " + newtime);
                            }
                            if (b && goal.getGoalEntry().getDate()
                                    .equals(TimeGoalieDateUtils.getSqlDateString())) {
                                TimeGoalieAlarmManager.startTimer(goalCounter, holder.time_tv, newtime, goal,
                                        compoundButton, holder.seekbar);
                                holder.spinningBallAnim.start();
                            } else {
                                if (timeGoalieAlarmObject != null) {
                                    if (timeGoalieAlarmObject.getCountDownTimer() != null) {
                                        timeGoalieAlarmObject.getCountDownTimer().cancel();
                                        timeGoalieAlarmObject.setCountDownTimer(null);
                                    }
                                    if (timeGoalieAlarmObject.getAlarmDonePendingIntent() != null) {
                                        TimeGoalieAlarmManager.cancelTimeGoalAlarm(
                                                compoundButton.getContext(),
                                                timeGoalieAlarmObject.getAlarmDonePendingIntent());
                                        timeGoalieAlarmObject.getAlarmDonePendingIntent().cancel();
                                        timeGoalieAlarmObject.setAlarmDonePendingIntent(null);
                                    }
                                    if (timeGoalieAlarmObject.getOneMinuteWarningPendingIntent() != null) {
                                        TimeGoalieAlarmManager.cancelTimeGoalAlarm(
                                                compoundButton.getContext(),
                                                timeGoalieAlarmObject.getOneMinuteWarningPendingIntent());
                                        timeGoalieAlarmObject.getOneMinuteWarningPendingIntent().cancel();
                                        timeGoalieAlarmObject.setOneMinuteWarningPendingIntent(null);
                                    }
                                    timeGoalieAlarmObject.setRunning(false);
                                }


                                holder.spinningBallAnim.cancel();
                            }
                        }
                    });

                    if (BaseApplication.getTimeGoalieAlarmObjectById(goal.getGoalId()) != null) {
                        if (BaseApplication.getTimeGoalieAlarmObjectById(goal.getGoalId()).isRunning()
                                && goal.getGoalEntry().getDate()
                                .equals(TimeGoalieDateUtils.getSqlDateString())) {
                            holder.startStopTimer.setChecked(false);
                            holder.startStopTimer.setChecked(true);
                        }
                    }


                }// end start/stop
                else {
                    holder.startStopTimer.setVisibility(View.GONE);
                }
            }//if istoday
            holder.mView.setOnClickListener(onClickListener);

            if (isToday) {
                if (holder.pencil != null) {
                    holder.pencil.setVisibility(View.VISIBLE);
                }
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
                                boolean isRunning = false;

                                if (timeGoalieAlarmObject != null) {
                                    if (goal.getGoalEntry() == null) {
                                        goal.setGoalEntry(new GoalEntry(goal.getGoalId(),
                                                TimeGoalieDateUtils.getSqlDateString()));
                                    }
                                    //old goal.setMinutes(goal.getMinutes() + value);
                                    goal.getGoalEntry().setGoalAugment(
                                            goal.getGoalEntry().getGoalAugment() + value * 60);

                                    isRunning = BaseApplication.getTimeGoalieAlarmObjectById(goal.getGoalId()).isRunning();
                                    if (timeGoalieAlarmObject.getCountDownTimer() != null) {
                                        timeGoalieAlarmObject.getCountDownTimer().cancel();
                                        timeGoalieAlarmObject.setCountDownTimer(null);
                                    }
                                    if (timeGoalieAlarmObject.getAlarmDonePendingIntent() != null) {
                                        TimeGoalieAlarmManager.cancelTimeGoalAlarm(view.getContext(),
                                                timeGoalieAlarmObject.getAlarmDonePendingIntent());
                                        timeGoalieAlarmObject.setAlarmDonePendingIntent(null);
                                    }
                                    if (timeGoalieAlarmObject.getOneMinuteWarningPendingIntent() != null) {
                                        TimeGoalieAlarmManager.cancelTimeGoalAlarm(view.getContext(),
                                                timeGoalieAlarmObject.getOneMinuteWarningPendingIntent());
                                        timeGoalieAlarmObject.setOneMinuteWarningPendingIntent(null);
                                    }
                                    timeGoalieAlarmObject.setTargetTime(0);
                                    notifyDataSetChanged();

                                } else {
                                    goal.getGoalEntry().setGoalAugment(
                                            goal.getGoalEntry().getGoalAugment() + value * 60);
                                    notifyDataSetChanged();
                                }
                            }
                        });

                        subtractButtons[i].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                boolean isRunning = false;


                                if (timeGoalieAlarmObject != null) {
                                    if (goal.getGoalSeconds() / 60 > value) {
                                        goal.getGoalEntry().setGoalAugment(
                                                goal.getGoalEntry().getGoalAugment() - value * 60);
                                    }

                                    isRunning = BaseApplication.getTimeGoalieAlarmObjectById(goal.getGoalId()).isRunning();
                                    if (timeGoalieAlarmObject.getCountDownTimer() != null) {
                                        timeGoalieAlarmObject.getCountDownTimer().cancel();
                                        timeGoalieAlarmObject.setCountDownTimer(null);
                                    }
                                    if (timeGoalieAlarmObject.getAlarmDonePendingIntent() != null) {
                                        TimeGoalieAlarmManager.cancelTimeGoalAlarm(view.getContext(),
                                                timeGoalieAlarmObject.getAlarmDonePendingIntent());
                                        timeGoalieAlarmObject.setAlarmDonePendingIntent(null);
                                    }
                                    if (timeGoalieAlarmObject.getOneMinuteWarningPendingIntent() != null) {
                                        TimeGoalieAlarmManager.cancelTimeGoalAlarm(view.getContext(),
                                                timeGoalieAlarmObject.getOneMinuteWarningPendingIntent());
                                        timeGoalieAlarmObject.setOneMinuteWarningPendingIntent(null);
                                    }
                                    timeGoalieAlarmObject.setTargetTime(0);
                                    notifyDataSetChanged();


                                } else {
                                    goal.getGoalEntry().setGoalAugment(
                                            goal.getGoalEntry().getGoalAugment() - value * 60);
                                    notifyDataSetChanged();
                                }
                            }
                        });
                    }
                } //end add edit buttons
            }//end istoday
            else {
                if (holder.pencil != null) {
                    holder.pencil.setVisibility(View.GONE);
                    holder.editButtons.setVisibility(View.GONE);
                }
            }


            //and now, the goal Checkbox

            if (holder.goalCheckBox != null) {
                holder.goalCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            if (goal.getGoalEntry() != null) {
                                goal.getGoalEntry().setHasSucceeded(1);
                                goal.getGoalEntry().setHasFinished(true);
                                new InsertNewGoalEntry(compoundButton.getContext())
                                        .execute(goal.getGoalEntry());
                                new InsertNewGoalEntry(context).execute(goal.getGoalEntry());
                                new GetSuccessfulGoalCount(context).execute(goalEntryGoalCounter);
                                holder.soccerBallImage.animate().translationX(600f)
                                        .setDuration(1000)
                                        .setInterpolator(new AccelerateDecelerateInterpolator())
                                .setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animator) {
                                        holder.spinningBallAnim.start();
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animator) {
                                        holder.spinningBallAnim.cancel();
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animator) {
                                        holder.spinningBallAnim.cancel();
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animator) {

                                    }
                                });
                            }
                        }
                        else {

                            if (goal.getGoalEntry() != null) {
                                goal.getGoalEntry().setHasSucceeded(0);
                                goal.getGoalEntry().setHasFinished(true);
                                new InsertNewGoalEntry(compoundButton.getContext())
                                        .execute(goal.getGoalEntry());
                                new InsertNewGoalEntry(context).execute(goal.getGoalEntry());
                                new GetSuccessfulGoalCount(context).execute(goalEntryGoalCounter);
                                holder.soccerBallImage.animate().translationX(0)
                                        .setDuration(1000)
                                        .setInterpolator(new AccelerateDecelerateInterpolator())
                                        .setListener(new Animator.AnimatorListener() {
                                            @Override
                                            public void onAnimationStart(Animator animator) {
                                                holder.spinningBallAnim.start();
                                            }

                                            @Override
                                            public void onAnimationEnd(Animator animator) {
                                                holder.spinningBallAnim.cancel();

                                            }

                                            @Override
                                            public void onAnimationCancel(Animator animator) {
                                                holder.spinningBallAnim.cancel();
                                            }

                                            @Override
                                            public void onAnimationRepeat(Animator animator) {

                                            }
                                        });

//                                if (holder.soccerBallImage != null && holder.moveSoccerBallAnim != null) {
//                                    holder.soccerBallImage.startAnimation(holder.moveSoccerBallAnim);
//                                }
                            }

                        }
                    }
                });
            }



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
        private ObjectAnimator spinningBallAnim;
        private TextView tv_timeOutOf;
        private AppCompatCheckBox goalCheckBox;
        private TranslateAnimation moveSoccerBallAnim;
        private ImageView soccerBallImage;


        public GoalViewHolder(View view) {
            super(view);
            mView = view;
            tv_goaltitle = view.findViewById(R.id.tv_goal_title);
            pencil = view.findViewById(R.id.pencil_button);
            editButtons = view.findViewById(R.id.edit_button_ll);
            startStopTimer = view.findViewById(R.id.start_stop);
            time_tv = view.findViewById(R.id.timeTextView);

            smallAdd = view.findViewById(R.id.plus_small);
            mediumAdd = view.findViewById(R.id.plus_medium);
            largeAdd = view.findViewById(R.id.plus_large);
            smallSubtract = view.findViewById(R.id.minus_small);
            mediumSubtract = view.findViewById(R.id.minus_medium);
            largeSubtract = view.findViewById(R.id.minus_large);
            seekbar = view.findViewById(R.id.goalProgressBar);
            tv_timeOutOf = view.findViewById(R.id.timeTextView_outOf);
            goalCheckBox = view.findViewById(R.id.yes_no_checkbox);
            soccerBallImage = view.findViewById(R.id.soccer_ball_image);
            RotateDrawable rt=null;


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
            if (seekbar != null) {
                seekbar.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return true;
                    }
                });
                seekbar.setMax(seekbar.getMax() * 100);

//                AnimatedVectorDrawable anim = (AnimatedVectorDrawable) view.getResources().getDrawable(R.drawable.anim_soccerball_small,null);
//                iv.setImageDrawable(anim);

                rt = new RotateDrawable();

                rt.setDrawable(view.getResources().getDrawable(R.drawable.soccerball_small, null));
                rt.setFromDegrees(0f);
                rt.setToDegrees(70f);
                spinningBallAnim = ObjectAnimator.ofInt(rt, "level", 10000);
                spinningBallAnim.setInterpolator(new LinearInterpolator());
                spinningBallAnim.setDuration(1000);
                spinningBallAnim.setRepeatCount(ValueAnimator.INFINITE);
                seekbar.setThumb(rt);
//                RotateAnimation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//                animation.setDuration(500);
//                animation.setRepeatMode(Animation.INFINITE);
//                iv.startAnimation(animation);
//                anim.start();


            }

            if (goalCheckBox != null) {


                spinningBallAnim = ObjectAnimator.ofFloat(soccerBallImage, "rotation", 0f,70f);
                spinningBallAnim.setInterpolator(new LinearInterpolator());
                spinningBallAnim.setDuration(1000);
                spinningBallAnim.setRepeatCount(ValueAnimator.INFINITE);


//                moveSoccerBallAnim = new TranslateAnimation(0, 200f,0,0f);
//                moveSoccerBallAnim.setDuration(1000);
//                moveSoccerBallAnim.setFillAfter(true);
//                moveSoccerBallAnim.setRepeatCount(1);
//                moveSoccerBallAnim.setRepeatMode(Animation.REVERSE);

            }



        }

        @Override
        public String toString() {
            return super.toString() + " '" + tv_goaltitle.getText() + "'";
        }
    }
}

