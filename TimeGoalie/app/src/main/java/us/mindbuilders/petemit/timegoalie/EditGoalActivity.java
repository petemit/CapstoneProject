package us.mindbuilders.petemit.timegoalie;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;

import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Day;
import us.mindbuilders.petemit.timegoalie.TimeGoalieDO.Goal;
import us.mindbuilders.petemit.timegoalie.data.TimeGoalieContract;
import us.mindbuilders.petemit.timegoalie.utils.TimeGoalieUtils;

public class EditGoalActivity extends AppCompatActivity implements EditGoalFragment.GoalGetter {
    private Goal goal;
    String[] dayNames = getApplicationContext().getResources()
            .getStringArray(R.array.days_of_the_week);
    String[] daySeqValues = getApplicationContext().getResources()
            .getStringArray(R.array.days_of_the_week_values);

    private ArrayList<Day> parseCommaSeparated(String list) {
        ArrayList<Day> days = new ArrayList<>();
        String[] strings = list.split(",");
        for (int i = 0; i < strings.length; i++) {
            Day day = new Day();
            day.setSequence(getDaySeq(strings[i]));
            day.setName(strings[i]);
            days.add(day);
        }
        return days;
    }

    private int getDaySeq(String day) {
        for (int i = 0; i < dayNames.length; i++) {
            if (day.equals(dayNames[i])){
                return i+1;
            }
        }
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Goal goal = new Goal();
        Intent intent = getIntent();
        goal.setGoalId(intent.getLongExtra("goal-name",0));
        goal.setName(intent.getStringExtra("goal-name"));
        goal.setGoalDays(parseCommaSeparated(intent.getStringExtra("goal-days")));
        goal.setGoalTypeId(intent.getLongExtra("goal-type",0));
        goal.setMinutes(intent.getIntExtra("goal-minutes",0));
        goal.setHours(intent.getIntExtra("goal-hours",0));
        goal.setIsDaily(intent.getIntExtra("goal-isDaily",0));
        goal.setIsDisabled(intent.getIntExtra("goal-isDisabled",0));
        goal.setIsWeekly(intent.getIntExtra("goal-isWeekly",0));

        setContentView(R.layout.activity_edit_goal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.report_toolbar);
        setSupportActionBar(toolbar);
        getSupportFragmentManager().beginTransaction().replace(
                R.id.new_goal_container, NewGoalFragment.getInstance()).commit();

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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
}
