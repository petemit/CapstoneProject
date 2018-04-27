package us.mindbuilders.petemit.timegoalie;

import android.content.Intent;
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

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Day;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;
import us.mindbuilders.petemit.timegoalie.data.InsertNewGoal;
import us.mindbuilders.petemit.timegoalie.data.UpdateGoal;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieDateUtils;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieUtils;

/**
 * Created by Peter on 9/19/2017.
 */

public class EditGoalFragment extends Fragment {
    private Spinner goalTypeSpinner;
    private EditText newGoalEditText;
    private NumberPicker npHour;
    private NumberPicker npMinute;
    private Button updateButton;
    private Button deleteButton;
    private Button cancelButton;
    private CheckBox dailyCb;
    private CheckBox weeklyCb;
    private LinearLayout weeklyCheckboxLinearLayout;
    private LinearLayout timeGoalPickersLinearLayout;
    private ArrayList<Day> selectedDays = new ArrayList<Day>();
    private TextView timeGoalLabel;
    private CheckBox monCb;
    private CheckBox tueCb;
    private CheckBox wedCb;
    private CheckBox thuCb;
    private CheckBox friCb;
    private CheckBox satCb;
    private CheckBox sunCb;
    private String[] day_array;
    private String[] day_array_values;
    private GoalGetter goalGetter;
    private Goal goal;

    private FirebaseAnalytics firebaseAnalytics;

    public interface GoalGetter {
        Goal getGoal();
    }

    public EditGoalFragment() {

    }

    public static Fragment getInstance() {
        return new EditGoalFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        goalGetter = (EditGoalActivity)getActivity();
        goal = goalGetter.getGoal();
        View view = inflater.inflate(R.layout.fragment_edit_goal, container, false);

        //firebase analytic
        firebaseAnalytics = FirebaseAnalytics.getInstance(this.getContext());

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

        updateButton = view.findViewById(R.id.button_update_goal);
        deleteButton = view.findViewById(R.id.button_delete_goal);
        cancelButton = view.findViewById(R.id.button_cancel_goal);
        newGoalEditText = view.findViewById(R.id.et_new_goal);
        dailyCb = view.findViewById(R.id.daily_checkbox);
        weeklyCb = view.findViewById(R.id.weekly_checkbox);
        weeklyCheckboxLinearLayout = view.findViewById(R.id.weekly_checkbox_list_ll);
        timeGoalPickersLinearLayout = view.findViewById(R.id.ll_time_goal_pickers);

        timeGoalLabel = view.findViewById(R.id.time_goal_label);

        day_array = getResources().getStringArray(R.array.days_of_the_week);
        day_array_values = getResources().getStringArray(R.array.days_of_the_week_values);

        //get the weekly checkboxes
        monCb = view.findViewById(R.id.checkbox_mon);
        //    monCb.setText(day_array[0]);
        monCb.setTag(day_array_values[0]);
        tueCb = view.findViewById(R.id.checkbox_tue);
        // monCb.setText(day_array[1]);
        tueCb.setTag(day_array_values[1]);
        wedCb = view.findViewById(R.id.checkbox_wed);
        //  monCb.setText(day_array[2]);
        wedCb.setTag(day_array_values[2]);
        thuCb = view.findViewById(R.id.checkbox_thu);
        //  monCb.setText(day_array[3]);
        thuCb.setTag(day_array_values[3]);
        friCb = view.findViewById(R.id.checkbox_fri);
        //  monCb.setText(day_array[4]);
        friCb.setTag(day_array_values[4]);
        satCb = view.findViewById(R.id.checkbox_sat);
        //  monCb.setText(day_array[5]);
        satCb.setTag(day_array_values[5]);
        sunCb = view.findViewById(R.id.checkbox_sun);
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

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String goalname = newGoalEditText.getText().toString().replaceAll("[+.^<>,]",
                        "");
                //Do not create goal if Goalname is empty
                if (goalname.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(),
                            R.string.no_goal_name_err_msg, Toast.LENGTH_SHORT).show();
                    return;
                }


