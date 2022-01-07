package com.writercorporation.model;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.j256.ormlite.field.DatabaseField;
import com.writercorporation.maudit.R;

import java.io.Serializable;

/**
 * Created by hemina.shah on 3/16/2016.
 */
public class CallLoggedList implements Serializable{

    @DatabaseField(generatedId = true)
    private int callLogID;
    @DatabaseField
    private String serverCallLogID;
    @DatabaseField
    private int siteID;
    @DatabaseField
    private int questionID;
    @DatabaseField
    private String callLogDate;
    private int color= R.color.white;
    @DatabaseField
    private String callLogStatus=null;

    @DatabaseField
    private String callLogImage;
    private String QuestionText;

    private String isButtonClick=null;
    private boolean isflag=false;
    private int postion=-1;
    private Bitmap BitmapValue=null;
    public CallLoggedList()
    {}
    public CallLoggedList(String isButtonClick,boolean isflag,int postion){
        this.isButtonClick=isButtonClick;
        this.isflag=isflag;
        this.postion=postion;
    }

    public CallLoggedList(int siteID,String serverCallLogID,int questionID,String callLogDate,String callLogStatus)
    {
        this.siteID=siteID;
        this.serverCallLogID=serverCallLogID;
        this.questionID=questionID;
        this.callLogDate=callLogDate;

        this.callLogStatus=callLogStatus;
    }
    public CallLoggedList(String questionText)
    {
        this.QuestionText=questionText;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getQuestionText() {
        return QuestionText;
    }

    public void setQuestionText(String questionText) {
        QuestionText = questionText;
    }

    public String getServerCallLogID() {
        return serverCallLogID;
    }

    public void setServerCallLogID(String serverCallLogID) {
        this.serverCallLogID = serverCallLogID;
    }



    public int getCallLogID() {
        return callLogID;
    }

    public void setCallLogID(int callLogID) {
        this.callLogID = callLogID;
    }

    public Bitmap getBitmapValue() {
        return BitmapValue;
    }

    public void setBitmapValue(Bitmap bitmapValue) {
        BitmapValue = bitmapValue;
    }

    public int getSiteID() {
        return siteID;
    }

    public void setSiteID(int siteID) {
        this.siteID = siteID;
    }

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public String getCallLogStatus() {
        return callLogStatus;
    }

    public void setCallLogStatus(String callLogStatus) {
        this.callLogStatus = callLogStatus;
    }

    public String getCallLogDate() {
        return callLogDate;
    }

    public void setCallLogDate(String callLogDate) {
        this.callLogDate = callLogDate;
    }

    public String getImage() {
        return callLogImage;
    }

    public void setImage(String image) {
        this.callLogImage = image;
    }

    public String getIsButtonClick() {
        return isButtonClick;
    }

    public void setIsButtonClick(String isButtonClick) {
        this.isButtonClick = isButtonClick;
    }

    public boolean isflag() {
        return isflag;
    }

    public void setIsflag(boolean isflag) {
        this.isflag = isflag;
    }

    public int getPostion() {
        return postion;
    }

    public void setPostion(int postion) {
        this.postion = postion;
    }
}
