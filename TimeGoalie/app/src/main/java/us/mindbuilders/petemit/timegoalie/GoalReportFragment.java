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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.GoalEntry;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.MonthInterval;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.WeekInterval;
import us.mindbuilders.petemit.timegoalie.data.TimeGoalieContract;
import us.mindbuilders.petemit.timegoalie.dummy.DummyContent;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieDateUtils;

/**
 * The report fragment
 */
public class GoalReportFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, GoalListActivity.TimeGoalieReportUpdater {
    private static final int GOAL_REPORT_LOADER_ID = 44;
    private static final int GOAL_ENTRY_LOADER_ID = 88;
    private static final int MONTHLY_ALL_GOALS = 100;
    private static final int MONTHLY_ONE_GOAL = 101;
    private static final int WEEKLY_ALL_GOALS = 102;
    private static final int WEEKLY_ONE_GOAL = 103;
    private static final int ALL_GOALS_ID = -999;
    private static final float CHART_TEXT_SIZE = 14f;
    private static final float CHART_OFFSET = 8f;
    private static final float RIGHT_CHART_OFFSET = 36f;

    private Spinner goalSpinner;
    private RadioGroup monthlyWeeklyRadioGroup;
    private RadioButton weeksRb;
    private RadioButton monthsRb;
    private ArrayList<GoalEntry> goalEntries;
    private ArrayList<Goal> goals;
    private LineChart chart;
    private String selectedScope;
    private String goalSelection;
    private ArrayAdapter spinnerAdapter;
    private Goal selectedGoal;
    private GoalReportFragment mySelf;

    @Override
    public void updateReport() {
        getActivity().getSupportLoaderManager().restartLoader(GOAL_REPORT_LOADER_ID, null, this);
    }

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
        mySelf = this;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(getString(R.string.selected_scope_key),selectedScope);
        outState.putString(getString(R.string.report_goal_selection_key),goalSelection);
        super.onSaveInstanceState(outState);
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


        monthlyWeeklyRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (i == R.id.radio_button_weeks) {
                    loaderBundle.putString(getString(R.string.goal_scope), scopeEnum.WEEKLY.name());
                    selectedScope = scopeEnum.WEEKLY.name();
                } else {
                    loaderBundle.putString(getString(R.string.goal_scope), scopeEnum.MONTHLY.name());
                    selectedScope = scopeEnum.MONTHLY.name();
                }