                goal.setName(goalname);
                goal.setGoalTypeId(goalTypeSpinner.getSelectedItemId());

                //if you AREN'T A YES NO GOAL
                if (goal.getGoalTypeId() != 2) {
                    if (npHour.getValue() == 0 && npMinute.getValue() == 0) {
                        Toast.makeText(getContext(),
                                R.string.add_time_to_goal_msg, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                goal.setHours(npHour.getValue());
                goal.setMinutes(npMinute.getValue());


                goal.setCreationDate(TimeGoalieDateUtils.getSqlDateString());

                //really not my favorite solution... want to move on to other things, though
                if (monCb.getVisibility() == View.VISIBLE && monCb.isChecked()) {
                    Day day = new Day();
                    day.setSequence(Integer.parseInt((String) monCb.getTag()));
                    day.setName(day_array[0]);
                    selectedDays.add(day);
                }
                if (tueCb.getVisibility() == View.VISIBLE && tueCb.isChecked()) {
                    Day day = new Day();
                    day.setSequence(Integer.parseInt((String) tueCb.getTag()));
                    day.setName(day_array[1]);
                    selectedDays.add(day);
                }
                if (wedCb.getVisibility() == View.VISIBLE && wedCb.isChecked()) {
                    Day day = new Day();
                    day.setSequence(Integer.parseInt((String) wedCb.getTag()));
                    day.setName(day_array[2]);
                    selectedDays.add(day);
                }
                if (thuCb.getVisibility() == View.VISIBLE && thuCb.isChecked()) {
                    Day day = new Day();
                    day.setSequence(Integer.parseInt((String) thuCb.getTag()));
                    day.setName(day_array[3]);
                    selectedDays.add(day);
                }
                if (friCb.getVisibility() == View.VISIBLE && friCb.isChecked()) {
                    Day day = new Day();
                    day.setSequence(Integer.parseInt((String) friCb.getTag()));
                    day.setName(day_array[4]);
                    selectedDays.add(day);
                }
                if (satCb.getVisibility() == View.VISIBLE && satCb.isChecked()) {
                    Day day = new Day();
                    day.setSequence(Integer.parseInt((String) satCb.getTag()));
                    day.setName(day_array[5]);
                    selectedDays.add(day);
                }
                if (sunCb.getVisibility() == View.VISIBLE && sunCb.isChecked()) {
                    Day day = new Day();
                    day.setSequence(Integer.parseInt((String) sunCb.getTag()));
                    day.setName(day_array[6]);
                    selectedDays.add(day);
                }
                goal.setGoalDays(selectedDays);

                if (dailyCb.isEnabled() && dailyCb.isChecked()) {
                    goal.setIsDaily(1);
                } else {
                    goal.setIsDaily(0);
                }

                if (weeklyCb.isEnabled() && weeklyCb.isChecked()) {
                    goal.setIsWeekly(1);
                } else {
                    goal.setIsWeekly(0);
                }

                if (firebaseAnalytics != null) {
                    Bundle firebaseBundle = new Bundle();
                    firebaseBundle.putString(FirebaseAnalytics.Param.ITEM_ID,
                            String.valueOf(goal.getGoalTypeId()));
                    firebaseBundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "new_goal");
                    firebaseBundle.putString(FirebaseAnalytics.Param.ITEM_NAME, goal.getName());
                    String goaldaysCommaSeparated = "";
                    goaldaysCommaSeparated = TimeGoalieUtils.getCommaSeparatedList(goal, goaldaysCommaSeparated);
                    firebaseBundle.putString(getString(R.string.goal_days_string), goaldaysCommaSeparated);
                    firebaseBundle.putString(getString(R.string.goal_length),
                            String.valueOf(goal.getGoalSeconds()));
                    firebaseBundle.putString(getString(R.string.is_daily_goal_string)
                            , String.valueOf(goal.getIsDaily()));
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT,
                            firebaseBundle);
                }

                new UpdateGoal(getContext()).execute(goal);
                String message = goalname.concat(" ")
                        .concat(getString(R.string.created));
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                getContext().startActivity(new Intent(getContext(), GoalListActivity.class));
            }
        });

        //Finally, populate the views
        newGoalEditText.setText(goal.getName());
        goalTypeSpinner.setSelection((int)goal.getGoalTypeId());
        dailyCb.setEnabled(goal.getIsDaily()==1);
        weeklyCb.setEnabled(goal.getIsWeekly()==1);
        npHour.setValue(goal.getHours());
        npMinute.setValue(goal.getMinutes());


        if (monCb.getVisibility() == View.VISIBLE) {
            Day day = new Day();
            day.setSequence(Integer.parseInt((String) monCb.getTag()));
            day.setName(day_array[0]);
            if(findDayInList(day.getName(),goal.getGoalDays())) {
                monCb.setChecked(true);
            }
        }
        if (tueCb.getVisibility() == View.VISIBLE ) {
            Day day = new Day();
            day.setSequence(Integer.parseInt((String) tueCb.getTag()));
            day.setName(day_array[1]);
            if(findDayInList(day.getName(),goal.getGoalDays())) {
                tueCb.setChecked(true);
            }
        }
        if (wedCb.getVisibility() == View.VISIBLE ) {
            Day day = new Day();
            day.setSequence(Integer.parseInt((String) wedCb.getTag()));
            day.setName(day_array[2]);
            if(findDayInList(day.getName(),goal.getGoalDays())) {
                wedCb.setChecked(true);
            }
        }
        if (thuCb.getVisibility() == View.VISIBLE) {
            Day day = new Day();
            day.setSequence(Integer.parseInt((String) thuCb.getTag()));
            day.setName(day_array[3]);
            if(findDayInList(day.getName(),goal.getGoalDays())) {
                thuCb.setChecked(true);
            }
        }
        if (friCb.getVisibility() == View.VISIBLE) {
            Day day = new Day();
            day.setSequence(Integer.parseInt((String) friCb.getTag()));
            day.setName(day_array[4]);
            if(findDayInList(day.getName(),goal.getGoalDays())) {
                friCb.setChecked(true);
            }
        }
        if (satCb.getVisibility() == View.VISIBLE) {
            Day day = new Day();
            day.setSequence(Integer.parseInt((String) satCb.getTag()));
            day.setName(day_array[5]);
            if(findDayInList(day.getName(),goal.getGoalDays())) {
                satCb.setChecked(true);
            }
        }
        if (sunCb.getVisibility() == View.VISIBLE) {
            Day day = new Day();
            day.setSequence(Integer.parseInt((String) sunCb.getTag()));
            day.setName(day_array[6]);
            if(findDayInList(day.getName(),goal.getGoalDays())) {
                sunCb.setChecked(true);
            }
        }




        return view;
    }

    private boolean findDayInList(String string, ArrayList<Day> days) {
        for (Day day : days) {
            if (day.getName().equals(string)) {
                return true;
            }
        }
        return false;
    }

    private void hideTimeGoalPickers() {
        timeGoalPickersLinearLayout.setVisibility(View.GONE);
        timeGoalLabel.setVisibility(View.GONE);
    }

    private void showTimeGoalPickers() {
        timeGoalPickersLinearLayout.setVisibility(View.VISIBLE);
        timeGoalLabel.setVisibility(View.VISIBLE);
    }


    private void hideWeeklyCheckboxes() {
        weeklyCheckboxLinearLayout.setVisibility(View.GONE);
    }

    private void showWeeklyCheckboxes() {
        weeklyCheckboxLinearLayout.setVisibility(View.VISIBLE);
    }

}
