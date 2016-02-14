package com.jamesc.stormcast.UI;

import android.content.SharedPreferences;
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
    }

    @Override
    public int getItemCount() {
        return mHours.length;
    }

    public class HourlyItemsViewHolder extends RecyclerView.ViewHolder {

        public CardView mHourlyItemCardView;
        public LinearLayout mDateTimeBar;
        public TextView mTimeTextView;
        public TextView mSummaryTextView;
        public TextView mTemperatureTextView;
        public TextView mApparentTemperatureTextView;
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
            mDateTimeBar = (LinearLayout)itemView.findViewById(R.id.hourly_time_date_bar);
            mTimeTextView = (TextView)itemView.findViewById(R.id.hourly_time_text_view);
            mSummaryTextView = (TextView)itemView.findViewById(R.id.hourly_summary_text_view);
            mTemperatureTextView = (TextView)itemView.findViewById(R.id.hourly_temperature_text_view);
            mApparentTemperatureTextView = (TextView)itemView.findViewById(R.id.hourly_apparent_temperature_text_view);
            mDegreesMainTextView = (TextView)itemView.findViewById(R.id.degrees_text_view);
            mDegreesAltTextView = (TextView)itemView.findViewById(R.id.degrees_text_view_two);
            mIconImageView = (ImageView)itemView.findViewById(R.id.hourly_icon_image_view);
        }

        public void bindHour(Hour hour){
            mTimeTextView.setText(hour.getFormattedHourlyTime());
            mSummaryTextView.setText(hour.getSummary());
            if (temperatureFormatPreference.equals("0")) {
                mTemperatureTextView.setText(String.format("%.1f", ((hour.getTemperature()- 32) * 5 / 9) ));
                mDegreesMainTextView.setText("°C");
               mDegreesAltTextView.setText("°C");
                mApparentTemperatureTextView.setText(String.format("%.1f", ((hour.getApparentTemperature()- 32) * 5 / 9)));


            } else if (temperatureFormatPreference.equals("1")) {
                mTemperatureTextView.setText(String.format("%.1f", hour.getTemperature()));
                mApparentTemperatureTextView.setText(String.format("%.1f", hour.getApparentTemperature()));
                mDegreesMainTextView.setText("°F");
                mDegreesAltTextView.setText("°F");
            }
            mIconImageView.setImageResource(hour.getIconId());
            String mIcon = hour.getIcon();
            Boolean mWallpaperTone;
            switch (mIcon) {
                case "clear-day":
                    mHourlyItemCardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(),R.color.clear_day));
                    mWallpaperTone = false;
                    break;
                case "clear-night":
                    mHourlyItemCardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.clear_night));
                    mWallpaperTone = false;
                    break;
                case "rain":
                    mHourlyItemCardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.rain));
                    mWallpaperTone = false;
                    break;
                case "snow":
                case "sleet":
                    mHourlyItemCardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.snow));
                    mWallpaperTone = true;
                    break;
                case "wind":
                    mHourlyItemCardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.wind));
                    mWallpaperTone = true;
                    break;
                case "cloudy":
                    mHourlyItemCardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.cloudy));
                    mWallpaperTone = true;
                    break;
                case "fog":
                case "partly-cloudy-day":
                    mHourlyItemCardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.partly_cloudy));
                    mWallpaperTone = true;
                    break;
                case "partly-cloudy-night":
                    mHourlyItemCardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.cloudy_night));
                    mWallpaperTone = false;
                    break;
            }
        }
    }
}
