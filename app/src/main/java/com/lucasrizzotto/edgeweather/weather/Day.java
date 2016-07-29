package com.lucasrizzotto.edgeweather.weather;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Day implements Parcelable {

    private String mIcon;
    private String mSummary;
    private double mTemperatureMin;
    private double mTemperatureMax;
    private long mTime;

    public Day() {}

    public void setTemperatureMax(double temperatureMax) { mTemperatureMax = temperatureMax; }
    public int getTemperatureMax() { return (int)Math.round(mTemperatureMax); }

    public void setTemperatureMin(double temperatureMin) { mTemperatureMin = temperatureMin; }
    public int getTemperatureMin() { return (int)Math.round(mTemperatureMin); }

    public String getSummary() { return mSummary; }
    public void setSummary(String summary) { mSummary = summary; }

    public int getIconId() {
        return Forecast.getIconId(mIcon);
    }
    public void setIcon(String icon) { mIcon = icon; }

    public String getDayOfTheWeek() {
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
        formatter.setTimeZone(TimeZone.getTimeZone(Forecast.getTimeZone()));
        Date dateTime = new Date(getTime() * 1000);
        return formatter.format(dateTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mSummary);
        dest.writeString(mIcon);
        dest.writeDouble(mTemperatureMax);
        dest.writeDouble(mTemperatureMin);
        dest.writeLong(mTime);
    }

    private Day(Parcel in){
        mSummary = in.readString();
        mIcon = in.readString();
        mTemperatureMax = in.readDouble();
        mTemperatureMin = in.readDouble();
        mTime = in.readLong();
    }

    public static final Creator<Day> CREATOR = new Creator<Day>(){
        @Override
        public Day createFromParcel(Parcel source) {
            return new Day(source);
        }

        @Override
        public Day[] newArray(int size) {
            return new Day[size];
        }
    };

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }
}
