package com.writercorporation.model;

import java.util.Comparator;

/**
 * Created by hemina.shah on 12/20/2016.
 */

public class SiteListCompare implements Comparator<SiteList> {
    @Override
    public int compare(SiteList e1, SiteList e2) {
        if(e1.getSiteID() == e2.getSiteID()){
            return 0;
        } if(e1.getSiteID() < e2.getSiteID()){
            return 1;
        } else {
            return -1;
        }




    }
}
