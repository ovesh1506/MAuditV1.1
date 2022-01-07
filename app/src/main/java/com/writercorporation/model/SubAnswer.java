package com.writercorporation.model;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by hemina.shah on 3/16/2016.
 */
public class SubAnswer {
    @DatabaseField(generatedId = true)
    private int answerId;
    @DatabaseField
    private int questionID;

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }
}
