package com.jamesc.stormcast.Weather;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;

import com.jamesc.stormcast.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by James on 12/02/2016.
 */
public class Hour implements Parcelable {

    /**
     * Created by James on 5/02/2016.
     */

        private long mTime;
        private String mTimeZone;
        private boolean mWallpaperTone;
        private int mCardBarColor;
        int mIconColor;
        private String mSummary;
        private String mIcon;
        private int mIconId;
        private double mTemperature;
        private double mApparentTemperature;
        private double mPrecipChance;
        private double mHumidity;
        private int mCardColor;

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
        public Hour(){

        }

        public Hour(long Time, String TimeZone, String Summary, String Icon, double Temperature, double apparentTemperature){
            this.mTime = Time;
            this.mTimeZone = TimeZone;
            this.mSummary = Summary;
            this.mIcon = Icon;
            this.mTemperature = Temperature;
            this.mApparentTemperature = apparentTemperature;

        }

        public long getTime() {return mTime;}

        public String getFormattedHourlyTime(){
            SimpleDateFormat sdf = new SimpleDateFormat("h:mm a - E, d MMM yyyy");
            Date hourTime = new Date((mTime * 1000L));
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
            switch (mIcon) {
                case "clear-day":
                    mIconId = R.mipmap.clear_day_icon;
                    break;
                case "clear-night":
                    mIconId = R.drawable.clear_night_icon;
                    break;
                case "rain":
                    mIconId = R.drawable.rain;
                    break;
                case "snow":
                    mIconId = R.drawable.snow;
                    break;
                case "sleet":
                    mIconId = R.drawable.sleet;
                    break;
                case "wind":
                    mIconId = R.drawable.wind;
                    break;
                case "fog":
                    mIconId = R.drawable.fog;
                    break;
                case "cloudy":
                    mIconId = R.drawable.cloudy;
                    break;
                case "partly-cloudy-day":
                    mIconId = R.mipmap.partly_cloudy_icon;
                    break;
                case "partly-cloudy-night":
                    mIconId = R.drawable.cloudy_night;
                    break;
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
            switch (mIcon) {
                case "clear-day":
                    mCardColor = ContextCompat.getColor(context,R.color.clear_day);
                    mWallpaperTone = false;
                    break;
                case "clear-night":
                    mCardColor = ContextCompat.getColor(context, R.color.clear_night);
                    mWallpaperTone = false;
                    break;
                case "rain":
                    mCardColor = ContextCompat.getColor(context, R.color.rain);
                    mWallpaperTone = false;
                    break;
                case "snow":
                case "sleet":
                    mCardColor = ContextCompat.getColor(context, R.color.snow);
                    mWallpaperTone = true;
                    break;
                case "wind":
                    mCardColor = ContextCompat.getColor(context, R.color.wind);
                    mWallpaperTone = true;
                    break;
                case "cloudy":
                    mCardColor = ContextCompat.getColor(context, R.color.cloudy);
                    mWallpaperTone = true;
                    break;
                case "fog":
                case "partly-cloudy-day":
                    mCardColor = ContextCompat.getColor(context, R.color.partly_cloudy);
                    mWallpaperTone = true;
                    break;
                case "partly-cloudy-night":
                    mCardColor = ContextCompat.getColor(context, R.color.cloudy_night);
                    mWallpaperTone = false;
                    break;
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

        public int getCardBarColor(Context context) {
            if (mIcon.equals("clear-day")) {
                mCardBarColor = ContextCompat.getColor(context, R.color.clear_day_dark);
            }
            else if (mIcon.equals("clear-night")) {
                mCardBarColor = ContextCompat.getColor(context, R.color.clear_night_dark);
            }
            else if (mIcon.equals("rain")) {
                mCardBarColor = ContextCompat.getColor(context, R.color.rain_dark);
            }
            else if (mIcon.equals("snow") || mIcon.equals("sleet")) {
                mCardBarColor = ContextCompat.getColor(context, R.color.snow_dark);
            }
            else if (mIcon.equals("wind")) {
                mCardBarColor = ContextCompat.getColor(context, R.color.wind_dark);
            }
            else if (mIcon.equals("cloudy")) {
                mCardBarColor = ContextCompat.getColor(context, R.color.cloudy_dark);
            }
            else if (mIcon.equals("fog") || mIcon.equals("partly-cloudy-day")) {
                mCardBarColor = ContextCompat.getColor(context, R.color.partly_cloudy_dark);
            }
            else if (mIcon.equals("partly-cloudy-night")) {
                mCardBarColor = ContextCompat.getColor(context, R.color.cloudy_night_dark);
            }
            return mCardBarColor;
        }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mTime);
        dest.writeString(mTimeZone);
        dest.writeString(mSummary);
        dest.writeString(mIcon);
        dest.writeDouble(mTemperature);
        dest.writeDouble(mApparentTemperature);
    }
    private Hour(Parcel in){
        mTime = in.readLong();
        mTimeZone = in.readString();
        mSummary = in.readString();
        mIcon = in.readString();
        mTemperature = in.readDouble();
        mApparentTemperature = in.readDouble();

    }
    public static final Creator<Hour> CREATOR = new Creator<Hour>() {
        @Override
        public Hour createFromParcel(Parcel source) {
            return new Hour(source);
        }

        @Override
        public Hour[] newArray(int size) {
            return new Hour[size];
        }
    };
}



