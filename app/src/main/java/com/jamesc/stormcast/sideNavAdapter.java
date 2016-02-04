package com.jamesc.stormcast;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by James on 3/02/2016.
 */
public class sideNavAdapter extends ArrayAdapter<sideNavItem> {
    BitmapDrawable bmCustom;
    TextView tvCustom;
    public sideNavAdapter(Context context, ArrayList<sideNavItem> sideNavItems) {
        super(context, 0, sideNavItems);
    }

    public static ArrayList<sideNavItem> fromStringArray(String[] stringArray) {
        ArrayList<sideNavItem> sideNavItems = new ArrayList<sideNavItem>();
        for (String s : stringArray) {
            sideNavItems.add(new sideNavItem(s));
        }
        return sideNavItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        sideNavItem sideNavItem = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                if (sideNavItem.Title.equals("Settings")) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_item_2, parent, false);
                    tvCustom = (TextView)convertView.findViewById(R.id.action_settings);
                    tvCustom.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.side_nav_separator));
                } else {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_item_1, parent, false);
                    tvCustom = (TextView) convertView.findViewById(R.id.custom_list_item_tv);
                }
                // Lookup view for data population

                ImageView ivCustom = (ImageView) convertView.findViewById(R.id.side_nav_item_icon);
                // Populate the data into the template view using the data object
                switch (sideNavItem.Title) {
                    case "Settings":
                        bmCustom = (BitmapDrawable) ContextCompat.getDrawable(getContext(), R.drawable.ic_settings_icon);
                        break;
                    case "Weekly Forecast":
                        bmCustom = (BitmapDrawable) ContextCompat.getDrawable(getContext(), R.drawable.ic_weekly_weather_icon);
                        break;
                    case "Hourly Forecast":
                        bmCustom = (BitmapDrawable) ContextCompat.getDrawable(getContext(), R.drawable.ic_hourly_weather_icon);
                        break;
                    case "Current Weather":
                        bmCustom = (BitmapDrawable) ContextCompat.getDrawable(getContext(), R.drawable.ic_current_weather_icon);
                        break;
                }
                tvCustom.setText(sideNavItem.Title);

                ivCustom.setImageDrawable(bmCustom);
            }

            // Return the completed view to render on screen
            return convertView;
        }

}