package com.writercorporation.model;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by hemina.shah on 3/16/2016.
 */
public class QuestionRequiredField {
    @DatabaseField(generatedId = true)
    private int questionId;
    @DatabaseField
    private String requriedFieldname;
    @DatabaseField
    private String requriedFieldType;

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getRequriedFieldname() {
        return requriedFieldname;
    }

    public void setRequriedFieldname(String requriedFieldname) {
        this.requriedFieldname = requriedFieldname;
    }

    public String getRequriedFieldType() {
        return requriedFieldType;
    }

    public void setRequriedFieldType(String requriedFieldType) {
        this.requriedFieldType = requriedFieldType;
    }
}
