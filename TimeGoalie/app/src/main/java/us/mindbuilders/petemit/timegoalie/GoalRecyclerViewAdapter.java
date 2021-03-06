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
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
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
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.GoalEntryGoalCounter;

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
        RecyclerView.Adapter<GoalRecyclerViewAdapter.GoalViewHolder> {

    // private final List<DummyContent.DummyItem> mValues;

    ViewTreeObserver.OnGlobalLayoutListener layoutListener;
    private View.OnClickListener onClickListener;
    private ArrayList<Goal> goalArrayList;
    private boolean isToday;
    private GoalCounter goalCounter;
    private Context context;
    private GoalEntryGoalCounter goalEntryGoalCounter;
    private boolean justARefresh;
    public static String checkBoxStateKey;

    //    public GoalRecyclerViewAdapter(List<DummyContent.DummyItem> items, View.OnClickListener onClickListener) {
//        mValues = items;
//        this.onClickListener = onClickListener;
//    }
    public GoalRecyclerViewAdapter(View.OnClickListener onClickListener, GoalCounter goalCounter,
                                   Context context) {
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

        this.goalArrayList = goalArrayList;
        justARefresh = false;
        notifyDataSetChanged();
    }

    public void swapCursor(ArrayList<Goal> goalArrayList, boolean isToday) {

        this.goalArrayList = goalArrayList;
        this.isToday = isToday;
        justARefresh = false;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final GoalViewHolder holder, final int position) {
        if (getItemCount() > 0) {
            final Goal goal = goalArrayList.get(position);


            goalEntryGoalCounter = new GoalEntryGoalCounter(goalCounter,
                    TimeGoalieDateUtils.getSqlDateString(BaseApplication.getActiveCalendarDate()));

            holder.tv_goaltitle.setText(goal.getName());
            holder.tv_goaltitle.setContentDescription(goal.getName());

            //not sure why I need to cancel this:
//            if (holder.spinningBallAnim != null) {
//                holder.spinningBallAnim.cancel();
//            }

            if (goal.getGoalTypeId() == 1) { // if this is GoalType Limit goal
                if (goal.getGoalEntry() != null) {

                }

                if (holder.seekbar != null) {
                    holder.seekbar.setProgressDrawable(holder.seekbar.getResources().
                            getDrawable(R.drawable.seekbar_reverse, null));
                }
            } else if (goal.getGoalTypeId() == 2 && !goal.getGoalEntry().isHasMoved()) { // yes no
                if (goal.getGoalEntry().getHasSucceeded()) {
                    goal.getGoalEntry().setHasMoved(true);
                    holder.goalCheckBox.setChecked(true);
                } else {
                    goal.getGoalEntry().setHasMoved(true);
                    holder.goalCheckBox.setChecked(false);
                }
            }

            //if statement checks to see if this is a time goal by the existence of a start/stop button
            if (holder.startStopTimer != null) {


                long remainingSeconds = TimeGoalieUtils.getRemainingSeconds(goal);

                // Reset start stop button to current state and stop the spinning ball
                if (!goal.getGoalEntry().isRunning()) {
                    holder.spinningBallAnim.cancel();
                    Log.e("controller",goal.getName() + "check1");
                    if (holder.startStopTimer.isChecked()) {
                        holder.startStopTimer.setChecked(false);
                    }
                }

                // Start the spinning ball and activate the start button if the goal is running
                if (goal.getGoalEntry().isRunning() && !holder.startStopTimer.isChecked()) {
                    holder.startStopTimer.setChecked(true);
                    Log.e("controller",goal.getName() + "check2");
                    holder.spinningBallAnim.start();
                }

                //Set Time Text labels:
                TimeGoalieUtils.setTimeTextLabel(goal, holder.time_tv, holder.tv_timeOutOf);
                // if this is a more goal
                if (goal.getGoalTypeId() == 0) {

                        if (holder.seekbar != null && !goal.getGoalEntry().isHasFinished() && !goal.isChangingSeekbar()){
                            if (goal.getSeekbarAnimation() != null) {
                                goal.getSeekbarAnimation().cancel();
                            }

                            ObjectAnimator animation = ObjectAnimator.ofInt(holder.seekbar, "progress",
                                    holder.seekbar.getProgress(), (int)
                                            ((1 - ((double) (remainingSeconds) /  goal.getGoalSeconds()))
                                                    * 100 * 100));
                            animation.setDuration(1000);
                            animation.setAutoCancel(true);
                            animation.setInterpolator(new LinearInterpolator());

                            goal.setSeekbarAnimation(animation);

                            animation.start();
                        }
                        if (holder.seekbar != null &&
                                TimeGoalieDateUtils.calculateSecondsElapsed(goal.getGoalEntry().getStartedTime(),
                                        goal.getGoalEntry().getSecondsElapsed()) >= goal.getGoalSeconds()) {
                            holder.seekbar.setProgress(10000);
                            if (!goal.getGoalEntry().isRunning()) {
                                holder.spinningBallAnim.cancel();
                            }
                        }



                } else {

                    if (holder.seekbar != null &&
                            TimeGoalieDateUtils.calculateSecondsElapsed(goal.getGoalEntry().getStartedTime(),
                                    goal.getGoalEntry().getSecondsElapsed()) >= goal.getGoalSeconds()) {
                        holder.seekbar.setProgress(10000);
                        if (!goal.getGoalEntry().isRunning()) {
                            holder.spinningBallAnim.cancel();
                        }
                    }

                    if (remainingSeconds < 0) {
                    } else {

                        //set Progress bar Progress
                        if (holder.seekbar != null && !goal.isChangingSeekbar()) {
//                            holder.seekbar.setProgress((int) ((1 - ((double) (remainingSeconds) /
//                                    goal.getGoalSeconds())) * 100 * 100));
                            if (goal.getSeekbarAnimation() != null) {
                                goal.getSeekbarAnimation().cancel();
                            }


                                ObjectAnimator animation = ObjectAnimator.ofInt(holder.seekbar, "progress",
                                        holder.seekbar.getProgress(), (int)
                                                ((1 - ((double) (remainingSeconds) /  goal.getGoalSeconds()))
                                                        * 100 * 100));
                                animation.setDuration(1000);
                                animation.setAutoCancel(true);
                                animation.setInterpolator(new LinearInterpolator());

                                goal.setSeekbarAnimation(animation);

                                animation.start();

                        }
                    }
                }
                if (isToday) {
                    // wow... trying to change the time with the seekbar?  That sounds dangerous

                    holder.startStopTimer.setVisibility(View.VISIBLE);

                }// end start/stop
                else {
                    holder.startStopTimer.setVisibility(View.GONE);
                }
            }//if istoday
            holder.mView.setOnClickListener(onClickListener);

            // Hide the pencil if not today... for now
            if (isToday) {
                if (holder.pencil != null) {
                    holder.pencil.setVisibility(View.VISIBLE);
                }

            }//end istoday
            else {
                if (holder.pencil != null) {
                    holder.pencil.setVisibility(View.GONE);
                    holder.editButtons.setVisibility(View.GONE);
                }
            }


            //and now, the goal Checkbox

            if (isToday) {
                if (holder.goalCheckBox != null) {
                    holder.goalCheckBox.setEnabled(true);
                }
                //Goal Checkbox Logic
                if (holder.goalCheckBox != null && !justARefresh) {
                    holder.goalCheckBox.setChecked(goal.getGoalEntry().getHasSucceeded());
                    if (goal.getGoalEntry().getHasSucceeded()) {
                        if (!holder.soccerBallImage.isShown()) {
                            goal.getGoalEntry().setHasMoved(false);
                            layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
                                @Override
                                public void onGlobalLayout() {
                                    if (!goal.getGoalEntry().isHasMoved()) {
                                        int[] ballLocation = new int[2];
                                        holder.soccerBallImage.getLocationOnScreen(ballLocation);
                                        int[] checkboxLocation = new int[2];
                                        holder.goalCheckBox.getLocationOnScreen(checkboxLocation);

                                        float distance = Math.abs(ballLocation[0] - checkboxLocation[0] +
                                                holder.goalCheckBox.getMeasuredWidth());
                                        holder.soccerBallImage.animate().translationX(distance)
                                                .setDuration(1000)
                                                .setInterpolator(new AccelerateDecelerateInterpolator())
                                                .setListener(new spinningBallAnimListener(holder.spinningBallAnim));
                                        holder.goalCheckBox.getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener);
                                        goal.getGoalEntry().setHasMoved(true);
                                    }
                                }
                            };
                            holder.goalCheckBox.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
                        }
                    }
                }
            } else {
                if (holder.goalCheckBox != null) {//end if istoday
                    holder.goalCheckBox.setEnabled(false);
                }
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

            }

            //Let's do the delete button

            if (holder.settingsButton != null) {
                if (goal != null) {
                    holder.settingsButton.setContentDescription(context.getString(
                            R.string.settings).concat(" ").concat(
                            goal.getName())
                    );
                }

            }

        } //end if itemviewcount

        new GetSuccessfulGoalCount(context).execute(goalEntryGoalCounter);
    }//end BindViewHolder


    public void turnOnGoal(Goal goal, Context context,
                            ObjectAnimator spinningBallAnim) {


        //todo is this needed?
//        TimeGoalieAlarmManager.startTimer(newtime, goal,
//                context);
        spinningBallAnim.start();
        //Start the Goal!!
        BaseApplication.getGoalEntryController().startEngine(goalArrayList);
        BaseApplication.getGoalEntryController().startGoal(goal.getGoalEntry(), goal);

    }

    public void turnOffGoal(Goal goal, Context context, ObjectAnimator spinningBallAnim) {

        //cancel killgoal intent

        Intent killGoalIntent = TimeGoalieAlarmReceiver
                .createKillGoalTimeGoalieAlarmIntent(context,
                        "Stopping goal due to inactivity", (int) goal.getGoalId());

        PendingIntent killGoalPi = TimeGoalieAlarmReceiver
                .createKillGoalSafetyPendingIntent(context,
                        killGoalIntent, (int) goal.getGoalId());

        TimeGoalieAlarmManager.cancelTimeGoalAlarm(context, killGoalPi);

        //                   TimeGoalieAlarmReceiver.cancelSecondlyAlarm(context, goal);
        //timeGoalieAlarmObject.setRunning(false);
        BaseApplication.getGoalEntryController().stopGoal(goal.getGoalEntry(), goal);

        spinningBallAnim.cancel();
    }

    @Override
    public int getItemViewType(int position) {
        return (int) goalArrayList.get(position).getGoalTypeId();
    }

    /* This will actually link the static list of Alarms and Countdowntimers in the baseapplication
       to the textview
    */

    @Override
    public int getItemCount() {
        if (goalArrayList != null) {
            return goalArrayList.size();
        } else {
            return 0;
        }
    }

    public interface GoalCounter {
        void updateGoalCounter(int add);
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
        private ImageView soccerBallImage;
        private ToggleButton yes_no_pencil;
        private ImageButton settingsButton;
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
            settingsButton = view.findViewById(R.id.settings_gear_button);

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

            if (settingsButton != null) {
                settingsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //not deleting anymore
//                        Animation fadeOut = new AlphaAnimation(1, 0);
//                        fadeOut.setInterpolator(new AccelerateInterpolator());
//                        fadeOut.setStartOffset(500);
//                        fadeOut.setDuration(500);
//                        cardView.setAnimation(fadeOut);
//                        Goal goal = goalArrayList.get(getLayoutPosition());
//                        goalArrayList.remove(getLayoutPosition());
//                        justARefresh = true;
//                        notifyItemRangeChanged(getLayoutPosition(), getItemCount());
//                        notifyItemRemoved(getLayoutPosition());
                        Goal goal = goalArrayList.get(getLayoutPosition());
                        if (goal != null) {
                            Intent intent = new Intent(context, EditGoalActivity.class);
                            intent.putExtra("goal-name", goal.getName());
                            intent.putExtra("goal-type", goal.getGoalTypeId());
                            intent.putExtra("goal-days", TimeGoalieUtils.getCommaSeparatedList(goal, ""));
                            intent.putExtra("goal-id", goal.getGoalId());
                            intent.putExtra("goal-minutes", goal.getMinutes());
                            intent.putExtra("goal-seconds", goal.getGoalSeconds());
                            intent.putExtra("goal-hours", goal.getHours());
                            intent.putExtra("goal-isDaily", goal.getIsDaily());
                            intent.putExtra("goal-isDisabled", goal.getIsDisabled());
                            intent.putExtra("goal-isWeekly", goal.getIsWeekly());
                            context.startActivity(intent);
                        }

                    }
                });
            }

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

                        if (settingsButton.getVisibility() != View.VISIBLE) {
                            settingsButton.setVisibility(View.VISIBLE);
                        } else {
                            settingsButton.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }

            if (yes_no_pencil != null) {
                yes_no_pencil.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (settingsButton.getVisibility() != View.VISIBLE) {
                            settingsButton.setVisibility(View.VISIBLE);
                        } else {
                            settingsButton.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }

            if (seekbar != null) {
                //   if (seekbar != null) {
                //   seekbar.setOnSeekBarChangeListener(null);
//                    seekbar.setOnTouchListener(new View.OnTouchListener() {
//                        @Override
//                        public boolean onTouch(View view, MotionEvent motionEvent) {
//                            return true;
//                        }
//                    });

                // holder.seekbar.setOnTouchListener(null);

                seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                        if (getLayoutPosition() > -1 && !(getLayoutPosition() > goalArrayList.size()-1)) {
                            Goal goal = goalArrayList.get(getLayoutPosition());
                            if (fromUser) {
                                float percentage = (float) i / 10000;
                                int secondsElapsed = (int) (goal.getGoalSeconds() * percentage);
                                goal.getGoalEntry().setSecondsElapsed(secondsElapsed);
                                TimeGoalieUtils.setTimeTextLabel(goal, time_tv, tv_timeOutOf);
                            }
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        Goal goal = goalArrayList.get(getLayoutPosition());
                        goal.setChangingSeekbar(true);
                        if (goal.getSeekbarAnimation() != null) {
                            goal.getSeekbarAnimation().cancel();

                        }
                        //can't click seekbar when animation is going
                        if (goal.getGoalEntry().isRunning()) {
                            spinningBallAnim.cancel();


                            goal.getGoalEntry().setHasFinished(false);
                            if (goal.getGoalTypeId() == 1) { //if this is a time limit goal
                                goal.getGoalEntry().setHasSucceeded(true);
                            }
                            if (goal.getGoalTypeId() == 0) { //if this is a time limit goal
                                goal.getGoalEntry().setHasSucceeded(false);
                            }

                            turnOffGoal(goal, context, spinningBallAnim);

                        }

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        Goal goal = goalArrayList.get(getLayoutPosition());
                        goal.setChangingSeekbar(false);
                        //can't click seekbar when animation is going
                        if (startStopTimer.isChecked()) {
                            turnOnGoal(goal, context, spinningBallAnim);
                        } else {
                            BaseApplication.getGoalEntryController().updateGoal(context,goal.getGoalEntry());
                        }
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

                //Add subtract/add button click listeners

                int[] incrementValues = smallAdd.getContext().getResources()
                        .getIntArray(R.array.incrementArray);
                // edit buttons
                Button[] addButtons = new Button[]{smallAdd, mediumAdd, largeAdd};
                Button[] subtractButtons = new Button[]{smallSubtract, mediumSubtract,
                        largeSubtract};

                for (int i = 0; i < incrementValues.length; i++) {
                    final int value = incrementValues[i];

                    addButtons[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Goal goal = goalArrayList.get(getLayoutPosition());


                            //old goal.setMinutes(goal.getMinutes() + value);
                            goal.getGoalEntry().setGoalAugment(
                                    goal.getGoalEntry().getGoalAugment() + value * 60);

                            TimeGoalieAlarmReceiver.cancelSecondlyAlarm(context, goal);

                            //timeGoalieAlarmObject.setTargetTime(0);
                            goal.getGoalEntry().setTargetTime(0);
                            new InsertNewGoalEntry(context).execute(goal.getGoalEntry());
                            justARefresh = true;
                            notifyDataSetChanged();


                        }
                    });

                    subtractButtons[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Goal goal = goalArrayList.get(getLayoutPosition());

                            if (goal.getGoalSeconds() / 60 > value) {
                                goal.getGoalEntry().setGoalAugment(
                                        goal.getGoalEntry().getGoalAugment() - value * 60);
                            }
                            TimeGoalieAlarmReceiver.cancelSecondlyAlarm(context, goal);
                            //timeGoalieAlarmObject.setTargetTime(0);
                            goal.getGoalEntry().setTargetTime(0);
                            new InsertNewGoalEntry(context).execute(goal.getGoalEntry());
                            justARefresh = true;
                            notifyDataSetChanged();
                        }
                    });
                } //end add edit buttons
            }//end seekbar

            if (startStopTimer != null) {
                startStopTimer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        Goal goal = goalArrayList.get(getLayoutPosition());
                        Log.i("mindbuilders3", goal.getName() + " tick " +
                                goal.getGoalEntry().getSecondsElapsed());


                        if (b && goal.getGoalEntry().getDate()
                                .equals(TimeGoalieDateUtils.getSqlDateString())) {
                            turnOnGoal(goal, context, spinningBallAnim);

                        } else {
                            turnOffGoal(goal, context, spinningBallAnim);
                        }
                    }
                });
            }
            if (goalCheckBox != null) {

                spinningBallAnim = ObjectAnimator.ofFloat(soccerBallImage, "rotation", 0f, 70f);
                spinningBallAnim.setInterpolator(new LinearInterpolator());
                spinningBallAnim.setDuration(1000);
                spinningBallAnim.setRepeatCount(ValueAnimator.INFINITE);

                goalCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        Goal goal = goalArrayList.get(getLayoutPosition());
                        if (b) {
                            if (goal.getGoalEntry() != null) {
                                goal.getGoalEntry().setHasSucceeded(1);
                                goal.getGoalEntry().setHasFinished(true);
                                new InsertNewGoalEntry(compoundButton.getContext())
                                        .execute(goal.getGoalEntry());
                                new InsertNewGoalEntry(context).execute(goal.getGoalEntry());


                                //get the distance properly
                                int[] ballLocation = new int[2];
                                soccerBallImage.getLocationOnScreen(ballLocation);
                                int[] checkboxLocation = new int[2];
                                goalCheckBox.getLocationOnScreen(checkboxLocation);

                                float distance = Math.abs(ballLocation[0] - checkboxLocation[0] +
                                        goalCheckBox.getMeasuredWidth());
                                soccerBallImage.animate().translationX(distance)
                                        .setDuration(1000)
                                        .setInterpolator(new AccelerateDecelerateInterpolator())
                                        .setListener(new spinningBallAnimListener(spinningBallAnim));
                            }
                        } else {

                            if (goal.getGoalEntry() != null) {
                                goal.getGoalEntry().setHasSucceeded(0);
                                goal.getGoalEntry().setHasFinished(true);
                                new InsertNewGoalEntry(compoundButton.getContext())
                                        .execute(goal.getGoalEntry());
                                new InsertNewGoalEntry(context).execute(goal.getGoalEntry());

                                soccerBallImage.animate().translationX(0)
                                        .setDuration(1000)
                                        .setInterpolator(new AccelerateDecelerateInterpolator())
                                        .setListener(new spinningBallAnimListener(spinningBallAnim));
                            }

                        }
                        new GetSuccessfulGoalCount(context).execute(goalEntryGoalCounter);
                    }
                });


            }


        }


        @Override
        public String toString() {
            return super.toString() + " '" + tv_goaltitle.getText() + "'";
        }
    }

    private class spinningBallAnimListener implements Animator.AnimatorListener {

        ObjectAnimator spinningBallAnim;

        spinningBallAnimListener(ObjectAnimator spinningBallAnim) {
            this.spinningBallAnim = spinningBallAnim;

        }

        @Override
        public void onAnimationStart(Animator animator) {
            spinningBallAnim.start();
        }

        @Override
        public void onAnimationEnd(Animator animator) {

            spinningBallAnim.cancel();

        }

        @Override
        public void onAnimationCancel(Animator animator) {
            spinningBallAnim.cancel();
        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    }
}

