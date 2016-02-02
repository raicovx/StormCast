package com.jamesc.stormcast;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by James on 20/01/2016.
 */
public class CurrentWeather {
    private String mIcon;
    private long mTime;
    private double mTemperature;
    private double mPrecipChance;
    private double mHumidity;
    private String mSummary;
    private String mTimeZone;
    private int iconId;
    private int summaryCardColor;

    public int getSummaryCardColor(Context context) {
        if (mIcon.equals("clear-day")) {
            summaryCardColor = ContextCompat.getColor(context, R.color.clear_day);
        }
        else if (mIcon.equals("clear-night")) {
            summaryCardColor = ContextCompat.getColor(context, R.color.clear_night);
        }
        else if (mIcon.equals("rain")) {
            summaryCardColor = ContextCompat.getColor(context, R.color.rain);
        }
        else if (mIcon.equals("snow") || mIcon.equals("sleet")) {
            summaryCardColor = ContextCompat.getColor(context, R.color.snow);
        }
        else if (mIcon.equals("wind")) {
            summaryCardColor = ContextCompat.getColor(context,R.color.wind);
        }
        else if (mIcon.equals("cloudy")) {
            summaryCardColor = ContextCompat.getColor(context,R.color.cloudy);
        }
        else if (mIcon.equals("fog") || mIcon.equals("partly-cloudy-day")) {
            summaryCardColor = ContextCompat.getColor(context,R.color.partly_cloudy);
        }
        else if (mIcon.equals("partly-cloudy-night")) {
            summaryCardColor = ContextCompat.getColor(context,R.color.cloudy_night);
        }
        return summaryCardColor;
    }



    private boolean mGetInCelsius = true;

    public boolean isGetInCelsius(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPref.getString(SettingsActivity.PrefsFragment.KEY_PREF_TEMPERATURE_UNITS, "0");
        return mGetInCelsius;
    }

    public void setGetInCelsius(boolean getInCelsius) {
        this.mGetInCelsius = getInCelsius;
    }

    public String getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(String timeZone) {
        mTimeZone = timeZone;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public int getIconId(){

        if (mIcon.equals("clear-day")) {
            iconId = R.drawable.clear_day;
        }
        else if (mIcon.equals("clear-night")) {
            iconId = R.drawable.clear_night;
        }
        else if (mIcon.equals("rain")) {
            iconId = R.drawable.rain;
        }
        else if (mIcon.equals("snow")) {
            iconId = R.drawable.snow;
        }
        else if (mIcon.equals("sleet")) {
            iconId = R.drawable.sleet;
        }
        else if (mIcon.equals("wind")) {
            iconId = R.drawable.wind;
        }
        else if (mIcon.equals("fog")) {
            iconId = R.drawable.fog;
        }
        else if (mIcon.equals("cloudy")) {
            iconId = R.drawable.cloudy;
        }
        else if (mIcon.equals("partly-cloudy-day")) {
            iconId = R.drawable.partly_cloudy;
        }
        else if (mIcon.equals("partly-cloudy-night")) {
            iconId = R.drawable.cloudy_night;
        }
        return iconId;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public String getFormattedTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        Date dateTime = new Date(getTime() * 1000);
        return formatter.format(dateTime);

    }

    public double getTemperature() {
        return mTemperature;
    }

    public void setTemperature(double temperature) {
                    mTemperature = temperature;

    }
    public double convertFahrenheitToCelsius(double fahrenheit) {
        return ((fahrenheit - 32) * 5 / 9);
    }

    private double convertCelsiusToFahrenheit(double celsius) {
        return ((celsius * 9) / 5) + 32;
    }

    public double getPrecipChance() {
        return mPrecipChance;
    }

    public void setPrecipChance(double precipChance) {
        mPrecipChance = precipChance;
    }

    public double getHumidity() {
        return mHumidity;
    }

    public void setHumidity(double humidity) {
        mHumidity = humidity;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }



}