                if (getActivity().getSupportLoaderManager().getLoader(GOAL_REPORT_LOADER_ID) != null) {
                    getActivity().getSupportLoaderManager().restartLoader(GOAL_REPORT_LOADER_ID, loaderBundle, mySelf);

                }
            }
        });

        goalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedGoal = (Goal) (adapterView.getItemAtPosition(i));
                if (selectedGoal != null) {
                    if (selectedGoal.getGoalId() != ALL_GOALS_ID) {
                        loaderBundle.putString(getString(R.string.report_goal_selection_key),
                                goalSelectionEnum.SINGLEGOAL.name());
                        goalSelection=goalSelectionEnum.SINGLEGOAL.name();
                    } else {
                        loaderBundle.putString(getString(R.string.report_goal_selection_key),
                                goalSelectionEnum.ALLGOALS.name());
                        goalSelection=goalSelectionEnum.ALLGOALS.name();
                    }
                    if (getActivity().getSupportLoaderManager().getLoader(GOAL_REPORT_LOADER_ID) != null) {
                        getActivity().getSupportLoaderManager().restartLoader(GOAL_REPORT_LOADER_ID, loaderBundle, mySelf);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (goals == null) {
            goals = new ArrayList<Goal>();
            Goal goal = new Goal();
            goal.setName(getString(R.string.all_goals));
            goal.setGoalId(ALL_GOALS_ID);
            goals.add(goal);
        }

        if (savedInstanceState != null) {
            selectedScope = savedInstanceState.getString(getString(R.string.selected_scope_key));
            goalSelection = savedInstanceState.getString(getString(R.string.report_goal_selection_key));


        } else {
            //set the default to weeks selected
            weeksRb.setChecked(true);
            loaderBundle.putString(getString(R.string.goal_scope), scopeEnum.WEEKLY.name());
            selectedScope = scopeEnum.WEEKLY.name();
            if (selectedGoal == null) {
                loaderBundle.putString(getString(R.string.report_goal_selection_key),
                        goalSelectionEnum.ALLGOALS.name());
                goalSelection=goalSelectionEnum.ALLGOALS.name();
            }

        }


        chart.getDescription().setEnabled(false);
        chart.setExtraLeftOffset(CHART_OFFSET);
        chart.setExtraRightOffset(RIGHT_CHART_OFFSET);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(numOfWeeks);
        xAxis.setTextSize(CHART_TEXT_SIZE);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setTextSize(CHART_TEXT_SIZE);
        yAxis.setLabelCount(8, false);
        yAxis.setAxisMaximum(DEFAULTMAX);
        yAxis.setAxisMinimum(0f);

        yAxis.setDrawGridLines(true);

        Legend legend = chart.getLegend();
        legend.setTextSize(CHART_TEXT_SIZE);

        chart.getAxisRight().setEnabled(false);


        getActivity().getSupportLoaderManager().restartLoader(GOAL_REPORT_LOADER_ID, loaderBundle, this);
        getActivity().getSupportLoaderManager().restartLoader(GOAL_ENTRY_LOADER_ID, loaderBundle, this);


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

    public ArrayList<MonthInterval> getMonthIntervals(int numOfMonths) {
        ArrayList<MonthInterval> monthIntervals = new ArrayList<MonthInterval>();
        for (int i = 0; i < numOfWeeks; i++) {

            Calendar c = new GregorianCalendar().getInstance();
            //get the first day of the month
            c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
            c.add(Calendar.MONTH, -i);
            String beg = TimeGoalieDateUtils.getSqlDateString(c);
            //get last day of week
            c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
            String end = TimeGoalieDateUtils.getSqlDateString(c);
            MonthInterval month = new MonthInterval(beg, end);
            monthIntervals.add(month);
        }
        return monthIntervals;
    }

    public void initChart(ArrayList<GoalEntry> goalEntries) {
        chart.invalidate();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        if (selectedScope.equals(scopeEnum.WEEKLY.name())) {
            final ArrayList<WeekInterval> weekIntervals = getWeekIntervals(numOfWeeks);
            HashMap<String, ArrayList<String>> weekMap = new HashMap<String, ArrayList<String>>();

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
                    int i2 = weekIntervals.size() - i - 1;
                    Entry entry = new Entry(i, weekMap.get(weekIntervals.get(i2).getBeginningOfWeek()).size());
                    entries.add(entry);
                }
            }
            //need to format the xAxis
            XAxis xAxis = chart.getXAxis();
            xAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float v, AxisBase axisBase) {
                    int inverse = weekIntervals.size()-1-(int)v;
                    return weekIntervals.get(inverse).getBeginningOfWeek();
                }
            });

        } else if (selectedScope.equals(scopeEnum.MONTHLY.name())) {
            final ArrayList<MonthInterval> monthIntervals = getMonthIntervals(numOfMonths);
            HashMap<String, ArrayList<String>> monthMap = new HashMap<String, ArrayList<String>>();


            for (MonthInterval interval : monthIntervals) {
                monthMap.put(interval.getBegOfMonth(), new ArrayList<String>());
            }

            if (goalEntries != null) {
                for (int i = 0; i < goalEntries.size(); i++) {
                    GoalEntry goalEntry = goalEntries.get(i);
                    if (!goalEntry.getHasSucceeded()) {
                        continue;
                    }
                    for (MonthInterval interval : monthIntervals) {
                        String goalDate = goalEntry.getDate();
                        Date goalTime = null;
                        Date begTime = null;
                        Date endTime = null;
                        try {
                            goalTime = TimeGoalieDateUtils.df.parse(goalDate);
                            begTime = TimeGoalieDateUtils.df.parse(interval.getBegOfMonth());
                            endTime = TimeGoalieDateUtils.df.parse(interval.getEndOfMonth());
                        } catch (ParseException p) {
                            Log.e("GoalReportFragment", p.toString());
                        }

                        if ((goalTime.compareTo(begTime)) > 0 && (goalTime.compareTo(endTime) < 0)) {
                            ArrayList<String> list = monthMap.get(interval.getBegOfMonth());
                            list.add(goalDate);
                        }
                    }
                }
                for (int i = 0; i < monthIntervals.size(); i++) {
                    int i2 = monthIntervals.size() - i - 1;
                    Entry entry = new Entry(i, monthMap.get(monthIntervals.get(i2).getBegOfMonth()).size());
                    entries.add(entry);
                }
            }


            //need to format the xAxis
            XAxis xAxis = chart.getXAxis();
            xAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float v, AxisBase axisBase) {
                    int inverse = monthIntervals.size()-1-(int)v;
                    String month =monthIntervals.get(inverse).getBegOfMonth();
                    return TimeGoalieDateUtils.getMonthFromStringDate(month);
                }
            });
        }//end if monthly scope
        //format the axes again
        int max = DEFAULTMAX;
        YAxis yAxis = chart.getAxisLeft();

        if (entries != null && entries.size() > 0) {
            for (Entry ent : entries
                    ) {
                if (max < ent.getY()) {
                    //give it a slight buffer
                    max = (int) ent.getY() + 5;
                }
            }
        }
        yAxis.setAxisMaximum(max);



        LineDataSet dataSet = null;
        if (selectedGoal != null) {
            if (selectedGoal.getGoalId() != ALL_GOALS_ID) {
                dataSet = new LineDataSet(entries, selectedGoal.getName());
            } else {
                dataSet = new LineDataSet(entries, "Total Goals Saved");
            }
        } else {
            dataSet = new LineDataSet(entries, "Total Goals Saved");
        }



        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setColor(ColorTemplate.getHoloBlue());
        dataSet.setCircleColor(Color.WHITE);
        dataSet.setLineWidth(5f);
        dataSet.setValueTextSize(CHART_TEXT_SIZE);
        dataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                return (String.valueOf((int) (v)));
            }
        });

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);


        //Next, make Entries out of all this mess
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        CursorLoader cl;
        if (id == GOAL_REPORT_LOADER_ID) {

            String scope = null;
            String goalSelect = null;
            if (args!= null) {
                scope = args.getString(getString(R.string.goal_scope));
                goalSelect = args.getString(getString(R.string.report_goal_selection_key));
            }

            if (scope == null && selectedScope != null) {
                scope = selectedScope;
                if (scope == null) {
                    scope = scopeEnum.WEEKLY.name();
                }
            }


            if (goalSelect == null && goalSelection != null) {
                goalSelect = goalSelection;
                if (goalSelect == null) {
                    goalSelect = goalSelectionEnum.ALLGOALS.name();
                }
            }

            long goalId = 0;
            int selectedQuery = 0;
            if (scope.equals(scopeEnum.WEEKLY.name())) {
                if (goalSelect.equals(goalSelectionEnum.ALLGOALS.name())) {
                    selectedQuery = WEEKLY_ALL_GOALS;
                }
                if (goalSelect.equals(goalSelectionEnum.SINGLEGOAL.name())) {
                    selectedQuery = WEEKLY_ONE_GOAL;
                    goalId = selectedGoal.getGoalId();
                }
            }

            if (scope.equals(scopeEnum.MONTHLY.name())) {
                if (goalSelect.equals(goalSelectionEnum.ALLGOALS.name())) {
                    selectedQuery = MONTHLY_ALL_GOALS;
                }
                if (goalSelect.equals(goalSelectionEnum.SINGLEGOAL.name())) {
                    selectedQuery = MONTHLY_ONE_GOAL;
                    goalId = selectedGoal.getGoalId();
                }
            }

            switch (selectedQuery) {

                case MONTHLY_ALL_GOALS:
                    cl = new CursorLoader(this.getContext(),
                            TimeGoalieContract.getMonthSuccessfulGoals(DEFAULTMONTHS),
                            null,
                            null,
                            null,
                            null
                    );
                    return cl;
                case MONTHLY_ONE_GOAL:
                    cl = new CursorLoader(this.getContext(),
                            TimeGoalieContract.getMonthSuccessfulGoalsByGoal(goalId, DEFAULTMONTHS),
                            null,
                            null,
                            null,
                            null
                    );
                    return cl;
                case WEEKLY_ALL_GOALS:
                    cl = new CursorLoader(this.getContext(),
                            TimeGoalieContract.getWeekSuccessfulGoals(DEFAULTWEEKS),
                            null,
                            null,
                            null,
                            null
                    );
                    return cl;
                case WEEKLY_ONE_GOAL:
                    cl = new CursorLoader(this.getContext(),
                            TimeGoalieContract.getWeekSuccessfulGoalsByGoal(goalId, DEFAULTWEEKS),
                            null,
                            null,
                            null,
                            null
                    );
                    return cl;
            }
        }
        if (id == GOAL_ENTRY_LOADER_ID) {
            cl = new CursorLoader(this.getContext(),
                    TimeGoalieContract.Goals.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);

            return cl;

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
            goals.addAll(Goal.createGoalListFromCursor(cursor));

//            ArrayList<String> list = new ArrayList<String>();
//            if (goals != null) {
//                for (Goal goal : goals) {
//                    list.add(goal.getName());
//                }
//            }
            spinnerAdapter = new ArrayAdapter<Goal>(this.getContext(),
                    R.layout.spinner_text_layout, goals);
            goalSpinner.setAdapter(spinnerAdapter);
        }


    }

    @Override
    public void onLoaderReset(Loader loader) {
        if (loader.getId() == GOAL_REPORT_LOADER_ID) {
            goalEntries = null;
            loader.reset();
        }
        if (loader.getId() == GOAL_ENTRY_LOADER_ID) {
            goals = null;
            loader.reset();
        }


    }
}
