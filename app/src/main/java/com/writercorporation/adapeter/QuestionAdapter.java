package com.writercorporation.adapeter;

import android.app.DatePickerDialog;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.writercorporation.maudit.OnCheckedChange;
import com.writercorporation.maudit.R;
import com.writercorporation.model.Answers;
import com.writercorporation.model.QuestionList;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by hemina.shah on 3/21/2016.
 */
public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionView>{

    Context context;
    ArrayList<QuestionList> list;
    OnCheckedChange checkedChangeObj;
    String textType= "number";


    public QuestionAdapter(Context context, ArrayList<QuestionList> list, OnCheckedChange checkedChangeObj)
    {
        this.context=context;
        this.list=list;
        this.checkedChangeObj = checkedChangeObj;
    }

    class QuestionView extends RecyclerView.ViewHolder{
        TextView txtQue;
        RadioGroup radioGroup;
        LinearLayout view1;
        RadioButton ans1;
        RadioButton ans2;
        RadioButton ans3;
        EditText childText;
        Spinner childSpinner;
        TextView childDatePicker;
        LinearLayout rootLayout;
        public QuestionView(View itemView) {
            super(itemView);

            txtQue=(TextView)itemView.findViewById(R.id.txtQue);
            radioGroup=(RadioGroup)itemView.findViewById(R.id.radioGroup);
            ans1=(RadioButton)itemView.findViewById(R.id.rb1);
            ans2=(RadioButton)itemView.findViewById(R.id.rb2);
            ans3=(RadioButton)itemView.findViewById(R.id.rb3);
            view1= (LinearLayout) itemView.findViewById(R.id.view1);
            childText = (EditText) itemView.findViewById(R.id.childText);
            childSpinner = (Spinner) itemView.findViewById(R.id.childSpinner);
            childDatePicker = (TextView) itemView.findViewById(R.id.childDatePicker);
            rootLayout = (LinearLayout) itemView.findViewById(R.id.rootLayout);

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindViewHolder(QuestionView holder, final int pos) {
        final int position = pos;

        final QuestionList questionSet=list.get(position);
        final TextView que=holder.txtQue;
        RadioGroup rg=holder.radioGroup;
        RadioButton rb1=holder.ans1;
        RadioButton rb2=holder.ans2;
        RadioButton rb3=holder.ans3;
        final EditText childText = holder.childText;
        final Spinner childSpinner = holder.childSpinner;
        final TextView childDatePicker = holder.childDatePicker;
        LinearLayout childCallLogView = holder.view1;
        LinearLayout rootLayout = holder.rootLayout;
        final int KEY_QUESTION_SET = R.string.key_questionset;
        final int KEY_ANSWER_SET = R.string.key_answerset;

        que.setText(questionSet.getQuestionText());
        rg.setVisibility(View.VISIBLE);
        List<Answers> answer = questionSet.getAnswerList();

        if(answer!=null){
            int size = answer.size();
            if(size>0) {
                rb1.setText(answer.get(0).getAnswerText());
                rb1.setVisibility(View.VISIBLE);
                rb1.setTag(KEY_QUESTION_SET, questionSet);
                rb1.setTag(KEY_ANSWER_SET, answer.get(0));

                //setView(answer.get(0),childCallLogView);
            }
            if(size>1){
                rb2.setText(answer.get(1).getAnswerText());
                rb2.setVisibility(View.VISIBLE);
                rb2.setTag(KEY_QUESTION_SET, questionSet);
                rb2.setTag(KEY_ANSWER_SET,answer.get(1));
            }
            if(size>2){
                rb3.setText(answer.get(2).getAnswerText());
                rb3.setVisibility(View.VISIBLE);
                rb3.setTag(KEY_QUESTION_SET,questionSet);
                rb3.setTag(KEY_ANSWER_SET,answer.get(2));
            }

            childSpinner.setTag(questionSet);
            childText.setTag(questionSet);
            childDatePicker.setTag(questionSet);
            rg.check(questionSet.getCheckedId());

            if(questionSet.isChildShow()){
                childCallLogView.setVisibility(View.VISIBLE);
                View parent = (View)childCallLogView.getParent();
                if(parent!=null)
                    parent.findViewById(questionSet.getChildView()).setVisibility(View.VISIBLE);
                Answers answers = questionSet.getCheckedAnswer();

                switch (questionSet.getChildView()){
                    case R.id.childText:
                        if ( answers.getTextInputType().equals("NUMERIC"))
                            childText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        else if (answers.getTextInputType().equals("ALPHANUMERIC"))
                            childText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE );
                        else if (answers.getTextInputType().equals("ALPHA"))
                            childText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                        else if (answers.getTextInputType().equals("FLOAT"))
                            childText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL );

                        childText.setHint(answers.getControlCaption());
                        childText.setText(questionSet.getEditTextValue());
                        childText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View view, boolean b) {
                                if (!b) {
                                    QuestionList questionSet = (QuestionList) view.getTag();
                                    questionSet.setEditTextValue(((TextView) view).getText().toString());
                                    questionSet.setExtraFieldValue(((TextView) view).getText().toString());
                                    checkedChangeObj.onListItemsChanged(questionSet);
                                    // notifyDataSetChanged();
                                }
                            }
                        });

                        childSpinner.setVisibility(View.GONE);
                        childDatePicker.setVisibility(View.GONE);

                        break;
                    case R.id.childSpinner:
                        //Spinner spinner = (Spinner)parent.findViewById(childView);
                        String controlValues = "Select,"+answers.getControlCaption()+","+answers.getControlValue();
                        String[] spinnerValues = controlValues.split(",");
                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item, spinnerValues);
                        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                        childSpinner.setAdapter(spinnerArrayAdapter);
                        childSpinner.setSelection(questionSet.getSpinnerSelectedItemPosition(), false);
                        childSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                QuestionList questionSet = (QuestionList) adapterView.getTag();
                                if(i==questionSet.getSpinnerSelectedItemPosition())
                                    return;
                                questionSet.setSpinnerSelectedItemPosition(i);
                                String value = childSpinner.getItemAtPosition(i).toString();
                                questionSet.setExtraFieldValue(value);

                                list.set(position, questionSet);
                                notifyDataSetChanged();
                                checkedChangeObj.onListItemsChanged(questionSet);
                                //notifyItemChanged(position);
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        childText.setVisibility(View.GONE);
                        childDatePicker.setVisibility(View.GONE);
                        break;
                    case R.id.childDatePicker:
                        Calendar mcurrentDate = Calendar.getInstance();

                        final int day;//  = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                        final int month;// = mcurrentDate.get(Calendar.MONTH);
                        final int year;//  = mcurrentDate.get(Calendar.YEAR);
                        if(!"Select Date".equals(questionSet.getSetedDate())){
                            day = Integer.valueOf(questionSet.getSetedDate().split("/")[0]);
                            month = Integer.valueOf(questionSet.getSetedDate().split("/")[1])-1;
                            year = Integer.valueOf(questionSet.getSetedDate().split("/")[2]);
                        }
                        else
                        {
                            day = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                            month = mcurrentDate.get(Calendar.MONTH);
                            year  = mcurrentDate.get(Calendar.YEAR);
                        }
                        childDatePicker.setText(questionSet.getSetedDate());
                        childDatePicker.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DatePickerDialog dpd = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                        // childDatePicker.setText("" + dayOfMonth + "/" + monthOfYear + "/" + year);

                                        questionSet.setSetedDate(dayOfMonth + "/" + (monthOfYear+1) + "/" + year);
                                        questionSet.setExtraFieldValue(dayOfMonth + "/" + (monthOfYear+1) + "/" + year);
                                        checkedChangeObj.onListItemsChanged(questionSet);
                                        notifyDataSetChanged();
                                    }
                                }
                                        , year, month, day);
                                dpd.show();
                            }
                        });

                        list.set(position, questionSet);
                        childText.setVisibility(View.GONE);
                        childSpinner.setVisibility(View.GONE);
                        break;
                }
            }else{
                childCallLogView.setVisibility(View.GONE);
            }

            View.OnClickListener lister = new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    QuestionList questionSet = (QuestionList) view.getTag(KEY_QUESTION_SET);
                    Answers answer = (Answers)view.getTag(KEY_ANSWER_SET);
                    questionSet.setCheckedId(view.getId());
                    if("TEXT".equalsIgnoreCase(answer.getControlType())) {
                        questionSet.setIsChildShow(true);
                        questionSet.setChildView(R.id.childText);
                    }
                    else if("COMBO".equalsIgnoreCase(answer.getControlType())) {
                        questionSet.setIsChildShow(true);
                        questionSet.setChildView(R.id.childSpinner);
                    }
                    else if("DATE".equalsIgnoreCase(answer.getControlType())) {
                        questionSet.setIsChildShow(true);
                        questionSet.setChildView(R.id.childDatePicker);

                    }else{
                        questionSet.setEditTextValue("");
                        questionSet.setSpinnerSelectedItemPosition(0);
                        questionSet.setIsChildShow(false);
                    }
                    questionSet.setCheckedAnswer(answer);

                    if("true".equals(answer.getIsChildShow())){
                       /* for(int i=0;i<list.size();i++){
                            QuestionList qList = list.get(i);
                            if(qList.getParentCheckpointId()==answer.getQuestionList().getId()){
                                qList = qList.setIsShow(false);
                                list.set(i,qList);
                            }
                        }*/
                        checkedChangeObj.onCheckedChange(answer.getQuestionList().getId(),false);

                    }else{
                        /*for(int i=0;i<list.size();i++){
                            QuestionList qList = list.get(i);
                            if(qList.getParentCheckpointId()==answer.getQuestionList().getId()){
                                qList = qList.setIsShow(true);
                                list.set(i,qList);
                            }
                        }*/
                        checkedChangeObj.onCheckedChange(answer.getQuestionList().getId(),true);
                    }


                    list.set(position, questionSet);
                   // checkedChangeObj.onListItemsChanged(questionSet);
                    // notifyDataSetChanged();


                }
            };
            rb1.setOnClickListener(lister);
            rb2.setOnClickListener(lister);
            rb3.setOnClickListener(lister);
        }

        if(questionSet.getParentCheckpointId()!=0 && questionSet.isShow()){
            rootLayout.setBackgroundColor(context.getResources().getColor(R.color.cardview_disabled_color));
            for ( int i = 0; i < childCallLogView.getChildCount();  i++ ){
                View view = childCallLogView.getChildAt(i);
                view.setEnabled(false);
            }
            rb1.setEnabled(false);
            rb2.setEnabled(false);
            rb3.setEnabled(false);
        }
        else
        {
            rootLayout.setBackgroundColor(context.getResources().getColor(R.color.cardview_enabled_color));
            for ( int i = 0; i < childCallLogView.getChildCount();  i++ ){
                View view = childCallLogView.getChildAt(i);
                view.setEnabled(true);
            }
            rb1.setEnabled(true);
            rb2.setEnabled(true);
            rb3.setEnabled(true);
        }
        //

    }


    @Override
    public QuestionView onCreateViewHolder(ViewGroup parent, int viewType) {
        View myView = LayoutInflater.from(context).inflate(R.layout.content_question, parent, false);
        QuestionView QueView = new QuestionView(myView);
        return QueView;

    }


}
