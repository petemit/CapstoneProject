package us.mindbuilders.petemit.timegoalie;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;


import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.TimeGoalieAlarmObject;
import us.mindbuilders.petemit.timegoalie.data.TimeGoalieContract;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieDateUtils;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * List of Goals.  In multi-pane, shows reports as well {@link GoalReportActivity}
 */
public class GoalListActivity extends AppCompatActivity implements View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor>, DatePickerDialog.OnDateSetListener,
        GoalRecyclerViewAdapter.GoalCounter {
    private static final int GOAL_LOADER_ID = 4;
    private static final String noDateString = "NODATE";
    private GoalRecyclerViewAdapter rvAdapter;
    Spinner datespinner;
    private TextView dateSpinnerTextView;
    private ArrayAdapter<String> spinnerAdapter;
    private boolean isToday;
    private ArrayList<Goal> goalArrayList;
    private int successfulGoalCount = 0;
    private TextView tv_successfulGoalCount;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     * <p>
     * Initiates a Loader
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_list);
        getSupportLoaderManager().initLoader(GOAL_LOADER_ID, null, this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        rvAdapter = new GoalRecyclerViewAdapter(this, this, this);
        toolbar.setTitle(getTitle());
        tv_successfulGoalCount = findViewById(R.id.tv_numberOfGoalsCleared);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), NewGoalActivity.class));
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.goal_list);
        if (recyclerView != null) {
            recyclerView.setAdapter(rvAdapter);
        }

        if (findViewById(R.id.goal_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(GOAL_LOADER_ID, null, this);
        rvAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        for (TimeGoalieAlarmObject tgoal : BaseApplication.getTimeGoalieAlarmObjects()) {
            if (tgoal.getCountDownTimer() != null)
                tgoal.getCountDownTimer().cancel();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem dateItem = menu.findItem(R.id.date_spinner);
        datespinner = (Spinner) dateItem.getActionView();
        if (BaseApplication.getActiveCalendarDate() != null) {
            setDateAdapter(TimeGoalieDateUtils.
                    getNicelyFormattedDate(BaseApplication.getActiveCalendarDate()));
        }
        datespinner.setPopupBackgroundDrawable(null);
        datespinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    android.support.v4.app.DialogFragment dateFrag = new myDatePickerFragment();
                    dateFrag.show(getSupportFragmentManager(), "datepicker");
                    return true;
                } else {
                    return true;
                }
            }
        });
        return true;
    }

    public void setDateAdapter(String s) {
        ArrayList<String> list = new ArrayList<String>();
        list.add(s);
        spinnerAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_text_layout, list);
        datespinner.setAdapter(spinnerAdapter);

    }

    public void changeOutDate(String s) {
        if (spinnerAdapter != null) {
            spinnerAdapter.clear();
            spinnerAdapter.add(s);
            spinnerAdapter.notifyDataSetChanged();
            //make sure and change active date first
            getSupportLoaderManager().restartLoader(GOAL_LOADER_ID, null, this);
        }


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings_key) {
            startActivity(new Intent(this, PreferenceActivity.class));
            return true;
        }


        return true;
    }

    /**
     * The Goal List Activity will handle onClicks
     *
     * @param v
     */
    @Override
    public void onClick(View v) {

        // TODO: 9/15/2017 implement logic to support editing pencil
//        if (mTwoPane) {
//            Bundle arguments = new Bundle();
//       //     arguments.putString(GoalReportFragment.ARG_ITEM_ID, holder.mItem.id);
//            GoalReportFragment fragment = new GoalReportFragment();
//            fragment.setArguments(arguments);
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.goal_detail_container, fragment)
//                    .commit();
//        } else {
//            Context context = v.getContext();
//            Intent intent = new Intent(context, GoalReportActivity.class);
//       //     intent.putExtra(GoalReportFragment.ARG_ITEM_ID, holder.mItem.id);
//
//            context.startActivity(intent);
//        }

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case GOAL_LOADER_ID:
                //if it's today
                if (DateUtils.isToday(BaseApplication.getActiveCalendarDate().getTimeInMillis())) {
                    isToday = true;
                    CursorLoader cl = new CursorLoader(this,
                            TimeGoalieContract.buildGetAllGoalsForCurrentDayOfWeekQueryUri(
                                    TimeGoalieDateUtils.getDayIdFromToday()),
                            null,
                            null,
                            null,
                            null
                    );
                    return cl;
                } //else if another day, do same query except filter by day.
                else {
                    isToday = false;
                    CursorLoader cl = new CursorLoader(this,
                            TimeGoalieContract.getGoalsThatHaveGoalEntryForToday(),
                            null,
                            null,
                            new String[]{TimeGoalieDateUtils.
                                    getSqlDateString(BaseApplication.getActiveCalendarDate())},
                            null
                    );
                    return cl;
                }
            default:
                throw new RuntimeException("Loader not Implemented: " + id);
        }
    }

    public void updateSuccessfulGoalCount(ArrayList<Goal> goalArrayList) {
        successfulGoalCount = 0;
        for (int i = 0; i < goalArrayList.size(); i++) {
            if (goalArrayList.get(i).getGoalEntry() != null) {
                if (goalArrayList.get(i).getGoalEntry().getHasSucceeded() == 1) {
                    successfulGoalCount++;
                }
            }

        }
        tv_successfulGoalCount.setText(successfulGoalCount+"");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        successfulGoalCount=0;
        tv_successfulGoalCount.setText(String.valueOf(successfulGoalCount));
        //creates arraylist of goals
        goalArrayList = (Goal.createGoalListWithGoalEntriesFromCursor(data));
        rvAdapter.swapCursor(goalArrayList, isToday);
        rvAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        for (TimeGoalieAlarmObject tgoal : BaseApplication.getTimeGoalieAlarmObjects()) {
            if (tgoal.getCountDownTimer() != null)
                tgoal.getCountDownTimer().cancel();
        }
        rvAdapter.swapCursor(null);
    }


    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        int er = i;
        int ere = i1;
        int eree = i2;
        cal.set(i, i1, i2);
        //very important to set date first
        BaseApplication.setActiveCalendarDate(cal);

        //Now change the gui and restart the loader
        changeOutDate(TimeGoalieDateUtils.getNicelyFormattedDate(cal));


    }

    @Override
    public void updateGoalCounter(int successfulGoalCount) {
        //You know... I should just read from the database.  That'd be better.
     //   this.successfulGoalCount=successfulGoalCount;

        tv_successfulGoalCount.setText(String.valueOf(successfulGoalCount));
    }
}
