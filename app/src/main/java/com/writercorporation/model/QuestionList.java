package com.writercorporation.model;

import android.graphics.Bitmap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by amol.tate on 3/16/2016.
 */
public class QuestionList implements Serializable{
    @SerializedName("AuditCategoryID")
    @Expose
    private int auditCategoryID;
    @SerializedName("CheckpointGroupID")
    @Expose
    private int checkpointGroupID;

    @SerializedName("CheckpointSubGroupID")
    @Expose
    private int checkpointSubGroupID;

    @Expose
    @SerializedName("CheckpointItemID")
    @DatabaseField(id = true)
    private int id;
    @Expose
    @SerializedName("CheckpointItemDesc")
    @DatabaseField
    private String questionText;
    @Expose
    @DatabaseField
    private int toShow;
    @Expose
    @SerializedName("IsParent")
    @DatabaseField
    private String isParent;
    @Expose
    @SerializedName("ParentCheckpointItemID")
    @DatabaseField
    private int parentCheckpointId;
    @Expose
    @SerializedName("CheckpointItemOrder")
    @DatabaseField
    private int questionOrder;
    @Expose
    @DatabaseField(foreign = true,foreignAutoRefresh = true)
    private MicroCategory microCategory;
    @Expose
    @SerializedName("IsOneTime")
    @DatabaseField
    String isOneTime;
    @ForeignCollectionField
    private ForeignCollection<Answers> answersForeignCollection;


    @Expose
    public int checkedId = 0;
    @Expose
    private int childView;
    @Expose
    private boolean isChildShow = false;
    @Expose
    private int spinnerSelectedItemPosition = 0;
    @Expose
    private String editTextValue = "";
    @Expose
    private boolean isShow = true;
    @Expose
    private String extraFieldValue="";
    @Expose
    private Answers checkedAnswer;
    @Expose
    @SerializedName("Ans")
    List<Answers> list;
    @Expose
    private String setedDate = "Select Date";
    @Expose
    private Bitmap confirmedImage1;
    @Expose
    private Bitmap confirmedImage2;
    @Expose
    private int positon;
    @Expose
    private String setPath;

    private boolean makeCallLog = false;

    public QuestionList() {
    }

    public QuestionList(int id, String questionText, int toShow, String isParent, int parentCheckpointId, int questionOrder, MicroCategory microCategory,String isOneTime,Bitmap confirmedImage1,Bitmap confirmedImage2) {
        this.id = id;
        this.questionText = questionText;
        this.toShow = toShow;
        this.isParent = isParent;
        this.parentCheckpointId = parentCheckpointId;
        this.questionOrder = questionOrder;
        this.microCategory = microCategory;
        this.confirmedImage1=confirmedImage1;
        this.confirmedImage2=confirmedImage2;
        this.isOneTime=isOneTime;
        list = new ArrayList<>();
    }

