package com.lucasrizzotto.edgeweather.weather;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Magnopus on 7/27/2016.
 */
public class Hour implements Parcelable {

    private long mTime;
    private double mTemperature;
    private String mIcon;
    private String mTimeZone;
    private String mSummary;

    public Hour() {}

    public String getTimeZone() { return mTimeZone;}
    public void setTimeZone(String timeZone) { mTimeZone = timeZone; }

    public String getSummary() { return mSummary; }
    public void setSummary(String summary) { mSummary = summary; }

    public long getTime() { return mTime; }
    public void setTime(long time) { mTime = time; }

    public String getIcon() { return mIcon; }
    public void setIcon(String icon) { mIcon = icon; }

    public int getTemperature() { return (int)Math.round(mTemperature); }
    public void setTemperature(double temperature) { mTemperature = temperature; }

    public String getHour() {
        SimpleDateFormat formatter = new SimpleDateFormat("h a");
        Date date = new Date(mTime * 1000);
        return formatter.format(date);
    }

    public int getIconId() {
        return Forecast.getIconId(mIcon);
    }

    @Override
    public int describeContents() {
        return 0; // IGNORE
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mTime);
        dest.writeDouble(mTemperature);
        dest.writeString(mIcon);
        dest.writeString(mSummary);
        dest.writeString(mTimeZone);
    }

    private Hour(Parcel in) {
        mTime = in.readLong();
        mTemperature = in.readDouble();
        mIcon = in.readString();
        mSummary = in.readString();
        mTimeZone = in.readString();
    }

    public static final Creator<Hour> CREATOR = new Creator<Hour>() {
        @Override
        public Hour createFromParcel(Parcel in) {
            return new Hour(in);
        }

        @Override
        public Hour[] newArray(int size) {
            return new Hour[size];
        }
    };
}
