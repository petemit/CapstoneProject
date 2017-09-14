package us.mindbuilders.petemit.timegoalie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class NewGoalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_goal);
        getSupportFragmentManager().beginTransaction().replace(R.id.new_goal_container,NewGoalFragment.getInstance()).commit();
    }
}