    public QuestionList(int id, String questionText, int toShow, String isParent, int parentCheckpointId, int questionOrder) {
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

    public QuestionList setId(int id) {
        this.id = id;
        return this;
    }

    public String getIsOneTime() {
        return isOneTime;
    }

    public void setIsOneTime(String isOneTime) {
        this.isOneTime = isOneTime;
    }

    public String getQuestionText() {
        return questionText;
    }

    public QuestionList setQuestionText(String questionText) {
        this.questionText = questionText;
        return this;
    }

    public int getToShow() {
        return toShow;
    }

    public QuestionList setToShow(int toShow) {
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

    public QuestionList setCheckedId(int checkedId) {
        this.checkedId = checkedId;
        return this;
    }

    public int getChildView() {
        return childView;
    }

    public QuestionList setChildView(int childView) {
        this.childView = childView;
        return this;
    }

    public boolean isChildShow() {
        return isChildShow;
    }

    public QuestionList setIsChildShow(boolean isChildShow) {
        this.isChildShow = isChildShow;
        return this;
    }

    public Answers getCheckedAnswer() {
        return checkedAnswer;
    }

    public QuestionList setCheckedAnswer(Answers checkedAnswer) {
        this.checkedAnswer = checkedAnswer;
        return this;
    }

    public int getSpinnerSelectedItemPosition() {
        return spinnerSelectedItemPosition;
    }

    public QuestionList setSpinnerSelectedItemPosition(int spinnerSelectedItemPosition) {
        this.spinnerSelectedItemPosition = spinnerSelectedItemPosition;
        return this;
    }

    public String getEditTextValue() {
        return editTextValue;
    }

    public QuestionList setEditTextValue(String editTextValue) {
        this.editTextValue = editTextValue;
        return this;
    }

    public String getIsParent() {
        return isParent;
    }

    public QuestionList setIsParent(String isParent) {
        this.isParent = isParent;
        return this;
    }

    public int getParentCheckpointId() {
        return parentCheckpointId;
    }

    public QuestionList setParentCheckpointId(int parentCheckpointId) {
        this.parentCheckpointId = parentCheckpointId;
        return this;
    }

    public int getQuestionOrder() {
        return questionOrder;
    }

    public QuestionList setQuestionOrder(int questionOrder) {
        this.questionOrder = questionOrder;
        return this;
    }

    public MicroCategory getMicroCategory() {
        return microCategory;
    }

    public QuestionList setMicroCategory(MicroCategory microCategory) {
        this.microCategory = microCategory;
        return this;
    }

    public ForeignCollection<Answers> getAnswersForeignCollection() {
        return answersForeignCollection;
    }

    public QuestionList setAnswersForeignCollection(List<Answers> answersForeignCollection) {
        this.answersForeignCollection.addAll(answersForeignCollection);
        return this;
    }



    public boolean isShow() {
        return isShow;
    }

    public QuestionList setIsShow(boolean isShow) {
        this.isShow = isShow;
        return this;
    }

    public String getExtraFieldValue() {
        return extraFieldValue;
    }

    public QuestionList setExtraFieldValue(String extraFieldValue) {
        this.extraFieldValue = extraFieldValue;
        return this;
    }
    public List<Answers> getList() {
        return list;
    }
    public QuestionList setList(List<Answers> list) {
        this.list = list;
        return this;
    }

    public String getSetedDate() {
        return setedDate;
    }

    public void setSetedDate(String setedDate) {
        this.setedDate = setedDate;
    }

    public boolean isMakeCallLog() {
        return makeCallLog;
    }

    public QuestionList setMakeCallLog(boolean makeCallLog) {
        this.makeCallLog = makeCallLog;
        return this;
    }

    public Bitmap getConfirmedImage1() {
        return confirmedImage1;
    }

    public void setConfirmedImage1(Bitmap confirmedImage1) {
        this.confirmedImage1 = confirmedImage1;
    }

    public int getPositon() {
        return positon;
    }

    public void setPositon(int positon) {
        this.positon = positon;
    }

    public void setSetPath(String setPath) {
        this.setPath = setPath;
    }

    public String getSetPath()
    {
        return setPath;
    }
    public Bitmap getConfirmedImage2() {
        return confirmedImage2;
    }

    public void setConfirmedImage2(Bitmap confirmedImage2) {
        this.confirmedImage2 = confirmedImage2;
    }

    public int getAuditCategoryID() {
        return auditCategoryID;
    }

    public void setAuditCategoryID(int auditCategoryID) {
        this.auditCategoryID = auditCategoryID;
    }

    public int getCheckpointGroupID() {
        return checkpointGroupID;
    }

    public void setCheckpointGroupID(int checkpointGroupID) {
        this.checkpointGroupID = checkpointGroupID;
    }

    public int getCheckpointSubGroupID() {
        return checkpointSubGroupID;
    }

    public void setCheckpointSubGroupID(int checkpointSubGroupID) {
        this.checkpointSubGroupID = checkpointSubGroupID;
    }
}
