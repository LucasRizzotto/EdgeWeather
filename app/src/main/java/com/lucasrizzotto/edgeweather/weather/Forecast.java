package com.lucasrizzotto.edgeweather.weather;

import com.lucasrizzotto.edgeweather.R;

public class Forecast {

    private Current mCurrent;
    private Day[] mDailyForecast;
    private Hour[] mHourlyForecast;
    private static String mTimeZone;


    public static String getTimeZone() {
        return mTimeZone;
    }
        public static void setTimeZone(String mTimeZone) {
        Forecast.mTimeZone = mTimeZone;
    }

    public Current getCurrent() {
        return mCurrent;
    }

    public void setCurrent(Current current) {
        mCurrent = current;
    }

    public Day[] getDailyForecast() {
        return mDailyForecast;
    }

    public void setDailyForecast(Day[] dailyForecast) {
        mDailyForecast = dailyForecast;
    }

    public Hour[] getHourlyForecast() {
        return mHourlyForecast;
    }
    public void setHourlyForecast(Hour[] hourlyForecast) {
        mHourlyForecast = hourlyForecast;
    }

    public static int getIconId(String iconString)
    {
        // Default icon
        int iconId = R.drawable.clear_day;

        if (iconString.equals("clear-day")) {
            iconId = R.drawable.clear_day;
        }
        else if (iconString.equals("clear-night")) {
            iconId = R.drawable.clear_night;
        }
        else if (iconString.equals("rain")) {
            iconId = R.drawable.rain;
        }
        else if (iconString.equals("snow")) {
            iconId = R.drawable.snow;
        }
        else if (iconString.equals("sleet")) {
            iconId = R.drawable.sleet;
        }
        else if (iconString.equals("wind")) {
            iconId = R.drawable.wind;
        }
        else if (iconString.equals("fog")) {
            iconId = R.drawable.fog;
        }
        else if (iconString.equals("cloudy")) {
            iconId = R.drawable.cloudy;
        }
        else if (iconString.equals("partly-cloudy-day")) {
            iconId = R.drawable.partly_cloudy;
        }
        else if (iconString.equals("partly-cloudy-night")) {
            iconId = R.drawable.cloudy_night;
        }

        return iconId;
    }


}
