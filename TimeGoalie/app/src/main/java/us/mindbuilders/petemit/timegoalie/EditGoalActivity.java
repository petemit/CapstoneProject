package us.mindbuilders.petemit.timegoalie;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;

import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Day;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;
import us.mindbuilders.petemit.timegoalie.data.GetGoalDaysByGoalId;
import us.mindbuilders.petemit.timegoalie.data.TimeGoalieContract;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieUtils;

public class EditGoalActivity extends AppCompatActivity implements EditGoalFragment.GoalGetter, GetGoalDaysByGoalId.myCallback {
    private Goal goal;
    String[] dayNames;
    Context context;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
         dayNames = getResources().getStringArray(R.array.days_of_the_week);
        Goal newGoal = new Goal();
        Intent intent = getIntent();
        newGoal.setGoalId(intent.getLongExtra("goal-id",0));
        newGoal.setName(intent.getStringExtra("goal-name"));
        newGoal.setGoalTypeId(intent.getLongExtra("goal-type",0));
        newGoal.setMinutes(intent.getIntExtra("goal-minutes",0));
        newGoal.setHours(intent.getIntExtra("goal-hours",0));
        newGoal.setIsDaily(intent.getIntExtra("goal-isDaily",0));
        newGoal.setIsDisabled(intent.getIntExtra("goal-isDisabled",0));
        newGoal.setIsWeekly(intent.getIntExtra("goal-isWeekly",0));
        goal = newGoal;



        setContentView(R.layout.activity_edit_goal);
        progressBar = findViewById(R.id.progress_bar_edit_goal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.report_toolbar);
        setSupportActionBar(toolbar);


        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        progressBar.setVisibility(View.VISIBLE);
        new GetGoalDaysByGoalId(context, this).execute(goal);
    }

    private void showFragment(){
        getSupportFragmentManager().beginTransaction().replace(
                R.id.edit_goal_container, EditGoalFragment.getInstance()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, GoalListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Goal getGoal() {
        return goal;
    }

    @Override
    public void show(ArrayList<Day> days) {
        getGoal().setGoalDays(days);
        showFragment();
        progressBar.setVisibility(View.INVISIBLE);
    }
}
