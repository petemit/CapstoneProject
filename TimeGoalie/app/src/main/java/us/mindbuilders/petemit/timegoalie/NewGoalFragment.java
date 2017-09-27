package us.mindbuilders.petemit.timegoalie;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Day;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;
import us.mindbuilders.petemit.timegoalie.data.TimeGoalieContract;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieDateUtils;

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
    private ArrayList<Day> selectedDays=new ArrayList<Day>();
    private CheckBox monCb;
    private CheckBox tueCb;
    private CheckBox wedCb;
    private CheckBox thuCb;
    private CheckBox friCb;
    private CheckBox satCb;
    private CheckBox sunCb;
    private String[] day_array;
    private String[] day_array_values;


    public NewGoalFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public static Fragment getInstance() {
        return new NewGoalFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_goal, container, false);

        //Set up the Spinner
        goalTypeSpinner = (Spinner) view.findViewById(R.id.goal_type_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.goal_type_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        goalTypeSpinner.setAdapter(spinnerAdapter);

        //set up the numberpicker

        npHour = view.findViewById(R.id.np_hour);
        npMinute = view.findViewById(R.id.np_minute);
      //  newGoalEditText.getText().app

        createButton = view.findViewById(R.id.button_create_new_goal);
        newGoalEditText = view.findViewById(R.id.et_new_goal);
        dailyCb = view.findViewById(R.id.daily_checkbox);
        weeklyCb = view.findViewById(R.id.weekly_checkbox);
        weeklyCheckboxLinearLayout = view.findViewById(R.id.weekly_checkbox_list_ll);
        timeGoalPickersLinearLayout = view.findViewById(R.id.ll_time_goal_pickers);

        day_array = getResources().getStringArray(R.array.days_of_the_week);
        day_array_values = getResources().getStringArray(R.array.days_of_the_week_values);

        //get the weekly checkboxes
        monCb=view.findViewById(R.id.checkbox_mon);
    //    monCb.setText(day_array[0]);
        monCb.setTag(day_array_values[0]);
        tueCb=view.findViewById(R.id.checkbox_tue);
       // monCb.setText(day_array[1]);
        tueCb.setTag(day_array_values[1]);
        wedCb=view.findViewById(R.id.checkbox_wed);
      //  monCb.setText(day_array[2]);
        wedCb.setTag(day_array_values[2]);
        thuCb=view.findViewById(R.id.checkbox_thu);
      //  monCb.setText(day_array[3]);
        thuCb.setTag(day_array_values[3]);
        friCb=view.findViewById(R.id.checkbox_fri);
      //  monCb.setText(day_array[4]);
        friCb.setTag(day_array_values[4]);
        satCb=view.findViewById(R.id.checkbox_sat);
      //  monCb.setText(day_array[5]);
        satCb.setTag(day_array_values[5]);
        sunCb=view.findViewById(R.id.checkbox_sun);
       // monCb.setText(day_array[6]);
        sunCb.setTag(day_array_values[6]);

        goalTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // TODO: 9/25/2017 do this better somehow
                if (((TextView) (view)).getText().toString().toLowerCase().contains("yes")) {
                    hideTimeGoalPickers();
                } else {
                    showTimeGoalPickers();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                hideTimeGoalPickers();
            }
        });


        //set up checkbox logic
        dailyCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    weeklyCb.setEnabled(false);
                } else {
                    weeklyCb.setEnabled(true);
                }
            }
        });

        weeklyCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    dailyCb.setEnabled(false);
                    showWeeklyCheckboxes();
                } else {
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
                String goalname = newGoalEditText.getText().toString();
                //Do not create goal if Goalname is empty
                if (goalname.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(),
                            R.string.no_goal_name_err_msg, Toast.LENGTH_SHORT).show();
                    return;
                }


                Goal goal = new Goal();
                goal.setName((newGoalEditText).getText().toString());
                goal.setGoalTypeId(goalTypeSpinner.getSelectedItemId());
                goal.setHours(npHour.getValue());
                goal.setMinutes(npMinute.getValue());

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String date = df.format(new java.util.Date());
                goal.setCreationDate(date);


                //really not my favorite solution... want to move on to other things, though
                if(monCb.getVisibility() == View.VISIBLE && monCb.isChecked()){
                    Day day = new Day();
                    day.setSequence(Integer.parseInt((String)monCb.getTag()));
                    day.setName(day_array[0]);
                    selectedDays.add(day);
                }
                if(tueCb.getVisibility() == View.VISIBLE && tueCb.isChecked()){
                    Day day = new Day();
                    day.setSequence(Integer.parseInt((String)tueCb.getTag()));
                    day.setName(day_array[1]);
                    selectedDays.add(day);
                }
                if(wedCb.getVisibility() == View.VISIBLE && wedCb.isChecked()){
                    Day day = new Day();
                    day.setSequence(Integer.parseInt((String)wedCb.getTag()));
                    day.setName(day_array[2]);
                    selectedDays.add(day);
                }
                if(thuCb.getVisibility() == View.VISIBLE && thuCb.isChecked()){
                    Day day = new Day();
                    day.setSequence(Integer.parseInt((String)thuCb.getTag()));
                    day.setName(day_array[3]);
                    selectedDays.add(day);
                }
                if(friCb.getVisibility() == View.VISIBLE && friCb.isChecked()){
                    Day day = new Day();
                    day.setSequence(Integer.parseInt((String)friCb.getTag()));
                    day.setName(day_array[4]);
                    selectedDays.add(day);
                }
                if(satCb.getVisibility() == View.VISIBLE && satCb.isChecked()){
                    Day day = new Day();
                    day.setSequence(Integer.parseInt((String)satCb.getTag()));
                    day.setName(day_array[5]);
                    selectedDays.add(day);
                }
                if(sunCb.getVisibility() == View.VISIBLE && sunCb.isChecked()){
                    Day day = new Day();
                    day.setSequence(Integer.parseInt((String)sunCb.getTag()));
                    day.setName(day_array[6]);
                    selectedDays.add(day);
                }
                goal.setGoalDays(selectedDays);

                if (dailyCb.isEnabled() && dailyCb.isChecked()) {
                    goal.setIsDaily(1);
                }

                if (weeklyCb.isEnabled() && weeklyCb.isChecked()) {
                    goal.setIsWeekly(1);
                }

                new insertNewGoal().execute(goal);
                getContext().startActivity(new Intent(getContext(), GoalListActivity.class));
            }
        });


        return view;
    }

    private void hideTimeGoalPickers() {
        timeGoalPickersLinearLayout.setVisibility(View.GONE);
    }

    private void showTimeGoalPickers() {
        timeGoalPickersLinearLayout.setVisibility(View.VISIBLE);
    }

    private void hideWeeklyCheckboxes() {
        weeklyCheckboxLinearLayout.setVisibility(View.GONE);
    }

    private void showWeeklyCheckboxes() {
        weeklyCheckboxLinearLayout.setVisibility(View.VISIBLE);
    }

    public class insertNewGoal extends AsyncTask<Goal, Void, Void> {

        @Override
        protected Void doInBackground(Goal... goals) {
            if (goals.length == 1) {
                Goal goal = goals[0];
                ContentValues goal_cv = new ContentValues();
                String date = goal.getCreationDate();

                goal_cv.put(TimeGoalieContract.Goals.GOALS_COLUMN_NAME, goal.getName());
                goal_cv.put(TimeGoalieContract.Goals.GOALS_COLUMN_GOALTYPEID, goal.getGoalTypeId());
                goal_cv.put(TimeGoalieContract.Goals.GOALS_COLUMN_ISDAILY, goal.getIsDaily());
                if (date != null) {
                    goal_cv.put(TimeGoalieContract.Goals.GOALS_COLUMN_CREATIONDATE, date);
                }
                goal_cv.put(TimeGoalieContract.Goals.GOALS_COLUMN_ISWEEKLY, goal.getIsWeekly());
                goal_cv.put(TimeGoalieContract.Goals.GOALS_COLUMN_TIMEGOALHOURS, goal.getHours());
                goal_cv.put(TimeGoalieContract.Goals.GOALS_COLUMN_TIMEGOALMINUTES, goal.getMinutes());

                long goal_id = ContentUris.parseId(getContext().getContentResolver()
                        .insert(TimeGoalieContract.Goals.CONTENT_URI, goal_cv));
                for (int i = 0; i < goal.getGoalDays().size(); i++) {
                    ContentValues goal_day_cv = new ContentValues();

                    goal_day_cv.put(TimeGoalieContract.GoalsDays.GOALS_DAYS_COLUMN_GOAL_ID, goal_id);
                    goal_day_cv.put(TimeGoalieContract.GoalsDays.GOALS_DAYS_COLUMN_DAY_ID,
                            goal.getGoalDays().get(i).getSequence());
                    getContext().getContentResolver().insert(TimeGoalieContract.GoalsDays.CONTENT_URI,
                            goal_day_cv);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
