package com.writercorporation.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by amol.tate on 12-12-2016.
 */
@DatabaseTable
public class VisitPurposeModel {
    @DatabaseField(generatedId = true)
    private int id;

    @Expose
    @SerializedName("ControlCaption")
    @DatabaseField
    private String ControlCaption;

    @Expose
    @SerializedName("ControlType")
    @DatabaseField
    private String ControlType;
    @Expose
    @SerializedName("ControlValues")
    @DatabaseField
    private String ControlValues;

    @Expose
    @SerializedName("PurposeDesc")
    @DatabaseField
    private String PurposeDesc;

    @Expose
    @SerializedName("PurposeID")
    @DatabaseField
    private String PurposeID;



    public VisitPurposeModel()
    {}
    public VisitPurposeModel(String ControlCaption, String ControlType,String  ControlValues,String PurposeDesc,String PurposeID)
    {
        this.ControlCaption=ControlCaption;
        this.ControlType=ControlType;
        this.ControlValues=ControlValues;
        this.PurposeDesc=PurposeDesc;
        this.PurposeID=PurposeID;
    }
    public String getControlCaption() {
        return ControlCaption;
    }

    public void setControlCaption(String controlCaption) {
        ControlCaption = controlCaption;
    }

    public String getControlType() {
        return ControlType;
    }

    public void setControlType(String controlType) {
        ControlType = controlType;
    }

    public String getControlValues() {
        return ControlValues;
    }

    public void setControlValues(String controlValues) {
        ControlValues = controlValues;
    }

    public String getPurposeDesc() {
        return PurposeDesc;
    }

    public void setPurposeDesc(String purposeDesc) {
        PurposeDesc = purposeDesc;
    }

    public String getPurposeID() {
        return PurposeID;
    }

    public void setPurposeID(String purposeID) {
        PurposeID = purposeID;
    }
}
