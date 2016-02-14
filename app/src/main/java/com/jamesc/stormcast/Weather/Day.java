package com.jamesc.stormcast.Weather;

/**
 * Created by James on 12/02/2016.
 */
public class Day {
    private long mTime;
    private String mTimeZone;
    private String mIcon;
    private String mSummary;
    private int mIconId;
    private double mTemperature;
    private double mApparentTemperature;

    public Day(long Time, String TimeZone, String Summary, String Icon, double Temperature, double apparentTemperature){
        this.mTime = Time;
        this.mTimeZone = TimeZone;
        this.mSummary = Summary;
        this.mIcon = Icon;
        this.mTemperature = Temperature;
        this.mApparentTemperature = apparentTemperature;

    }

    public double getApparentTemperature() {
        return mApparentTemperature;
    }

    public void setApparentTemperature(double apparentTemperature) {
        mApparentTemperature = apparentTemperature;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public double getTemperature() {
        return mTemperature;
    }

    public void setTemperature(double temperature) {
        mTemperature = temperature;
    }

    public int getIconId() {
        return mIconId;
    }

    public void setIconId(int iconId) {
        mIconId = iconId;
    }

}
