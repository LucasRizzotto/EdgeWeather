package com.lucasrizzotto.edgeweather.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lucasrizzotto.edgeweather.R;
import com.lucasrizzotto.edgeweather.weather.Day;

public class DayAdapter extends BaseAdapter {

    private Context mContext;
    private Day[] mDays;

    // Constructor for the Adapter, taking the context and an array of days as an input
    public DayAdapter(Context context, Day[] days) {
        mContext = context;
        mDays = days;
    }

    // Gets us the count of the array this adapter is using
    @Override
    public int getCount() {
        return mDays.length;
    }

    // Gets item on a specific position
    @Override
    public Object getItem(int position) {
        return mDays[position];
    }


    // Called for each item in the list, it's where we'll set the data to the adapter
    /*
        We will be using a helper class called ViewHolder, that once associated with a view
        It lets us reuse the same references to objects in the view, like Textviews and Imageviews
        It helps us save memory and it's considered a best practice
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        // The first time getView is called, convertView will be null
        // This is the object that will be reused multiple times to create the list
        // If it's not null, all we have to do is reset the data ("Recycling")

        if(convertView == null) {

            // Inflate this view from the context using a layout inflator
            // A layout inflator takes XML layouts and turns them into views in code we can use
            convertView = LayoutInflater.from(mContext).inflate(R.layout.daily_list_item, null);

            holder = new ViewHolder();
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.iconImageView);
            holder.minTemperatureLabelTextView = (TextView) convertView.findViewById(R.id.minTemperatureLabelTextView);
            holder.maxTemperatureLabelTextView = (TextView) convertView.findViewById(R.id.maxTemperatureLabelTextView);
            holder.dayTextView = (TextView) convertView.findViewById(R.id.dayTextView);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Picks a day from our mDays days array, transmitting the data to the holder variables
        Day day = mDays[position];
        holder.iconImageView.setImageResource(day.getIconId());
        holder.maxTemperatureLabelTextView.setText(day.getTemperatureMax() + "");
        holder.minTemperatureLabelTextView.setText(day.getTemperatureMin() + "");
        holder.dayTextView.setText(day.getDayOfTheWeek());

        return convertView;
    }

    // Nested Viewholder class, it holds the views in our layout
    private static class ViewHolder {
        ImageView iconImageView;
        TextView minTemperatureLabelTextView;
        TextView maxTemperatureLabelTextView;
        TextView dayTextView;
    }







    // Can be used for tagging for easy reference
    @Override
    public long getItemId(int position) {
        return 0;
    }
}

