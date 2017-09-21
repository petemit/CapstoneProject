package us.mindbuilders.petemit.timegoalie;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.facebook.stetho.Stetho;

import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.TimeGoalieAlarmObject;
import us.mindbuilders.petemit.timegoalie.data.TimeGoalieContract;
import us.mindbuilders.petemit.timegoalie.dummy.DummyContent;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieDateUtils;

import java.util.List;

/**
 * List of Goals.  In multi-pane, shows reports as well {@link GoalReportActivity}
 */
public class GoalListActivity extends AppCompatActivity implements View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final int GOAL_LOADER_ID = 4;
    private GoalRecyclerViewAdapter rvAdapter;

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
        rvAdapter = new GoalRecyclerViewAdapter(this);
        toolbar.setTitle(getTitle());

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
    protected void onPause() {
        super.onPause();
        for (TimeGoalieAlarmObject tgoal : BaseApplication.getTimeGoalieAlarmObjects())
              {
                  tgoal.getCountDownTimer().cancel();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
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
                CursorLoader cl = new CursorLoader(this,
                        TimeGoalieContract.buildGetAllGoalsForASpecificDayQueryUri(
                                TimeGoalieDateUtils.getDayIdFromToday()),
                        null,
                        null,
                        null,
                        null
                );
                return cl;
            default:
                throw new RuntimeException("Loader not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //creates arraylist of goals
        rvAdapter.swapCursor(Goal.createGoalListFromCursor(data));
        rvAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        rvAdapter.swapCursor(null);
    }

}
