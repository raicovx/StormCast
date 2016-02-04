package com.jamesc.stormcast;

import java.util.ArrayList;

/**
 * Created by James on 3/02/2016.
 */
public class sideNavItem {
    public String Title;

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
