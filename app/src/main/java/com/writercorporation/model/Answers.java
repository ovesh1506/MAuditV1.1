package com.writercorporation.model;

import android.widget.EditText;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by amol.tate on 3/16/2016.
 */
public class Answers {

    @DatabaseField(id = true)
    private int id;
    @DatabaseField
    private String answerText;
    @DatabaseField
    private String callLog;
    @DatabaseField
    private String controlCaption;
    @DatabaseField
    private String controlType;
    @DatabaseField
    private String textInputType;
    @DatabaseField
    private String isChildShow;
    @DatabaseField
    private String controlValue;
    @DatabaseField
    private String childValue;
    private EditText externalValue;
    public Answers() {
    }

    public Answers(int id, String answerText, String callLog, String controlCaption, String controlType, String textInputType, String isChildShow, String controlValue, QuestionList questionList) {
        this.id = id;
        this.answerText = answerText;
        this.callLog = callLog;
        this.controlCaption = controlCaption;
        this.controlType = controlType;
        this.textInputType = textInputType;
        this.isChildShow = isChildShow;
        this.controlValue = controlValue;
        this.questionList = questionList;
    }

    @DatabaseField(foreign = true,foreignAutoRefresh = true)
    private QuestionList questionList;

    public int getId() {
        return id;
    }

    public Answers setId(int id) {
        this.id = id;
        return this;
    }

    public EditText getExternalValue() {
        return externalValue;
    }

    public void setExternalValue(EditText externalValue) {
        this.externalValue = externalValue;
    }

    public String getAnswerText() {
        return answerText;
    }

    public Answers setAnswerText(String answerText) {
        this.answerText = answerText;
        return this;
    }

    public String getChildValue() {
        return childValue;
    }

    public void setChildValue(String childValue) {
        this.childValue = childValue;
    }

    public String getCallLog() {
        return callLog;
    }

    public String getTextInputType() {
        return textInputType;
    }

    public void setTextInputType(String textInputType) {
        this.textInputType = textInputType;
    }

    public Answers setCallLog(String callLog) {
        this.callLog = callLog;
        return this;

    }


    public String getControlCaption() {
        return controlCaption;
    }

    public Answers setControlCaption(String controlCaption) {
        this.controlCaption = controlCaption;
        return this;
    }

    public String getControlType() {
        return controlType;
    }

    public Answers setControlType(String controlType) {
        this.controlType = controlType;
        return this;
    }

    public String getIsChildShow() {
        return isChildShow;
    }

    public Answers setIsChildShow(String isChildShow) {
        this.isChildShow = isChildShow;
        return this;
    }

    public String getControlValue() {
        return controlValue;
    }

    public Answers setControlValue(String controlValue) {
        this.controlValue = controlValue;
        return this;
    }

    public QuestionList getQuestionList() {
        return questionList;
    }

    public Answers setQuestionList(QuestionList questionList) {
        this.questionList = questionList;
        return this;
    }
}
