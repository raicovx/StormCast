package com.jamesc.stormcast;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

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
    TextView tvFeelsLike;
    String temperatureFormatPreference;
    private CardView cvHourlyWeather;
    String iconString;

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
        iconString = hourlyWeatherModel.getIcon();
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.hourly_list_item, parent, false);

        }
        cvHourlyWeather = (CardView)convertView.findViewById(R.id.hourly_weather_card_view);
        ivCustom = (ImageView) convertView.findViewById(R.id.hourly_icon_image_view);
        tvTimeCustom = (TextView) convertView.findViewById(R.id.hourly_time_text_view);
        tvApparentTemperatureCustom = (TextView)convertView.findViewById(R.id.hourly_apparent_temperature_text_view);
        tvTemperatureCustom = (TextView)convertView.findViewById(R.id.hourly_temperature_text_view);
        tvFeelsLike = (TextView)convertView.findViewById(R.id.feels_like_text_view);
        TextView tvMainDegrees = (TextView)convertView.findViewById(R.id.degrees_text_view);
        TextView tvAltDegrees = (TextView)convertView.findViewById(R.id.degrees_text_view_two);
        if (temperatureFormatPreference.equals("0")) {
            tvTemperatureCustom.setText(String.format("%.1f", ((hourlyWeatherModel.getTemperature()- 32) * 5 / 9) ));
            tvMainDegrees.setText("°C");
            tvAltDegrees.setText("°C");
            tvApparentTemperatureCustom.setText(String.format("%.1f", ((hourlyWeatherModel.getApparentTemperature()- 32) * 5 / 9)));


        } else if (temperatureFormatPreference.equals("1")) {
            tvTemperatureCustom.setText(String.format("%.1f", hourlyWeatherModel.getTemperature()));
            tvApparentTemperatureCustom.setText(String.format("%.1f", hourlyWeatherModel.getApparentTemperature()));
            tvMainDegrees.setText("°F");
            tvAltDegrees.setText("°F");
        }

        ivCustom.setImageDrawable(ContextCompat.getDrawable(getContext(), hourlyWeatherModel.getIconId()));
        ivCustom.setColorFilter(hourlyWeatherModel.getIconColor(getContext()));
        tvSummaryCustom = (TextView) convertView.findViewById(R.id.hourly_summary_text_view);
        tvTimeCustom.setText(hourlyWeatherModel.getFormattedTime(hourlyWeatherModel.getTime()));
        tvSummaryCustom.setText(hourlyWeatherModel.getSummary());
        tvFeelsLike.setText("Feels like:");
        cvHourlyWeather.setCardBackgroundColor(hourlyWeatherModel.getCardColor(getContext()));

        if(hourlyWeatherModel.getWallpaperTone()){

        }else{
            tvSummaryCustom.setTextColor(ContextCompat.getColor(getContext(),R.color.primary_text_color));
            tvTimeCustom.setTextColor(ContextCompat.getColor(getContext(),R.color.primary_text_color));
            tvTemperatureCustom.setTextColor(ContextCompat.getColor(getContext(),R.color.primary_text_color));
            tvApparentTemperatureCustom.setTextColor(ContextCompat.getColor(getContext(),R.color.primary_text_color));
            tvFeelsLike.setTextColor(ContextCompat.getColor(getContext(),R.color.primary_text_color));
        }

        return convertView;
    }
}