package us.mindbuilders.petemit.timegoalie;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.RotateDrawable;

import android.support.transition.AutoTransition;
import android.support.transition.TransitionManager;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;

import android.widget.ImageButton;
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
import us.mindbuilders.petemit.timegoalie.data.DeleteGoal;
import us.mindbuilders.petemit.timegoalie.data.GetSuccessfulGoalCount;
import us.mindbuilders.petemit.timegoalie.data.InsertNewGoalEntry;
import us.mindbuilders.petemit.timegoalie.services.TimeGoalieAlarmReceiver;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieAlarmManager;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieDateUtils;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieUtils;

/**
 * Data handler for goal recyclerview.  This is turning out to be the brains of this operation
 */

public class GoalRecyclerViewAdapter extends
        RecyclerView.Adapter<GoalRecyclerViewAdapter.GoalViewHolder>
        implements BaseApplication.GoalActivityListListener {

    // private final List<DummyContent.DummyItem> mValues;

    private View.OnClickListener onClickListener;
    private ArrayList<Goal> goalArrayList;
    private boolean isToday;
    private GoalCounter goalCounter;
    private Context context;
    private GoalEntryGoalCounter goalEntryGoalCounter;
    ViewTreeObserver.OnGlobalLayoutListener layoutListener;

    @Override
    public void notifyChanges(GoalEntry goalEntry) {
        if (goalArrayList != null) {
            for (int i = 0; i < goalArrayList.size(); i++) {
                if (goalEntry != null) {
                    if (goalEntry.getGoal_id() == goalArrayList.get(i).getGoalId()) {
                        goalArrayList.get(i).setGoalEntry(goalEntry);
                    }
                }
            }
        }
    }


    public interface GoalCounter {
        void updateGoalCounter(int add);
    }

    //    public GoalRecyclerViewAdapter(List<DummyContent.DummyItem> items, View.OnClickListener onClickListener) {
//        mValues = items;
//        this.onClickListener = onClickListener;
//    }
    public GoalRecyclerViewAdapter(View.OnClickListener onClickListener, GoalCounter goalCounter,
                                   Context context) {
        this.onClickListener = onClickListener;
        this.goalCounter = goalCounter;
        this.context = context;
        BaseApplication.setGoalActivityListListener(this);
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
    public void onBindViewHolder(final GoalViewHolder holder, final int position) {
        if (getItemCount() > 0) {
            final Goal goal = goalArrayList.get(position);

            TimeGoalieAlarmObject tj = new TimeGoalieAlarmObject(goal.getGoalId(),
                    TimeGoalieDateUtils.getSqlDateString());
            BaseApplication.getTimeGoalieAlarmObjects().add(tj);


            goalEntryGoalCounter = new GoalEntryGoalCounter(goalCounter,
                    TimeGoalieDateUtils.getSqlDateString(BaseApplication.getActiveCalendarDate()));


            //cursor.moveToPosition(position);
            holder.tv_goaltitle.setText(goal.getName());
            holder.tv_goaltitle.setContentDescription(goal.getName());
            if (holder.spinningBallAnim != null) {
                holder.spinningBallAnim.cancel();
            }

            if (goal.getGoalTypeId() == 1) { // if this is GoalType Limit goal
                if (goal.getGoalEntry() != null) {

                }

                if (holder.seekbar != null) {
                    holder.seekbar.setProgressDrawable(holder.seekbar.getResources().
                            getDrawable(R.drawable.seekbar_reverse, null));
                }
            } else if (goal.getGoalTypeId() == 2) { // yes no
                if (goal.getGoalEntry().getHasSucceeded()) {
                    holder.goalCheckBox.setChecked(true);
                } else {
                    holder.goalCheckBox.setChecked(false);
                }
            }

            //if statement checks to see if this is a time goal by the existence of a start/stop button
            if (holder.startStopTimer != null) {

                long remainingSeconds = TimeGoalieUtils.getRemainingSeconds(goal);

                //Set initial Time Text labels:
                TimeGoalieUtils.setTimeTextLabel(goal, holder.time_tv, holder.tv_timeOutOf);
                // if this is a more goal
                if (goal.getGoalTypeId() == 0) {

                    if (holder.seekbar != null) {
                        holder.seekbar.setProgress((int) ((1 - ((double) (remainingSeconds) /
                                goal.getGoalSeconds())) * 100 * 100));
                    }

                } else {
                    if (remainingSeconds < 0) {
                    } else {

                        //set Progress bar Progress
                        if (holder.seekbar != null) {
                            holder.seekbar.setProgress((int) ((1 - ((double) (remainingSeconds) /
                                    goal.getGoalSeconds())) * 100 * 100));
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

                            Log.e("mindbuilders3", goal.getName() + " tick " +
                                    goal.getGoalEntry().getSecondsElapsed());

                            long newtime = goal.getGoalSeconds();
                            if (goal.getGoalEntry() != null) {
                                newtime = goal.getGoalSeconds() - goal.getGoalEntry().getSecondsElapsed();
                            }
                            if (b && goal.getGoalEntry().getDate()
                                    .equals(TimeGoalieDateUtils.getSqlDateString())) {
                                TimeGoalieAlarmManager.startTimer(goalCounter, holder.time_tv, newtime, goal,
                                        compoundButton.getContext(), holder.seekbar);
                                holder.spinningBallAnim.start();
                                //Start the Goal!!
                                goal.getGoalEntry().setRunning(true);
                                Log.e("mindbuilders4", goal.getName() + " tick " +
                                        goal.getGoalEntry().getSecondsElapsed());
                                new InsertNewGoalEntry(
                                        compoundButton.getContext()).execute(goal.getGoalEntry());
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
                                    //cancel killgoal intent

                                    Intent killGoalIntent = TimeGoalieAlarmReceiver
                                            .createKillGoalTimeGoalieAlarmIntent(context,
                                            "Stopping goal due to inactivity",(int)goal.getGoalId());

                                    PendingIntent killGoalPi = TimeGoalieAlarmReceiver
                                    .createKillGoalSafetyPendingIntent(context,
                                            killGoalIntent, (int)goal.getGoalId());

                                    TimeGoalieAlarmManager.cancelTimeGoalAlarm(context,killGoalPi);

                                    //                   TimeGoalieAlarmReceiver.cancelSecondlyAlarm(context, goal);
                                    //timeGoalieAlarmObject.setRunning(false);
                                    goal.getGoalEntry().setRunning(false);
                                    Log.e("mindbuilders5", goal.getName() + " tick " +
                                            goal.getGoalEntry().getSecondsElapsed());
                                    new InsertNewGoalEntry(
                                            compoundButton.getContext()).execute(goal.getGoalEntry());
                                }


                                holder.spinningBallAnim.cancel();
                            }
                        }
                    });

                    if (goal.getGoalEntry() != null) {
                        if (goal.getGoalEntry().isRunning()
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

                    int[] incrementValues = holder.smallAdd.getContext().getResources()
                            .getIntArray(R.array.incrementArray);
                    // edit buttons
                    Button[] addButtons = new Button[]{holder.smallAdd, holder.mediumAdd, holder.largeAdd};
                    Button[] subtractButtons = new Button[]{holder.smallSubtract, holder.mediumSubtract,
                            holder.largeSubtract};

                    for (int i = 0; i < incrementValues.length; i++) {
                        final int value = incrementValues[i];

                        addButtons[i].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (timeGoalieAlarmObject != null) {
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
                                }
                                //old goal.setMinutes(goal.getMinutes() + value);
                                goal.getGoalEntry().setGoalAugment(
                                        goal.getGoalEntry().getGoalAugment() + value * 60);

                                TimeGoalieAlarmReceiver.cancelSecondlyAlarm(context, goal);

                                //timeGoalieAlarmObject.setTargetTime(0);
                                goal.getGoalEntry().setTargetTime(0);
                                new InsertNewGoalEntry(context).execute(goal.getGoalEntry());
                                notifyDataSetChanged();


                            }
                        });

                        subtractButtons[i].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {


                                if (timeGoalieAlarmObject != null) {

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

                                }
                                if (goal.getGoalSeconds() / 60 > value) {
                                    goal.getGoalEntry().setGoalAugment(
                                            goal.getGoalEntry().getGoalAugment() - value * 60);
                                }
                                TimeGoalieAlarmReceiver.cancelSecondlyAlarm(context, goal);
                                //timeGoalieAlarmObject.setTargetTime(0);
                                goal.getGoalEntry().setTargetTime(0);
                                new InsertNewGoalEntry(context).execute(goal.getGoalEntry());
                                notifyDataSetChanged();
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
                if (holder.goalCheckBox.isChecked()) {
                    layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            int[] ballLocation = new int[2];
                            holder.soccerBallImage.getLocationOnScreen(ballLocation);
                            int[] checkboxLocation = new int[2];
                            holder.goalCheckBox.getLocationOnScreen(checkboxLocation);

                            float distance = Math.abs(ballLocation[0] - checkboxLocation[0] +
                                    holder.goalCheckBox.getMeasuredWidth());
                            holder.soccerBallImage.animate().translationX(distance)
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
                            holder.goalCheckBox.getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener);
                        }
                    };
                    holder.goalCheckBox.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
                }

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


                                //get the distance properly
                                int[] ballLocation = new int[2];
                                holder.soccerBallImage.getLocationOnScreen(ballLocation);
                                int[] checkboxLocation = new int[2];
                                holder.goalCheckBox.getLocationOnScreen(checkboxLocation);

                                float distance = Math.abs(ballLocation[0] - checkboxLocation[0] +
                                        holder.goalCheckBox.getMeasuredWidth());
                                holder.soccerBallImage.animate().translationX(distance)
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
                        } else {

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
                            }

                        }
                    }
                });
            }

            //update goal counter to reflect time goals to limit


            if (goal.getGoalTypeId() == 1) { // if this is GoalType Limit goal
                if (goal.getGoalEntry() != null) {
                    if (!goal.getGoalEntry().isHasFinished() &&
                            !goal.getGoalEntry().getHasSucceeded()) {
                        goal.getGoalEntry().setHasSucceeded(1);
                    } else if (goal.getGoalEntry().isHasFinished() &&
                            goal.getGoalEntry().getHasSucceeded()) {
                        goal.getGoalEntry().setHasSucceeded(0);
                    }
                }
                new GetSuccessfulGoalCount(context).execute(goalEntryGoalCounter);
            }

            //Let's do the delete button

            if (holder.deleteButton != null) {
                if (goal != null) {
                    holder.deleteButton.setContentDescription(context.getString(
                            R.string.delete_button_content_description).concat(" ").concat(
                            goal.getName())
                    );
                }
                holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Animation fadeOut = new AlphaAnimation(1, 0);
                        fadeOut.setInterpolator(new AccelerateInterpolator());
                        fadeOut.setStartOffset(500);
                        fadeOut.setDuration(500);
                        holder.cardView.setAnimation(fadeOut);
                        goalArrayList.remove(position);
                        notifyItemRangeChanged(position, getItemCount());
                        notifyItemRemoved(position);

                        new DeleteGoal(context, goalEntryGoalCounter).execute(goal);

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
        private ToggleButton yes_no_pencil;
        private ImageButton deleteButton;
        private CardView cardView;


        public GoalViewHolder(View view) {
            super(view);
            mView = view;
            tv_goaltitle = view.findViewById(R.id.tv_goal_title);
            pencil = view.findViewById(R.id.pencil_button);
            yes_no_pencil = view.findViewById(R.id.yes_no_pencil_button);
            editButtons = view.findViewById(R.id.edit_button_ll);
            startStopTimer = view.findViewById(R.id.start_stop);
            time_tv = view.findViewById(R.id.timeTextView);
            deleteButton = view.findViewById(R.id.delete_button);

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
            cardView = view.findViewById(R.id.card_layout);
            RotateDrawable rt;


            if (pencil != null) {
                pencil.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                        if (editButtons.getVisibility() != View.VISIBLE) {
                            editButtons.setVisibility(View.VISIBLE);
                        } else {
                            editButtons.setVisibility(View.GONE);
                        }

                        if (deleteButton.getVisibility() != View.VISIBLE) {
                            deleteButton.setVisibility(View.VISIBLE);
                        } else {
                            deleteButton.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }

            if (yes_no_pencil != null) {
                yes_no_pencil.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (deleteButton.getVisibility() != View.VISIBLE) {
                            deleteButton.setVisibility(View.VISIBLE);
                        } else {
                            deleteButton.setVisibility(View.INVISIBLE);
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

                rt = new RotateDrawable();

                rt.setDrawable(view.getResources().getDrawable(R.drawable.soccerball_small, null));
                rt.setFromDegrees(0f);
                rt.setToDegrees(70f);
                spinningBallAnim = ObjectAnimator.ofInt(rt, "level", 10000);
                spinningBallAnim.setInterpolator(new LinearInterpolator());
                spinningBallAnim.setDuration(1000);
                spinningBallAnim.setRepeatCount(ValueAnimator.INFINITE);
                seekbar.setThumb(rt);
            }

            if (goalCheckBox != null) {

                spinningBallAnim = ObjectAnimator.ofFloat(soccerBallImage, "rotation", 0f, 70f);
                spinningBallAnim.setInterpolator(new LinearInterpolator());
                spinningBallAnim.setDuration(1000);
                spinningBallAnim.setRepeatCount(ValueAnimator.INFINITE);
            }


        }

        @Override
        public String toString() {
            return super.toString() + " '" + tv_goaltitle.getText() + "'";
        }
    }
}

