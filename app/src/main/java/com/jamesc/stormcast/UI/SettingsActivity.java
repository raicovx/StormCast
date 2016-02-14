package com.jamesc.stormcast.UI;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.jamesc.stormcast.R;

import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar actionBar = (Toolbar) findViewById(R.id.settings_toolbar);
        actionBar.setTitle("Settings");
        int actionBarColour = ContextCompat.getColor(getApplicationContext(), R.color.actionBarColorPrimary);
        actionBar.setBackgroundColor(actionBarColour);
        actionBar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_action_bar_back_arrow));
        actionBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setSupportActionBar(actionBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        PrefsFragment mPrefsFragment = new PrefsFragment();
        getFragmentManager().beginTransaction().replace(R.id.settings_fragment_container, mPrefsFragment).commit();

    }

    public static class PrefsFragment extends PreferenceFragment {
        public static final String KEY_PREF_WIND_SPEED_UNITS = "windSpeedMeasurement";
        public static final String KEY_PREF_DISTANCE_UNITS = "distanceMeasurement";
        public static final String KEY_PREF_TEMPERATURE_UNITS = "temperatureMeasurement";
        public static final String KEY_PREF_AUTO_SYNC_INTERVAL = "autoSyncInterval";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preference);
            ListPreference windSpeedMeasurementPref = (ListPreference) findPreference("windSpeedMeasurement");
            windSpeedMeasurementPref.setSummary(windSpeedMeasurementPref.getEntry());
            windSpeedMeasurementPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String newValueString = newValue.toString();
                    if (newValueString.equals("0")) {
                        preference.setSummary(R.string.miles_per_hour);
                    } else if (newValueString.equals("1")) {
                        preference.setSummary(R.string.meters_per_second);
                    } else if (newValueString.equals("2")) {
                        preference.setSummary(R.string.kilometres_per_hour);
                    }
                    return true;
                }
            });
            ListPreference distanceMeasurementPref = (ListPreference) findPreference("distanceMeasurement");
            distanceMeasurementPref.setSummary(distanceMeasurementPref.getEntry());
            distanceMeasurementPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String newValueString = (newValue.toString());
                    if (newValueString.equals("0")) {
                        preference.setSummary(R.string.distance_kilometres_setting_summary);
                    } else if (newValueString.equals("1")) {
                        preference.setSummary(R.string.distance_miles_setting_summary);
                    }
                    return true;
                }
            });

            ListPreference temperatureMeasurementPref = (ListPreference) findPreference("temperatureMeasurement");
            temperatureMeasurementPref.setSummary(temperatureMeasurementPref.getEntry());
            temperatureMeasurementPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String newValueString = (newValue.toString());
                    if (newValueString.equals("0")) {
                        preference.setSummary(R.string.celsius_setting_summary);
                    } else if (newValueString.equals("1")) {
                        preference.setSummary(R.string.fahrenheit_setting_summary);
                    }
                    return true;

                }
            });

            ListPreference autoRefreshIntervalPref = (ListPreference) findPreference("autoSyncInterval");
            autoRefreshIntervalPref.setSummary(autoRefreshIntervalPref.getEntry());
            autoRefreshIntervalPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String newValueString = (newValue.toString());
                    if (newValueString.equals("0")) {
                        preference.setSummary(R.string.auto_sync_never_setting_summary);
                    } else if (newValueString.equals("1")) {
                        preference.setSummary(R.string.auto_sync_thirty_minute_setting_summary);
                    } else if (newValueString.equals("2")) {
                        preference.setSummary(R.string.auto_sync_one_hour_setting_summary);
                    } else if (newValueString.equals("3")) {
                        preference.setSummary(R.string.auto_sync_two_hour_setting_summary);
                    } else if (newValueString.equals("4")) {
                        preference.setSummary(R.string.auto_sync_four_hour_setting_summary);
                    } else if (newValueString.equals("5")) {
                        preference.setSummary(R.string.auto_sync_six_hour_setting_summary);
                    }
                    return true;
                }
            });

        }


    }
}
