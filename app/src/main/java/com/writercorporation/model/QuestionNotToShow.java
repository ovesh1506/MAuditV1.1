package com.writercorporation.model;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by hemina.shah on 3/16/2016.
 */
public class QuestionNotToShow {
    @DatabaseField(generatedId = true)
    private int siteListID;
    @DatabaseField
    private int questionID;

    public int getSiteListID() {
        return siteListID;
    }

    public void setSiteListID(int siteListID) {
        this.siteListID = siteListID;
    }

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }
}
