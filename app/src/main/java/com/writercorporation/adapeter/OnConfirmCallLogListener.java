package com.writercorporation.adapeter;

import android.graphics.Bitmap;

import com.writercorporation.model.QuestionList;

import java.util.ArrayList;

/**
 * Created by hemina.shah on 20-Jun-16.
 */
public interface OnConfirmCallLogListener {

        void setImageListener( ArrayList<QuestionList> value,int pos,String path);

}
