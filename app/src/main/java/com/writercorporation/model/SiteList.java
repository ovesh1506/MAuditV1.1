package com.writercorporation.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.writercorporation.maudit.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemina.shah on 3/16/2016.
 */
public class SiteList implements Parcelable {
    @Expose
    @SerializedName("SiteID")
    @DatabaseField(id = true)
    private int siteID;
    @Expose
    @SerializedName("SiteCode")
    @DatabaseField
    private String siteCode;


    @Expose
    @SerializedName("ATMCode")
    @DatabaseField
    private String atmCode;
    @Expose
    @SerializedName("SiteName")
    @DatabaseField
    private String siteName;
    @Expose
    @SerializedName("IsCallLogged")
    @DatabaseField
    private String siteIsLogged;
    @DatabaseField
    private String siteStatus;
    @Expose
    @SerializedName("LastAuditDate")
    @DatabaseField
    private String lstAuditDate;
    @Expose
    @SerializedName("SiteAddressLine1")
    @DatabaseField
    private String siteAddress;
    @DatabaseField
    private int siteColor= R.color.white;
    private String isVisit="";
    private int siteposition;
    /* @DatabaseField
     private String lastDateTime;*/
    @ForeignCollectionField(eager = false)
    private ForeignCollection<CompletedSiteInfo> completedSiteInfos;

    @ForeignCollectionField(eager = false)
    private ForeignCollection<VisitPurposeAnswer> VisitInfos;

    @SerializedName("IsBarcoded")
    @Expose
    @DatabaseField
    private String isBarcoded = "Y";

    public SiteList() {
    }

    public SiteList(int siteID, String siteCode, String atmCode,String siteName, String siteIsLogged, String siteStatus, String lstAuditDate, String siteAddress, String isBarcoded) {
        this.siteID = siteID;
        this.siteCode = siteCode;
        this.atmCode = atmCode;
        this.siteName = siteName;
        this.siteIsLogged = siteIsLogged;
        this.siteStatus = siteStatus;
        this.lstAuditDate = lstAuditDate;
        this.siteAddress = siteAddress;
        this.isBarcoded = isBarcoded;

    }


    public String getIsBarcoded() {
        return isBarcoded;
    }

    public void setIsBarcoded(String isBarcoded) {
        this.isBarcoded = isBarcoded;
    }

    public int getSitePosition() {
        return siteposition;
    }

    public void setSitePosition(int position) {
        this.siteposition = position;
    }

    public int getSiteColor() {
        return siteColor;
    }

    public void setSiteColor(int siteColor) {
        this.siteColor = siteColor;
    }

    public int getSiteID() {
        return siteID;
    }
    public SiteList setDiteID(int id)
    {
        this.siteID =id;
        return this;
    }
    public String getSiteCode() {
        return siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public String getSiteIsLogged() {
        if(siteIsLogged.equalsIgnoreCase("N"))
            return "";
        else
            return "Call log";
    }
    public List<CompletedSiteInfo> geCompleteSiteInfo() {
        ArrayList<CompletedSiteInfo> itemList = new ArrayList<>();
        for (CompletedSiteInfo item : completedSiteInfos) {
            itemList.add(item);
        }
        return itemList;
    }
    public String getSiteStatus() {
        return siteStatus;
    }

    public String getLstAuditDate() {
        return lstAuditDate;
    }

    public String getSiteAddress() {
        return siteAddress;
    }

    public String getAtmCode() {
        return atmCode;
    }

    protected SiteList(Parcel in) {
        siteCode = in.readString();
        atmCode = in.readString();
        siteName = in.readString();
        siteIsLogged = in.readString();
        siteStatus = in.readString();
        lstAuditDate = in.readString();
        siteAddress = in.readString();
        isBarcoded = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(siteCode);
        dest.writeString(atmCode);
        dest.writeString(siteName);
        dest.writeString(siteIsLogged);
        dest.writeString(siteStatus);
        dest.writeString(lstAuditDate);
        dest.writeString(siteAddress);
        dest.writeString(isBarcoded);
    }

    public String getIsVisit() {
        return isVisit;
    }
    public void setIsVisit(String isVisit)
    {
        this.isVisit=isVisit;
    }
    public ArrayList<SiteList> setIsVisit(ArrayList<SiteList> isVisit) {

        for(SiteList s:isVisit){
            s.setIsVisit("Visited");
        }
        return  isVisit;
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SiteList> CREATOR = new Parcelable.Creator<SiteList>() {
        @Override
        public SiteList createFromParcel(Parcel in) {
            return new SiteList(in);
        }

        @Override
        public SiteList[] newArray(int size) {
            return new SiteList[size];
        }
    };
}
