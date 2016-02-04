package com.jamesc.stormcast;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by James on 3/02/2016.
 */
public class sideNavAdapter extends ArrayAdapter<sideNavItem> {
    public sideNavAdapter(Context context, ArrayList<sideNavItem> sideNavItems) {
        super(context, 0, sideNavItems);
    }

    public static ArrayList<sideNavItem> fromStringArray(String [] stringArray){
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
        if(sideNavItem.Title.equals("Settings")){
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_item_2, parent, false);
            }
            // Lookup view for data population
            final TextView tvCustom = (TextView)convertView.findViewById(R.id.action_settings);

            // Populate the data into the template view using the data object
            tvCustom.setText(sideNavItem.Title);
        }else{
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_item_1, parent, false);
        }
        // Lookup view for data population
        TextView tvCustom= (TextView) convertView.findViewById(R.id.custom_list_item_tv);
        // Populate the data into the template view using the data object
        tvCustom.setText(sideNavItem.Title);
        }
        // Return the completed view to render on screen
        return convertView;
    }
}
