package us.mindbuilders.petemit.timegoalie;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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


import us.mindbuilders.petemit.timegoalie.dummy.DummyContent;

import java.util.List;

/**
 * List of Goals.  In multi-pane, shows reports as well {@link GoalReportActivity}
 */
public class GoalListActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(),NewGoalActivity.class));
            }
        });

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.goal_list);
        if (recyclerView != null) {
            recyclerView.setAdapter(new GoalRecyclerViewAdapter(DummyContent.ITEMS,this));
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.settings_key){
            startActivity(new Intent(this,PreferenceActivity.class));
            return true;
        }
        return true;
    }

    /**
     * The Goal List Activity will handle onClicks
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
}
