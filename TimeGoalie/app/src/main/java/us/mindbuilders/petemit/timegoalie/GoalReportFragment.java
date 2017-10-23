package us.mindbuilders.petemit.timegoalie;

import android.app.Activity;
import android.database.Cursor;
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
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

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
    private ArrayList<GoalEntry> goalEntries;
    private ArrayList<Entry> entries = new ArrayList<Entry>();
    private Chart chart;

    private static final int DEFAULTWEEKS = 4;
    private static final int DEFAULTMONTHS = 4;
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
        LineChart chart = (LineChart) rootView.findViewById(R.id.report_chart);
        //  goalSpinner = rootView.findViewById

        Bundle bundle = new Bundle();

        //for testing
        bundle.putInt(getString(R.string.goal_scope), WEEKLY_ALL_GOALS);

        getActivity().getSupportLoaderManager().initLoader(GOAL_REPORT_LOADER_ID, bundle, this);
        getActivity().getSupportLoaderManager().initLoader(GOAL_ENTRY_LOADER_ID, null, this);


        IAxisValueFormatter xAxisFormatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                return null;
            }
        };

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(numOfWeeks);
        xAxis.setValueFormatter(xAxisFormatter);

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
                if(!goalEntry.getHasSucceeded()){
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
                Entry entry = new Entry(i,weekMap.get(weekIntervals.get(i).getBeginningOfWeek()).size());
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "Time");
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();


        //Next, make Entries out of all this mess
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if (id == GOAL_REPORT_LOADER_ID) {
            int selectedQuery = args.getInt(getString(R.string.goal_scope));

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
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        if (loader.getId() == GOAL_REPORT_LOADER_ID) {
            goalEntries = GoalEntry.makeGoalEntryListFromCursor(cursor);
            initChart(goalEntries);
        }

    }

    @Override
    public void onLoaderReset(Loader loader) {
        if (loader.getId() == GOAL_REPORT_LOADER_ID) {
            goalEntries = null;
        }

    }
}
