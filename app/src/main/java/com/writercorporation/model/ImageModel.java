package com.writercorporation.model;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by hemina.shah on 4/28/2016.
 */
public class ImageModel {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    String jsonString;
    @DatabaseField
    String syncStatus="UNSYNC";
    @DatabaseField
    String pTransId;
    @DatabaseField
    int siteId;



    public ImageModel(){}


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJsonString() {
        return jsonString;
    }

    public ImageModel setJsonString(String jsonString) {
        this.jsonString = jsonString;
        return this;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public ImageModel setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
        return this;
    }

    public String getpTransId() {
        return pTransId;
    }

    public ImageModel setpTransId(String pTransId) {
        this.pTransId = pTransId;
        return this;
    }

    public int getSiteId() {
        return siteId;
    }

    public ImageModel setSiteId(int siteId) {
        this.siteId = siteId;
        return this;
    }
}
