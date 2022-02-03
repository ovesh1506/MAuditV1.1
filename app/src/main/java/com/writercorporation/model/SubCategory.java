package com.writercorporation.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amol.tate on 3/16/2016.
 */
public class SubCategory {
    public static final String CATEGORY_ID_FIELD_NAME = "categoryServerId";

    @SerializedName("AuditCategoryID")
    @Expose
    private int subCategoryAuditId;

    @SerializedName("CheckpointGroupID")
    @Expose
    @DatabaseField(id = true)
    private int subCategoryServerId;

    @SerializedName("CheckpointGroupDesc")
    @Expose
    @DatabaseField
    private String subCategoryName;

    @SerializedName("CheckpointGroupOrder")
    @Expose
    @DatabaseField
    private String subCategoryOrder;

    @ForeignCollectionField
    private ForeignCollection<MicroCategory> microCategories;


    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private CategoryList categoryList;


    public int getId() {
        return subCategoryServerId;
    }

    public SubCategory(){
        //No-arg constructor
    }
    public SubCategory(int subCategoryServerId, String subCategoryName, CategoryList categoryList, String subCategoryOrder) {
        this.subCategoryServerId = subCategoryServerId;
        this.subCategoryName = subCategoryName;
        this.categoryList = categoryList;
        this.subCategoryOrder = subCategoryOrder;
    }

    public SubCategory setId(int subCategoryServerId) {
        this.subCategoryServerId = subCategoryServerId;
        return this;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public SubCategory setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
        return this;
    }

    public CategoryList getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(CategoryList categoryList) {
        this.categoryList = categoryList;
        return;
    }

    public List<MicroCategory> getMicroCategories() {
        ArrayList<MicroCategory> itemList = new ArrayList<>();
        for (MicroCategory item : microCategories) {
            itemList.add(item);
        }
        return itemList;
    }

    public int getSubCategoryAuditId() {
        return subCategoryAuditId;
    }

    public void setSubCategoryAuditId(int subCategoryAuditId) {
        this.subCategoryAuditId = subCategoryAuditId;
    }
}
