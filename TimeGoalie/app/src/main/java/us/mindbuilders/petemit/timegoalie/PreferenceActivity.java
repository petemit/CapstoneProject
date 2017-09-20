package us.mindbuilders.petemit.timegoalie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;

public class PreferenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.preference_container, new SettingsFragment())
                .commit();
    }
}
