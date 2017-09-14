package us.mindbuilders.petemit.timegoalie;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

/**
 * Created by Peter on 9/19/2017.
 */

public class NewGoalFragment extends Fragment {
    private Spinner newGoalSpinner;
    private EditText newGoalEditText;
    private NumberPicker npHour;
    private NumberPicker npMinute;

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

//        npHour.setDisplayedValues(getResources().getStringArray(R.array.hour_array));
//        npMinute.setDisplayedValues(getResources().getStringArray(R.array.minute_array));

        npHour.setMinValue(0);
        npHour.setMaxValue(24);
        npMinute.setMinValue(0);
        npMinute.setMaxValue(60);




        return view;
    }
}
