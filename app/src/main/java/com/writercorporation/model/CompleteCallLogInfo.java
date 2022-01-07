package com.writercorporation.model;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by hemina.shah on 5/16/2016.
 */
public class CompleteCallLogInfo {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String jsonString;
    @DatabaseField
    private String syncStatus = "UNSYNC";
    @DatabaseField
    private int siteID;

    public  CompleteCallLogInfo()
    {}
    public CompleteCallLogInfo(String jsonString,int siteID)
    {
        this.jsonString = jsonString;
        this.siteID=siteID;
    }

    public int getSiteID() {
        return siteID;
    }

    public void setSiteID(int siteID) {
        this.siteID = siteID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }
}
