package us.mindbuilders.petemit.timegoalie;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ContentValues;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;

import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;
import us.mindbuilders.petemit.timegoalie.data.TimeGoalieContentProvider;
import us.mindbuilders.petemit.timegoalie.data.TimeGoalieContract;

/**
 * Created by Peter on 9/19/2017.
 */

public class NewGoalFragment extends Fragment {
    private Spinner goalTypeSpinner;
    private EditText newGoalEditText;
    private NumberPicker npHour;
    private NumberPicker npMinute;
    private Button createButton;
    private CheckBox dailyCb;
    private CheckBox weeklyCb;
    private LinearLayout weeklyCheckboxLinearLayout;
    private LinearLayout timeGoalPickersLinearLayout;

    public NewGoalFragment(){

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public static Fragment getInstance(){
        return new NewGoalFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_new_goal,container,false);

        //Set up the Spinner
        goalTypeSpinner=(Spinner)view.findViewById(R.id.goal_type_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.goal_type_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        goalTypeSpinner.setAdapter(spinnerAdapter);

        //set up the numberpicker

        npHour=view.findViewById(R.id.np_hour);
        npMinute=view.findViewById(R.id.np_minute);

        createButton=view.findViewById(R.id.button_create_new_goal);
        newGoalEditText=view.findViewById(R.id.et_new_goal);
        dailyCb=view.findViewById(R.id.daily_checkbox);
        weeklyCb=view.findViewById(R.id.weekly_checkbox);
        weeklyCheckboxLinearLayout=view.findViewById(R.id.weekly_checkbox_list_ll);
        timeGoalPickersLinearLayout=view.findViewById(R.id.ll_time_goal_pickers);

        goalTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // TODO: 9/25/2017 do this better somehow
                if (((TextView)(view)).getText().toString().toLowerCase().contains("yes")){
                    hideTimeGoalPickers();
                }
                else{
                    showTimeGoalPickers();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //set up checkbox logic
        dailyCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    weeklyCb.setEnabled(false);
                }
                else {
                    weeklyCb.setEnabled(true);
                }
            }
        });

        weeklyCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    dailyCb.setEnabled(false);
                    showWeeklyCheckboxes();
                }
                else{
                    dailyCb.setEnabled(true);
                    hideWeeklyCheckboxes();
                }
            }
        });

//        npHour.setDisplayedValues(getResources().getStringArray(R.array.hour_array));
//        npMinute.setDisplayedValues(getResources().getStringArray(R.array.minute_array));

        npHour.setMinValue(0);
        npHour.setMaxValue(24);
        npMinute.setMinValue(0);
        npMinute.setMaxValue(60);

        //create Button insert logic

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String goalname=newGoalEditText.getText().toString();
                //Do not create goal if Goalname is empty
                if (goalname.equalsIgnoreCase("")){
                    Toast.makeText(getContext(),
                            R.string.no_goal_name_err_msg, Toast.LENGTH_SHORT).show();
                    return;
                }


                Goal goal = new Goal();
                goal.setName((newGoalEditText).getText().toString());
                goal.setGoalTypeId(goalTypeSpinner.getSelectedItemId());
                new insertNewGoal().execute(goal);
                getContext().startActivity(new Intent(getContext(),GoalListActivity.class));
            }
        });


        return view;
    }

    private void hideTimeGoalPickers(){
        timeGoalPickersLinearLayout.setVisibility(View.GONE);
    }

    private void showTimeGoalPickers(){
        timeGoalPickersLinearLayout.setVisibility(View.VISIBLE);
    }

    private void hideWeeklyCheckboxes(){
        weeklyCheckboxLinearLayout.setVisibility(View.GONE);
    }

    private void showWeeklyCheckboxes(){
        weeklyCheckboxLinearLayout.setVisibility(View.VISIBLE);
    }

    public class insertNewGoal extends AsyncTask<Goal, Void, Void> {

        @Override
        protected Void doInBackground(Goal... goals) {
            if (goals.length == 1) {
                Goal goal = goals[0];
                ContentValues cv = new ContentValues();
                Date date = goal.getIsTodayOnly();

                cv.put(TimeGoalieContract.Goals.GOALS_COLUMN_NAME, goal.getName());
                cv.put(TimeGoalieContract.Goals.GOALS_COLUMN_GOALTYPEID, goal.getGoalTypeId());
                cv.put(TimeGoalieContract.Goals.GOALS_COLUMN_ISDAILY,goal.getIsDaily());
                if (date != null) {
                    cv.put(TimeGoalieContract.Goals.GOALS_COLUMN_ISTODAYONLY,date.toString());
                }
                cv.put(TimeGoalieContract.Goals.GOALS_COLUMN_ISWEEKLY,goal.getIsWeekly());
                cv.put(TimeGoalieContract.Goals.GOALS_COLUMN_TIMEGOALHOURS,goal.getHours());
                cv.put(TimeGoalieContract.Goals.GOALS_COLUMN_TIMEGOALMINUTES,goal.getMinutes());


                getContext().getContentResolver().insert(TimeGoalieContract.Goals.CONTENT_URI, cv);
            }
            return null;
        }
    }
}
