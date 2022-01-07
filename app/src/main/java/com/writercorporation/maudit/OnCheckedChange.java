package com.writercorporation.maudit;

import com.writercorporation.model.QuestionList;

/**
 * Created by hemina.shah on 4/21/2016.
 */
public interface OnCheckedChange {
    void onCheckedChange(int questionId,boolean flag);
    void onListItemsChanged(QuestionList callList);
}
