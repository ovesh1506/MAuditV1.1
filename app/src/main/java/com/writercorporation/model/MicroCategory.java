package com.writercorporation.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by amol.tate on 3/16/2016.
 */
public class MicroCategory implements Serializable {
    @DatabaseField(id = true)
    private int microCategoryServerId;

    @DatabaseField
    private String microCategoryName;

    @DatabaseField
    private int microCategoryOrder;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private SubCategory subCategoryList;

    @ForeignCollectionField
    private ForeignCollection<QuestionList> questionListForeignCollection;




    public MicroCategory() {
    }

    public MicroCategory(int microCategoryServerId, String microCategoryName, int microCategoryOrder, SubCategory subCategoryList) {
        this.microCategoryServerId = microCategoryServerId;
        this.microCategoryName = microCategoryName;
        this.microCategoryOrder = microCategoryOrder;
        this.subCategoryList = subCategoryList;
    }

    public int getId() {
        return microCategoryServerId;
    }

    public MicroCategory setId(int id) {
        this.microCategoryServerId = id;
        return this;
    }

    public String getMicroCategoryName() {
        return microCategoryName;
    }

    public MicroCategory setMicroCategoryName(String microCategoryName) {
        this.microCategoryName = microCategoryName;
        return this;
    }




    public SubCategory getSubCategoryList() {
        return subCategoryList;
    }

    public List<QuestionList> getQuestionList() {
        ArrayList<QuestionList> questionList = new ArrayList<>();
        for (QuestionList question : questionListForeignCollection) {
            questionList.add(question);
        }
        return questionList;
    }

    public int getMicroCategoryServerId() {
        return microCategoryServerId;
    }

    public MicroCategory setMicroCategoryServerId(int microCategoryServerId) {
        this.microCategoryServerId = microCategoryServerId;
        return this;
    }

    public int getMicroCategoryOrder() {
        return microCategoryOrder;
    }

    public MicroCategory setMicroCategoryOrder(int microCategoryOrder) {
        this.microCategoryOrder = microCategoryOrder;
        return this;
    }

    public MicroCategory setSubCategoryList(SubCategory subCategoryList) {
        this.subCategoryList = subCategoryList;
        return this;
    }

    public ForeignCollection<QuestionList> getQuestionListForeignCollection() {
        return questionListForeignCollection;
    }

    public MicroCategory setQuestionListForeignCollection(ForeignCollection<QuestionList> questionListForeignCollection) {
        this.questionListForeignCollection = questionListForeignCollection;
        return this;
    }
}
