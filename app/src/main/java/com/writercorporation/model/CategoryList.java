package com.writercorporation.model;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.writercorporation.maudit.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amol.tate on 3/16/2016.
 */
public class CategoryList {

    @Expose
    @DatabaseField
    private String categoryName;
    @Expose
    @DatabaseField(id = true)
    private int categoryServerId;



    @ForeignCollectionField
    private ForeignCollection<SubCategory> subCategories;
    @Expose
    int imageResource = R.mipmap.arrow;
    @Expose
    boolean isCompleted = false;

    public String getCategoryName() {
        return categoryName;
    }

    public CategoryList setCategoryName(String categoryName) {
        this.categoryName = categoryName;
        return this;
    }

    public int getCategoryServerId() {
        return categoryServerId;
    }

    public CategoryList setCategoryServerId(int categoryServerId) {
        this.categoryServerId = categoryServerId;
        return this;
    }

    public List<SubCategory> getSubCategories() {
        ArrayList<SubCategory> itemList = new ArrayList<SubCategory>();
        for (SubCategory item : subCategories) {
            itemList.add(item);
        }
        return itemList;
    }

    public int getImageResource() {
        return imageResource;
    }

    public CategoryList setImageResource(int imageResource) {
        this.imageResource = imageResource;
        return this;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public CategoryList setIsCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
        return this;
    }
}
