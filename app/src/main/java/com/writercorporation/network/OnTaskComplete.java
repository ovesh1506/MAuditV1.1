package com.writercorporation.network;

/**
 * Created by amol.tate on 9/29/2015.
 */
public interface OnTaskComplete {
     String onTaskComplete(String result);
     void onTaskComplete(String result,int id);
     void dismissDialog(String result);
     void onPublishResult(String msg);

}
