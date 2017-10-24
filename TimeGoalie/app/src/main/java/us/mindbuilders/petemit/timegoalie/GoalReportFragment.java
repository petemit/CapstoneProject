package us.mindbuilders.petemit.timegoalie;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.GoalEntry;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.WeekInterval;
import us.mindbuilders.petemit.timegoalie.data.TimeGoalieContract;
import us.mindbuilders.petemit.timegoalie.dummy.DummyContent;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieDateUtils;

/**
 * The report fragment
 */
public class GoalReportFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int GOAL_REPORT_LOADER_ID = 44;
    private static final int GOAL_ENTRY_LOADER_ID = 44;
    private static final int MONTHLY_ALL_GOALS = 100;
    private static final int MONTHLY_ONE_GOAL = 101;
    private static final int WEEKLY_ALL_GOALS = 102;
    private static final int WEEKLY_ONE_GOAL = 103;

    private Spinner goalSpinner;
    private RadioGroup monthlyWeeklyRadioGroup;
    private RadioButton weeksRb;
    private RadioButton monthsRb;
    private ArrayList<GoalEntry> goalEntries;
    private ArrayList<Goal> goals;
    private LineChart chart;
    private String selectedScope;
    private ArrayAdapter spinnerAdapter;

    public enum scopeEnum {
        WEEKLY, MONTHLY
    }

    public enum goalSelectionEnum {
        ALLGOALS, SINGLEGOAL
    }

    private static final int DEFAULTWEEKS = 4;
    private static final int DEFAULTMONTHS = 4;
    private static final int DEFAULTMAX = 20;
    private int numOfWeeks;
    private int numOfMonths;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GoalReportFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = this.getActivity();
        numOfWeeks = DEFAULTWEEKS;
        numOfMonths = DEFAULTMONTHS;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.goal_report_fragment, container, false);

        // Show the dummy content as text in a TextView.
        chart = rootView.findViewById(R.id.report_chart);
        goalSpinner = rootView.findViewById(R.id.report_goal_spinner);
        weeksRb = rootView.findViewById(R.id.radio_button_weeks);
        monthsRb = rootView.findViewById(R.id.radio_button_months);
        monthlyWeeklyRadioGroup = rootView.findViewById(R.id.month_week_radio_group);

        final Bundle loaderBundle = new Bundle();

        //for testing
        //    bundle.putInt(getString(R.string.goal_scope), WEEKLY_ALL_GOALS);



        IAxisValueFormatter xAxisFormatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                return v + "";
            }
        };

        monthlyWeeklyRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                    if (i == R.id.radio_button_weeks) {
                        loaderBundle.putString(getString(R.string.goal_scope), scopeEnum.WEEKLY.name());
                    } else {
                        loaderBundle.putString(getString(R.string.goal_scope), scopeEnum.MONTHLY.name());
                    }
                }
        });

        if (savedInstanceState != null) {


        } else {
            //set the default to weeks selected
            weeksRb.setChecked(true);
            loaderBundle.putString(getString(R.string.goal_scope),scopeEnum.WEEKLY.name());
            loaderBundle.putString(getString(R.string.report_goal_selection_key),
                    goalSelectionEnum.ALLGOALS.name());
        }


        chart.getDescription().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(numOfWeeks);
        xAxis.setValueFormatter(xAxisFormatter);


        YAxis yAxis = chart.getAxisLeft();
        yAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        yAxis.setLabelCount(1);
        yAxis.setAxisMaximum(DEFAULTMAX);
        yAxis.setAxisMinimum(0f);
        yAxis.setDrawGridLines(true);

        chart.getAxisRight().setEnabled(false);


        getActivity().getSupportLoaderManager().initLoader(GOAL_REPORT_LOADER_ID, loaderBundle, this);
        getActivity().getSupportLoaderManager().initLoader(GOAL_ENTRY_LOADER_ID, loaderBundle, this);


        return rootView;
    }

    public ArrayList<WeekInterval> getWeekIntervals(int numOfWeeks) {
        ArrayList<WeekInterval> weekIntervals = new ArrayList<WeekInterval>();
        for (int i = 0; i < numOfWeeks; i++) {

            Calendar c = new GregorianCalendar().getInstance();
            c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
            c.add(Calendar.WEEK_OF_YEAR, -i);
            String beg = TimeGoalieDateUtils.getSqlDateString(c);
            //get last day of week
            c.add(Calendar.DAY_OF_WEEK, 6);
            String end = TimeGoalieDateUtils.getSqlDateString(c);
            WeekInterval week = new WeekInterval(beg, end);
            weekIntervals.add(week);
        }
        return weekIntervals;
    }

    public void initChart(ArrayList<GoalEntry> goalEntries) {

        ArrayList<WeekInterval> weekIntervals = getWeekIntervals(numOfWeeks);
        HashMap<String, ArrayList<String>> weekMap = new HashMap<String, ArrayList<String>>();
        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (WeekInterval interval : weekIntervals) {
            weekMap.put(interval.getBeginningOfWeek(), new ArrayList<String>());
        }

        if (goalEntries != null) {
            for (int i = 0; i < goalEntries.size(); i++) {
                GoalEntry goalEntry = goalEntries.get(i);
                if (!goalEntry.getHasSucceeded()) {
                    continue;
                }
                for (WeekInterval interval : weekIntervals) {
                    String goalDate = goalEntry.getDate();
                    Date goalTime = null;
                    Date begTime = null;
                    Date endTime = null;
                    try {
                        goalTime = TimeGoalieDateUtils.df.parse(goalDate);
                        begTime = TimeGoalieDateUtils.df.parse(interval.getBeginningOfWeek());
                        endTime = TimeGoalieDateUtils.df.parse(interval.getEndOfWeek());
                    } catch (ParseException p) {
                        Log.e("GoalReportFragment", p.toString());
                    }

                    if ((goalTime.compareTo(begTime)) > 0 && (goalTime.compareTo(endTime) < 0)) {
                        ArrayList<String> list = weekMap.get(interval.getBeginningOfWeek());
                        list.add(goalDate);
                    }
                }
            }
            for (int i = 0; i < weekIntervals.size(); i++) {
                Entry entry = new Entry(i, weekMap.get(weekIntervals.get(i).getBeginningOfWeek()).size());
                entries.add(entry);
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "Total Goals Saved");
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setColor(ColorTemplate.getHoloBlue());
        dataSet.setCircleColor(Color.WHITE);
        dataSet.setLineWidth(5f);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);


        //Next, make Entries out of all this mess
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if (id == GOAL_REPORT_LOADER_ID) {
            String scope = args.getString(getString(R.string.goal_scope));
            String goalSelection = args.getString(getString(R.string.report_goal_selection_key));
            int selectedQuery = 0;
            if (scope.equals(scopeEnum.WEEKLY.name())) {
                if (goalSelection.equals(goalSelectionEnum.ALLGOALS.name())) {
                    selectedQuery = WEEKLY_ALL_GOALS;
                }
                if (goalSelection.equals(goalSelectionEnum.SINGLEGOAL.name())) {
                    selectedQuery = WEEKLY_ONE_GOAL;
                    //put goal id here probs
                }
            }

            if (scope.equals(scopeEnum.MONTHLY.name())) {
                if (goalSelection.equals(goalSelectionEnum.ALLGOALS.name())) {
                    selectedQuery = MONTHLY_ALL_GOALS;
                }
                if (goalSelection.equals(goalSelectionEnum.SINGLEGOAL.name())) {
                    selectedQuery = MONTHLY_ONE_GOAL;
                    //put goal id here probs
                }
            }

            switch (selectedQuery) {
                case MONTHLY_ALL_GOALS:
                    break;
                case MONTHLY_ONE_GOAL:
                    break;
                case WEEKLY_ALL_GOALS:
                    CursorLoader cl = new CursorLoader(this.getContext(),
                            TimeGoalieContract.getWeekSuccessfulGoals(DEFAULTWEEKS),
                            null,
                            null,
                            null,
                            null
                    );
                    return cl;
            }
        }
        if (id == GOAL_ENTRY_LOADER_ID) {
            CursorLoader cl = new CursorLoader(this.getContext(),
                    TimeGoalieContract.Goals.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);

        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        if (loader.getId() == GOAL_REPORT_LOADER_ID) {
            goalEntries = GoalEntry.makeGoalEntryListFromCursor(cursor);
            initChart(goalEntries);
        }
        if (loader.getId() == GOAL_ENTRY_LOADER_ID) {
            goals = Goal.createGoalListFromCursor(cursor);

            ArrayList<String> list = new ArrayList<String>();
            if(goals != null) {
                for (Goal goal : goals) {
                    list.add(goal.getName());
                }
            }
            spinnerAdapter = new ArrayAdapter<String>(this.getContext(),
                    R.layout.spinner_text_layout, list);
            goalSpinner.setAdapter(spinnerAdapter);
        }



    }

    @Override
    public void onLoaderReset(Loader loader) {
        if (loader.getId() == GOAL_REPORT_LOADER_ID) {
            goalEntries = null;
        }
        if (loader.getId() == GOAL_ENTRY_LOADER_ID) {
            goals = null;
        }



    }
}
