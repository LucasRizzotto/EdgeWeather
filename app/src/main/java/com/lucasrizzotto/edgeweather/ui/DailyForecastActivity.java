package com.lucasrizzotto.edgeweather.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Parcelable;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.lucasrizzotto.edgeweather.R;
import com.lucasrizzotto.edgeweather.adapters.DayAdapter;
import com.lucasrizzotto.edgeweather.weather.Day;

import java.util.Arrays;

public class DailyForecastActivity extends ListActivity { // Use a regular Activitiy here if it's a shared activity

    private Day[] mDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);

        Intent intent = getIntent();
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.DAILY_FORECAST);

        // Special method that copies one array into another
        mDays = Arrays.copyOf(parcelables, parcelables.length, Day[].class);
        DayAdapter adapter = new DayAdapter(this, mDays);
        setListAdapter(adapter);

    }


    // Detects clicks on each list item
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Toast.makeText(DailyForecastActivity.this, "Click detected on position " + position, Toast.LENGTH_SHORT).show();
        String dayOfTheWeek = mDays[position].getDayOfTheWeek();

    }
}
