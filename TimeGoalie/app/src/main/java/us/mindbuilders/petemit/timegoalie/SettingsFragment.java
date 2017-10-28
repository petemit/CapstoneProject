package us.mindbuilders.petemit.timegoalie;


import android.media.audiofx.BassBoost;
import android.os.Bundle;



import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import us.mindbuilders.petemit.timegoalie.services.TimeGoalieNotifications;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    public SettingsFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Preference pref = findPreference(getString(R.string.pref_daily_notification_key));
        pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                boolean result = (boolean)o;
                TimeGoalieNotifications.subscribeFromPref(result);
                return true;
            }
        });

    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }
}

