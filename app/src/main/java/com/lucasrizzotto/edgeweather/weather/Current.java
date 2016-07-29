package com.lucasrizzotto.edgeweather.weather;

import com.lucasrizzotto.edgeweather.R;

import java.util.Calendar;

/**
 * Created by Magnopus on 7/12/2016.
 */
public class Current {

    public static final String TAG = Current.class.getSimpleName();

    private int mTemperature;
    private String mLocation;
    private String mIcon;

    public int getTemperature() {
        return mTemperature;
    }

    public void setTemperature(double temperature) {
        mTemperature = (int) Math.round(temperature);
    }

    // This will return an int with the relevant resource in the final version
    public String getBackgroundResource()
    {
        //int backgroundResource;
        String backgroundResource;

        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);

        // Checks hours and returns the appropriate background
        if(hour < 5) {
            // backgroundResource = R.drawable.BG_NIGHT;
            backgroundResource = "#2980b9";
        } else if (hour < 7) {
            // backgroundResource = R.drawable.BG_DAWN;
            backgroundResource = "#2980b9";
        } else if (hour < 17) {
            // backgroundResource = R.drawable.BG_DAY;
            backgroundResource = "#e67e22";
        } else if (hour < 19) {
            // backgroundResource = R.drawable.BG_DUSK;
            backgroundResource = "#2980b9";
        } else {
            // backgroundResource = R.drawable.BG_NIGHT;
            backgroundResource = "#2980b9";
        }

        return backgroundResource;

    }

    public String getLocation() {

        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public int getIconId(String icon) {
        return Forecast.getIconId(mIcon);
    }

    public int getMainIconResource() {

        int drawable;

        // Choosing the correct icon
        if (mIcon.equals("clear-day")) {
            drawable = R.drawable.main_clear_day;
        } else if (mIcon.equals("clear-night")){
            drawable = R.drawable.main_clear_night;
        } else if (mIcon.equals("partly-cloudy-day")){
            drawable = R.drawable.main_partly_cloudy_day;
        } else if (mIcon.equals("partly-cloudy-night")) {
            drawable = R.drawable.main_partly_cloudy_night;

        /*
            CONTENT TO BE MADE
        } else if (mIcon.equals("rain")){
            correctDrawable = R.drawable.main_rain;
        } else if (mIcon.equals("snow")){
            correctDrawable = R.drawable.main_snow;
        } else if (mIcon.equals("rain")){
            correctDrawable = R.drawable.main_rain;
        } else if (mIcon.equals("fog")){
            correctDrawable = R.drawable.main_rain;
        } else if (mIcon.equals("cloudy")){
            correctDrawable = R.drawable.main_rain;
        } else if (mIcon.equals("hail") || (mIcon.equals("thunderstorm") || (mIcon.equals("tornado"){
            correctDrawable = R.drawable.main_rain;
        }
        */

        } else {
            drawable = 0;
            // RETURN AN ERROR
        }

        return drawable;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

}
