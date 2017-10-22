package us.mindbuilders.petemit.timegoalie;

import android.app.Activity;
import android.database.Cursor;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

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

    private static final int DEFAULTWEEKS = 4;
    private static final int DEFAULTMONTHS = 4;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.goal_report_fragment, container, false);

        // Show the dummy content as text in a TextView.
        LineChart Chart = (LineChart) rootView.findViewById(R.id.report_chart);
      //  goalSpinner = rootView.findViewById

        Bundle bundle = new Bundle();

        //for testing
        bundle.putInt(getString(R.string.goal_scope),WEEKLY_ALL_GOALS);

        getActivity().getSupportLoaderManager().initLoader(GOAL_REPORT_LOADER_ID,bundle,this);
        getActivity().getSupportLoaderManager().initLoader(GOAL_ENTRY_LOADER_ID,null,this);

        return rootView;
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
            if (cursor != null) {
                cursor.moveToFirst();
            }
        }

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
