package com.jamesc.stormcast.UI.Navigation;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

/**
 * Created by James on 3/02/2016.
 */
public class sideNavItem {
    public String Title;
    public Drawable Icon;

    public sideNavItem(String Title){
        this.Title = Title;
    }

    public static ArrayList<sideNavItem> fromStringArray(String [] stringArray){
        ArrayList<sideNavItem> sideNavItems = new ArrayList<sideNavItem>();
        for(String s: stringArray){
                sideNavItem newSideNavItem = new sideNavItem(s);
                sideNavItems.add(newSideNavItem);
        }
        return sideNavItems;
    }

}
