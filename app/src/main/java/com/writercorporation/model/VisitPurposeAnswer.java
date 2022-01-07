package com.writercorporation.model;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by hemina.shah on 12/15/2016.
 */

public class VisitPurposeAnswer {
    @DatabaseField(generatedId = true)
    private int visiID;
    @DatabaseField
    private String jsonString;
    @DatabaseField
    private int siteId;
    @DatabaseField
    private String pTransId;
    @DatabaseField
    private String syncStatus="UNSYNC";
    @DatabaseField(foreign = true,foreignAutoCreate = true, foreignAutoRefresh = true)
    private SiteList siteList;


    public VisitPurposeAnswer()
    {}
    public VisitPurposeAnswer(String jsonString,int siteId,String pTransId)
    {
        this.jsonString=jsonString;
        this.siteId=siteId;
        this.pTransId=pTransId;
    }
    public VisitPurposeAnswer(String jsonString,int siteId,String pTransId,SiteList siteList)
    {
        this.jsonString=jsonString;
        this.siteId=siteId;
        this.pTransId=pTransId;
        this.siteList=siteList;
    }
    public int getVisiID() {
        return visiID;
    }

    public void setVisiID(int visiID) {
        this.visiID = visiID;
    }

    public String getJsonString() {
        return jsonString;
    }

    public VisitPurposeAnswer setJsonString(String jsonString) {
        this.jsonString = jsonString;
        return  this;
    }

    public int getSiteId() {
        return siteId;
    }
    public VisitPurposeAnswer setSiteList(SiteList siteList) {
        this.siteList = siteList;
        return this;
    }
    public VisitPurposeAnswer setSiteId(int siteId) {
        this.siteId = siteId;
        return  this;
    }

    public String getpTransId() {
        return pTransId;
    }

    public void setpTransId(String pTransId) {
        this.pTransId = pTransId;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public VisitPurposeAnswer setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
        return  this;
    }
}
