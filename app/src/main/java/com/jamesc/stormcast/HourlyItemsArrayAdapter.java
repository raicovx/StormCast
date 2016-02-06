package com.jamesc.stormcast;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by James on 5/02/2016.
 */
public class HourlyItemsArrayAdapter extends ArrayAdapter<HourlyWeatherModel> {
    ImageView ivCustom;
    TextView tvTimeCustom;
    TextView tvTemperatureCustom;
    TextView tvApparentTemperatureCustom;
    TextView tvSummaryCustom;
    String temperatureFormatPreference;

    public HourlyItemsArrayAdapter(Context context, List<HourlyWeatherModel> objects) {
        super(context, 0, objects);
    }
    private SharedPreferences initializeSharedPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(getContext());

    }
    public String getTemperatureFormatPreferences(){
        SharedPreferences sharedPref = initializeSharedPrefs();
        String temperatureFormatPref = sharedPref.getString(SettingsActivity.PrefsFragment.KEY_PREF_TEMPERATURE_UNITS, "0");
        return temperatureFormatPref;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        temperatureFormatPreference  = getTemperatureFormatPreferences();
        HourlyWeatherModel hourlyWeatherModel = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.hourly_list_item, parent, false);

        }
        ivCustom = (ImageView) convertView.findViewById(R.id.hourly_icon_image_view);
        tvTimeCustom = (TextView) convertView.findViewById(R.id.hourly_time_text_view);
        tvApparentTemperatureCustom = (TextView)convertView.findViewById(R.id.hourly_apparent_temperature_text_view);
        tvTemperatureCustom = (TextView)convertView.findViewById(R.id.hourly_temperature_text_view);
        if (temperatureFormatPreference.equals("0")) {
            tvTemperatureCustom.setText(String.format("%.1f", ((hourlyWeatherModel.getTemperature()- 32) * 5 / 9) ));
            tvApparentTemperatureCustom.setText(String.format("%.1f", ((hourlyWeatherModel.getApparentTemperature()- 32) * 5 / 9)));

        } else if (temperatureFormatPreference.equals("1")) {
            tvTemperatureCustom.setText(String.format("%.1f", hourlyWeatherModel.getTemperature()));
            tvApparentTemperatureCustom.setText(String.format("%.1f", hourlyWeatherModel.getApparentTemperature()));
        }

        ivCustom.setImageDrawable(ContextCompat.getDrawable(getContext(), hourlyWeatherModel.getIconId()));
        tvSummaryCustom = (TextView) convertView.findViewById(R.id.hourly_summary_text_view);
        tvTimeCustom.setText(hourlyWeatherModel.getFormattedTime(hourlyWeatherModel.getTime()));
        tvSummaryCustom.setText(hourlyWeatherModel.getSummary());

        return convertView;
    }
}