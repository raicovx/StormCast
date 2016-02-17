package com.jamesc.stormcast.UI;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jamesc.stormcast.R;
import com.jamesc.stormcast.Weather.Hour;

/**
 * Created by James on 12/02/2016.
 */
public class HourlyItemsRecyclerViewAdapter extends RecyclerView.Adapter<HourlyItemsRecyclerViewAdapter.HourlyItemsViewHolder> {

    private Hour[] mHours;

    public HourlyItemsRecyclerViewAdapter(Hour[] hours){
            mHours = hours;
    }
    @Override
    public HourlyItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hourly_list_item, parent, false);
        HourlyItemsViewHolder viewHolder = new HourlyItemsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HourlyItemsViewHolder holder, int position) {
            holder.bindHour(mHours[position]);
        //holder.mHourlyItemCardView.setCardBackgroundColor(Color.parseColor("#2196F3"));
    }

    @Override
    public int getItemCount() {
        return mHours.length;
    }

    public class HourlyItemsViewHolder extends RecyclerView.ViewHolder {

        public CardView mHourlyItemCardView;
        public LinearLayout mHourlyItemCardLayout;
        public LinearLayout mDateTimeBar;
        public TextView mTimeTextView;
        public TextView mSummaryTextView;
        public TextView mTemperatureTextView;
        public TextView mApparentTemperatureTextView;
        public TextView mFeelsLikeTextView;
        public TextView mDegreesMainTextView;
        public TextView mDegreesAltTextView;
        public ImageView mIconImageView;
        public String temperatureFormatPreference;

        private SharedPreferences initializeSharedPrefs() {
            return PreferenceManager.getDefaultSharedPreferences(itemView.getContext());

        }
        public String getTemperatureFormatPreferences(){
            SharedPreferences sharedPref = initializeSharedPrefs();
            String temperatureFormatPref = sharedPref.getString(SettingsActivity.PrefsFragment.KEY_PREF_TEMPERATURE_UNITS, "0");
            return temperatureFormatPref;
        }

        public HourlyItemsViewHolder(View itemView) {
            super(itemView);
            temperatureFormatPreference = getTemperatureFormatPreferences();
            mHourlyItemCardView = (CardView)itemView.findViewById(R.id.hourly_weather_card_view);
            mHourlyItemCardLayout = (LinearLayout)itemView.findViewById(R.id.hourly_weather_card_layout);
            mDateTimeBar = (LinearLayout)itemView.findViewById(R.id.hourly_time_date_bar);
            mTimeTextView = (TextView)itemView.findViewById(R.id.hourly_time_text_view);
            mFeelsLikeTextView = (TextView)itemView.findViewById(R.id.feels_like_text_view);
            mSummaryTextView = (TextView)itemView.findViewById(R.id.hourly_summary_text_view);
            mTemperatureTextView = (TextView)itemView.findViewById(R.id.hourly_temperature_text_view);
            mApparentTemperatureTextView = (TextView)itemView.findViewById(R.id.hourly_apparent_temperature_text_view);
            mDegreesMainTextView = (TextView)itemView.findViewById(R.id.degrees_text_view);
            mDegreesAltTextView = (TextView)itemView.findViewById(R.id.degrees_text_view_two);
            mIconImageView = (ImageView)itemView.findViewById(R.id.hourly_icon_image_view);
        }

        public void bindHour(Hour hour) {
            Boolean mWallpaperTone;
            mTimeTextView.setText(hour.getFormattedHourlyTime());
            mSummaryTextView.setText(hour.getSummary());
            if (temperatureFormatPreference.equals("0")) {
                mTemperatureTextView.setText(String.format("%.1f", ((hour.getTemperature() - 32) * 5 / 9)));
                mDegreesMainTextView.setText("°C");
                mDegreesAltTextView.setText("°C");
                mApparentTemperatureTextView.setText(String.format("%.1f", ((hour.getApparentTemperature() - 32) * 5 / 9)));


            } else if (temperatureFormatPreference.equals("1")) {
                mTemperatureTextView.setText(String.format("%.1f", hour.getTemperature()));
                mApparentTemperatureTextView.setText(String.format("%.1f", hour.getApparentTemperature()));
                mDegreesMainTextView.setText("°F");
                mDegreesAltTextView.setText("°F");
            }
            mIconImageView.setImageResource(hour.getIconId());
            String mIcon = hour.getIcon();
            switch (mIcon) {
                case "clear-day":
                    mHourlyItemCardView.setCardBackgroundColor(ContextCompat.getColor(HourlyWeather.getContext(), R.color.clear_day));
                    mDateTimeBar.setBackgroundColor(ContextCompat.getColor(HourlyWeather.getContext(), R.color.clear_day_dark));
                    mWallpaperTone = false;
                    break;
                case "clear-night":
                    mHourlyItemCardView.setCardBackgroundColor(ContextCompat.getColor(HourlyWeather.getContext(), R.color.clear_night));
                    mDateTimeBar.setBackgroundColor(ContextCompat.getColor(HourlyWeather.getContext(), R.color.clear_night_dark));
                    mWallpaperTone = false;
                    break;
                case "rain":
                    mHourlyItemCardView.setCardBackgroundColor(ContextCompat.getColor(HourlyWeather.getContext(), R.color.rain));
                    mDateTimeBar.setBackgroundColor(ContextCompat.getColor(HourlyWeather.getContext(), R.color.rain_dark));
                    mWallpaperTone = false;
                    break;
                case "snow":
                case "sleet":
                    mHourlyItemCardView.setCardBackgroundColor(ContextCompat.getColor(HourlyWeather.getContext(), R.color.snow));
                    mDateTimeBar.setBackgroundColor(ContextCompat.getColor(HourlyWeather.getContext(), R.color.snow_dark));
                    mWallpaperTone = true;
                    break;
                case "wind":
                    mHourlyItemCardView.setCardBackgroundColor(ContextCompat.getColor(HourlyWeather.getContext(), R.color.wind));
                    mDateTimeBar.setBackgroundColor(ContextCompat.getColor(HourlyWeather.getContext(), R.color.wind_dark));
                    mWallpaperTone = true;
                    break;
                case "cloudy":
                    mHourlyItemCardView.setCardBackgroundColor(ContextCompat.getColor(HourlyWeather.getContext(), R.color.cloudy));
                    mDateTimeBar.setBackgroundColor(ContextCompat.getColor(HourlyWeather.getContext(), R.color.cloudy_dark));
                    mWallpaperTone = true;
                    break;
                case "fog":
                case "partly-cloudy-day":
                    mHourlyItemCardView.setCardBackgroundColor(ContextCompat.getColor(HourlyWeather.getContext(), R.color.partly_cloudy));
                    mDateTimeBar.setBackgroundColor(ContextCompat.getColor(HourlyWeather.getContext(), R.color.partly_cloudy_dark));
                    mWallpaperTone = true;
                    break;
                case "partly-cloudy-night":
                    mHourlyItemCardView.setCardBackgroundColor(ContextCompat.getColor(HourlyWeather.getContext(), R.color.cloudy_night));
                    mDateTimeBar.setBackgroundColor(ContextCompat.getColor(HourlyWeather.getContext(), R.color.cloudy_night_dark));
                    mWallpaperTone = false;
                    break;
                default:
                    mWallpaperTone = true;
                    break;
            }
            if (mWallpaperTone) {
                mTimeTextView.setTextColor(ContextCompat.getColor(HourlyWeather.getContext(), R.color.primary_text_color));
                mTemperatureTextView.setTextColor(ContextCompat.getColor(HourlyWeather.getContext(), R.color.secondary_text_color));
                mFeelsLikeTextView.setTextColor(ContextCompat.getColor(HourlyWeather.getContext(), R.color.secondary_text_color));
                mSummaryTextView.setTextColor(ContextCompat.getColor(HourlyWeather.getContext(), R.color.secondary_text_color));
                mApparentTemperatureTextView.setTextColor(ContextCompat.getColor(HourlyWeather.getContext(), R.color.secondary_text_color));
                mDegreesMainTextView.setTextColor(ContextCompat.getColor(HourlyWeather.getContext(), R.color.secondary_text_color));
                mDegreesAltTextView.setTextColor(ContextCompat.getColor(HourlyWeather.getContext(), R.color.secondary_text_color));
            } else {
                mTimeTextView.setTextColor(ContextCompat.getColor(HourlyWeather.getContext(), R.color.primary_text_color));
                mTemperatureTextView.setTextColor(ContextCompat.getColor(HourlyWeather.getContext(), R.color.primary_text_color));
                mFeelsLikeTextView.setTextColor(ContextCompat.getColor(HourlyWeather.getContext(), R.color.primary_text_color));
                mSummaryTextView.setTextColor(ContextCompat.getColor(HourlyWeather.getContext(), R.color.primary_text_color));
                mApparentTemperatureTextView.setTextColor(ContextCompat.getColor(HourlyWeather.getContext(), R.color.primary_text_color));
                mDegreesMainTextView.setTextColor(ContextCompat.getColor(HourlyWeather.getContext(), R.color.primary_text_color));
                mDegreesAltTextView.setTextColor(ContextCompat.getColor(HourlyWeather.getContext(), R.color.primary_text_color));

            }
        }


    }
}
