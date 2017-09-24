package us.mindbuilders.petemit.timegoalie;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;
import us.mindbuilders.petemit.timegoalie.data.TimeGoalieContentProvider;
import us.mindbuilders.petemit.timegoalie.data.TimeGoalieContract;

/**
 * Created by Peter on 9/19/2017.
 */

public class NewGoalFragment extends Fragment {
    private Spinner newGoalSpinner;
    private EditText newGoalEditText;
    private NumberPicker npHour;
    private NumberPicker npMinute;
    private Button createButton;

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
        newGoalSpinner=(Spinner)view.findViewById(R.id.new_goal_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.goal_type_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newGoalSpinner.setAdapter(spinnerAdapter);

        //set up the numberpicker

        npHour=view.findViewById(R.id.np_hour);
        npMinute=view.findViewById(R.id.np_minute);

        createButton=view.findViewById(R.id.button_create_new_goal);

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

                new insertNewGoal().execute(Goal);
                getContext().startActivity(new Intent(getContext(),GoalListActivity.class));
            }
        });


        return view;
    }

    public class insertNewGoal extends AsyncTask<Goal, Void, Void> {

        @Override
        protected Void doInBackground(Goal... goals) {
            if (goals.length == 1) {
                Goal goal = goals[0];
                ContentValues cv = new ContentValues();
                cv.put(TimeGoalieContract.Goals.GOALS_COLUMN_NAME, goal.getName());
                cv.put(TimeGoalieContract.Goals.GOALS_COLUMN_GOALTYPEID, goal.getGoalTypeId());
                cv.put(TimeGoalieContract.Goals.GOALS_COLUMN_ISDAILY,goal.getIsDaily());
                cv.put(TimeGoalieContract.Goals.GOALS_COLUMN_ISTODAYONLY,goal.getIsTodayOnly().toString());
                cv.put(TimeGoalieContract.Goals.GOALS_COLUMN_ISWEEKLY,goal.getIsWeekly());
                cv.put(TimeGoalieContract.Goals.GOALS_COLUMN_TIMEGOALHOURS,goal.getHours());
                cv.put(TimeGoalieContract.Goals.GOALS_COLUMN_TIMEGOALMINUTES,goal.getMinutes());


                getContext().getContentResolver().insert(TimeGoalieContract.Goals.CONTENT_URI, cv);
            }
            return null;
        }
    }
}
