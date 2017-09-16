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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import us.mindbuilders.petemit.timegoalie.dummy.DummyContent;

import java.util.List;

/**
 * An activity representing a list of Goals. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link GoalReportActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
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
                Snackbar.make(view, "Replace with add goal activity", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
