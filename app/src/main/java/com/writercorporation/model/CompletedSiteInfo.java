package com.writercorporation.model;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * Created by amol.tate on 4/27/2016.
 */
public class CompletedSiteInfo   implements Serializable {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String jsonString;
    @DatabaseField
    private String syncStatus = "UNSYNC";
    @DatabaseField
    private int siteID;
    @DatabaseField(foreign = true,foreignAutoCreate = true, foreignAutoRefresh = true)
    private SiteList siteList;

    public CompletedSiteInfo(String jsonString,int siteID) {
        this.siteID=siteID;
        this.jsonString = jsonString;
    }
    public  CompletedSiteInfo(String jsonString,int siteID,SiteList siteList) {
        this.siteID=siteID;
        this.jsonString = jsonString;
        this.siteList=siteList;
    }

    public int getSiteID() {
        return siteID;
    }

    public void setSiteID(int siteID) {
        this.siteID = siteID;
    }
    public SiteList getSubCategoryList() {
        return siteList;
    }
    public CompletedSiteInfo setSiteList(SiteList siteList) {
        this.siteList = siteList;
        return this;
    }

    public CompletedSiteInfo() {
    }

    public int getId() {
        return id;
    }

    public CompletedSiteInfo setId(int id) {
        this.id = id;
        return this;
    }

    public String getJsonString() {
        return jsonString;
    }

    public CompletedSiteInfo setJsonString(String jsonString) {
        this.jsonString = jsonString;
        return this;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public CompletedSiteInfo setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
        return this;
    }
}
