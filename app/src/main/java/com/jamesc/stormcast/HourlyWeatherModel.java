package com.jamesc.stormcast;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by James on 5/02/2016.
 */
public class HourlyWeatherModel {
    private long mTime;
    private int mIconColour;
    private boolean mWallpaperTone;

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public double getApparentTemperature() {
        return mApparentTemperature;
    }

    public void setApparentTemperature(double apparentTemperature) {
        mApparentTemperature = apparentTemperature;
    }
    int mIconColor;
    private String mSummary;
    private String mIcon;
    private int mIconId;
    private double mTemperature;
    private double mApparentTemperature;
    private double mPrecipChance;
    private double mHumidity;
    private int mCardColor;

    public HourlyWeatherModel(long Time, String Summary, String Icon, double Temperature, double apparentTemperature){
            this.mTime = Time;
            this.mSummary = Summary;
            this.mIcon = Icon;
            this.mTemperature = Temperature;
            this.mApparentTemperature = apparentTemperature;

    }

    public long getTime() {


        return mTime;
    }
    public String getFormattedTime(long time){
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
        Date hourTime = new Date((time * 1000L));
        String stringFormattedTime = sdf.format(hourTime);
        return stringFormattedTime;

    }

    public void setTime(long time) {
        mTime = time;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public int getIconId() {
        if (mIcon.equals("clear-day")) {
            mIconId = R.drawable.clear_day_icon;
        }
        else if (mIcon.equals("clear-night")) {
            mIconId = R.drawable.clear_night;
        }
        else if (mIcon.equals("rain")) {
            mIconId = R.drawable.rain;
        }
        else if (mIcon.equals("snow")) {
            mIconId = R.drawable.snow;
        }
        else if (mIcon.equals("sleet")) {
            mIconId = R.drawable.sleet;
        }
        else if (mIcon.equals("wind")) {
            mIconId = R.drawable.wind;
        }
        else if (mIcon.equals("fog")) {
            mIconId = R.drawable.fog;
        }
        else if (mIcon.equals("cloudy")) {
            mIconId = R.drawable.cloudy;
        }
        else if (mIcon.equals("partly-cloudy-day")) {
            mIconId = R.drawable.partly_cloudy;
        }
        else if (mIcon.equals("partly-cloudy-night")) {
            mIconId = R.drawable.cloudy_night;
        }
        return mIconId;
    }

    public void setIconId(int iconId) {
        mIconId = iconId;
    }

    public double getTemperature() {
        return mTemperature;
    }

    public double convertFahrenheitToCelsius(double temperature) {
        return ((temperature- 32) * 5 / 9);
    }

    public void setTemperature(double temperature) {
        mTemperature = temperature;
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

    public int getCardColor(Context context) {
        if (mIcon.equals("clear-day")) {
            mCardColor = ContextCompat.getColor(context, R.color.clear_day);
            mWallpaperTone = false;
        }
        else if (mIcon.equals("clear-night")) {
            mCardColor = ContextCompat.getColor(context, R.color.clear_night);
            mWallpaperTone = false;
        }
        else if (mIcon.equals("rain")) {
            mCardColor = ContextCompat.getColor(context, R.color.rain);
            mWallpaperTone = false;
        }
        else if (mIcon.equals("snow") || mIcon.equals("sleet")) {
            mCardColor = ContextCompat.getColor(context, R.color.snow);
            mWallpaperTone = true;
        }
        else if (mIcon.equals("wind")) {
            mCardColor = ContextCompat.getColor(context,R.color.wind);
            mWallpaperTone = true;
        }
        else if (mIcon.equals("cloudy")) {
            mCardColor = ContextCompat.getColor(context,R.color.cloudy);
            mWallpaperTone = true;
        }
        else if (mIcon.equals("fog") || mIcon.equals("partly-cloudy-day")) {
            mCardColor = ContextCompat.getColor(context,R.color.partly_cloudy);
            mWallpaperTone = true;
        }
        else if (mIcon.equals("partly-cloudy-night")) {
            mCardColor = ContextCompat.getColor(context,R.color.cloudy_night);
            mWallpaperTone = false;
        }
        return mCardColor;
    }

    public void setCardColor(int cardColor) {
        mCardColor = cardColor;
    }

    public int getIconColor(Context context) {

        if (mIcon.equals("clear-day")) {
            mIconColor = ContextCompat.getColor(context, R.color.clear_day_icon);
        }
        else if (mIcon.equals("clear-night")) {
            mIconColor = ContextCompat.getColor(context, R.color.clear_night_icon);
        }
        else if (mIcon.equals("rain")) {
            mIconColor = ContextCompat.getColor(context, R.color.rain_icon);
        }
        else if (mIcon.equals("snow") || mIcon.equals("sleet")) {
            mIconColor = ContextCompat.getColor(context, R.color.snow_icon);
        }
        else if (mIcon.equals("wind")) {
            mIconColor = ContextCompat.getColor(context,R.color.wind_icon);
        }
        else if (mIcon.equals("cloudy")) {
            mIconColor = ContextCompat.getColor(context,R.color.cloudy_icon);
        }
        else if (mIcon.equals("fog") || mIcon.equals("partly-cloudy-day")) {
            mIconColor = ContextCompat.getColor(context,R.color.partly_cloudy_icon);
        }
        else if (mIcon.equals("partly-cloudy-night")) {
            mIconColor = ContextCompat.getColor(context,R.color.cloudy_night_icon);
        }
        return mIconColor;
    }

    public boolean getWallpaperTone() {
        //True for Light..... False for Dark

        return mWallpaperTone;
    }
}
