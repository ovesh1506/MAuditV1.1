package com.writercorporation.model;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by amol.tate on 3/16/2016.
 */
public class QuestionListCache implements Serializable{
    @Expose
    @DatabaseField(id = true)
    private int id;
    @Expose
    @DatabaseField
    private String questionText;

    @Expose
    @DatabaseField
    private int toShow;

    @Expose
    @DatabaseField
    private String isParent;

    @Expose
    @DatabaseField
    private int parentCheckpointId;

    @Expose
    @DatabaseField
    private int questionOrder;

    @Expose
    @DatabaseField(foreign = true,foreignAutoRefresh = true)
    private MicroCategory microCategory;

    @Expose
    @ForeignCollectionField
    private ForeignCollection<Answers> answersForeignCollection;





    @DatabaseField
    public int checkedId = 0;
    @DatabaseField
    private int childView;
    @DatabaseField
    private boolean isChildShow = false;
    @DatabaseField
    private int spinnerSelectedItemPosition = 0;
    @DatabaseField
    private String editTextValue = "";
    @DatabaseField
    private boolean isShow = true;
    @DatabaseField
    private String extraFieldValue="";

    @Expose
    @DatabaseField
    private Answers checkedAnswer;
    @DatabaseField
    private String categoryType;



    public QuestionListCache() {
    }

    public QuestionListCache(int id, String questionText, int toShow, String isParent, int parentCheckpointId, int questionOrder, MicroCategory microCategory) {
        this.id = id;
        this.questionText = questionText;
        this.toShow = toShow;
        this.isParent = isParent;
        this.parentCheckpointId = parentCheckpointId;
        this.questionOrder = questionOrder;
        this.microCategory = microCategory;
    }

    public QuestionListCache(int id, String questionText, int toShow, String isParent, int parentCheckpointId, int questionOrder) {
        this.id = id;
        this.questionText = questionText;
        this.toShow = toShow;
        this.isParent = isParent;
        this.parentCheckpointId = parentCheckpointId;
        this.questionOrder = questionOrder;
    }

    public int getId() {
        return id;
    }

    public QuestionListCache setId(int id) {
        this.id = id;
        return this;
    }

    public String getQuestionText() {
        return questionText;
    }

    public QuestionListCache setQuestionText(String questionText) {
        this.questionText = questionText;
        return this;
    }

    public int getToShow() {
        return toShow;
    }

    public QuestionListCache setToShow(int toShow) {
        this.toShow = toShow;
        return this;
    }

    public List<Answers> getAnswerList() {
        ArrayList<Answers> answersArrayList = new ArrayList<>();
        for (Answers answer : answersForeignCollection) {
            answersArrayList.add(answer);
        }
        return answersArrayList;
    }

    public int getCheckedId() {
        return checkedId;
    }

    public QuestionListCache setCheckedId(int checkedId) {
        this.checkedId = checkedId;
        return this;
    }

    public int getChildView() {
        return childView;
    }

    public QuestionListCache setChildView(int childView) {
        this.childView = childView;
        return this;
    }

    public boolean isChildShow() {
        return isChildShow;
    }

    public QuestionListCache setIsChildShow(boolean isChildShow) {
        this.isChildShow = isChildShow;
        return this;
    }

    public Answers getCheckedAnswer() {
        return checkedAnswer;
    }

    public QuestionListCache setCheckedAnswer(Answers checkedAnswer) {
        this.checkedAnswer = checkedAnswer;
        return this;
    }

    public int getSpinnerSelectedItemPosition() {
        return spinnerSelectedItemPosition;
    }

    public QuestionListCache setSpinnerSelectedItemPosition(int spinnerSelectedItemPosition) {
        this.spinnerSelectedItemPosition = spinnerSelectedItemPosition;
        return this;
    }

    public String getEditTextValue() {
        return editTextValue;
    }

    public QuestionListCache setEditTextValue(String editTextValue) {
        this.editTextValue = editTextValue;
        return this;
    }

    public String getIsParent() {
        return isParent;
    }

    public QuestionListCache setIsParent(String isParent) {
        this.isParent = isParent;
        return this;
    }

    public int getParentCheckpointId() {
        return parentCheckpointId;
    }

    public QuestionListCache setParentCheckpointId(int parentCheckpointId) {
        this.parentCheckpointId = parentCheckpointId;
        return this;
    }

    public int getQuestionOrder() {
        return questionOrder;
    }

    public QuestionListCache setQuestionOrder(int questionOrder) {
        this.questionOrder = questionOrder;
        return this;
    }

    public MicroCategory getMicroCategory() {
        return microCategory;
    }

    public QuestionListCache setMicroCategory(MicroCategory microCategory) {
        this.microCategory = microCategory;
        return this;
    }

    public ForeignCollection<Answers> getAnswersForeignCollection() {
        return answersForeignCollection;
    }

    public QuestionListCache setAnswersForeignCollection(ForeignCollection<Answers> answersForeignCollection) {
        this.answersForeignCollection = answersForeignCollection;
        return this;
    }

    public boolean isShow() {
        return isShow;
    }

    public QuestionListCache setIsShow(boolean isShow) {
        this.isShow = isShow;
        return this;
    }

    public String getExtraFieldValue() {
        return extraFieldValue;
    }

    public QuestionListCache setExtraFieldValue(String extraFieldValue) {
        this.extraFieldValue = extraFieldValue;
        return this;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public QuestionListCache setCategoryType(String categoryType) {
        this.categoryType = categoryType;
        return this;
    }
}
