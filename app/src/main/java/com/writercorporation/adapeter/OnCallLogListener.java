package com.writercorporation.adapeter;

import android.widget.ImageView;

import com.writercorporation.model.CallLoggedList;

/**
 * Created by hemina.shah on 5/6/2016.
 */
public interface OnCallLogListener {
    void setOnCallLogListener(CallLoggedList callLoggedList,int position,String buttonClick);

}
