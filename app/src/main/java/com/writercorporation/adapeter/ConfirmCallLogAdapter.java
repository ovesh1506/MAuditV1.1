package com.writercorporation.adapeter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.writercorporation.maudit.R;
import com.writercorporation.model.QuestionList;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by hemina.shah on 4/19/2016.
 */
public class ConfirmCallLogAdapter extends BaseAdapter{

    Context context;
    ViewHolder1 holder=null;
    public ArrayList<QuestionList> qlist;
    ArrayList<Integer> hiddenPositions = new ArrayList<>();
    String defaultPath;
    File file;
    public final static int CALLLOGIMAGE1=1002;
    public final static int CALLLOGIMAGE2=1003;
    OnConfirmCallLogListener onlistner;
    public ConfirmCallLogAdapter(Context context,ArrayList<QuestionList> qlist,ArrayList<Integer> hiddenPositions,OnConfirmCallLogListener onlistner)
    {
        this.context=context;
        this.qlist=qlist;
        this.hiddenPositions.addAll(hiddenPositions);
        this.onlistner=onlistner;
    }
    @Override
    public int getCount() {

        return qlist.size()- hiddenPositions.size();
    }

    @Override
    public Object getItem(int position) {
        return qlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    class  ViewHolder1
    {
        TextView txtQuetion,txtAnswer;
        CheckBox checkBox;
        RelativeLayout rootLayout;
        ImageView ivConfirmedCallLog1,ivConfirmedCallLog2;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater minflater=(LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        final int pos;
        if(convertView==null)
        {
            convertView=minflater.inflate(R.layout.content_confirmcalllog,null);
            holder=new ViewHolder1();
            holder.txtQuetion=(TextView)convertView.findViewById(R.id.txtQueCalllog);
            holder.txtAnswer=(TextView)convertView.findViewById(R.id.txtAnsCalllog);
            holder.checkBox=(CheckBox)convertView.findViewById(R.id.ckCallLog);
            holder.rootLayout = (RelativeLayout)convertView.findViewById(R.id.rootLayout);
            holder.ivConfirmedCallLog1=(ImageView)convertView.findViewById(R.id.ivConfirmedCallLog1);
            holder.ivConfirmedCallLog2=(ImageView)convertView.findViewById(R.id.ivConfirmedCallLog2);
            convertView.setTag(holder);
        }
        else
        {
            holder=(ViewHolder1)convertView.getTag();
        }

        for(Integer hiddenIndex : hiddenPositions) {
            if(hiddenIndex <= position) {
                position = position + 1;
            }
        }
        pos = position;
        final QuestionList question = qlist.get(position);

        holder.txtQuetion.setTag(question);
        if(question.getCheckedAnswer()!=null ) {

            holder.txtQuetion.setText(question.getQuestionText());

            holder.txtAnswer.setText(question.getCheckedAnswer().getAnswerText());

            holder.checkBox.setChecked(question.isMakeCallLog());
            holder.ivConfirmedCallLog1.setImageBitmap(question.getConfirmedImage1());
            holder.ivConfirmedCallLog2.setImageBitmap(question.getConfirmedImage2());

            /*holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    QuestionList que = qlist.get(pos);
                    que.setMakeCallLog(isChecked);

                    qlist.set(pos, que);
                    notifyDataSetChanged();

                }
            });*/

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox ch = (CheckBox) view;
                    QuestionList que = qlist.get(pos);
                    que.setPositon(pos);
                    defaultPath = getPathOfImage();
                    onlistner.setImageListener(qlist, pos, defaultPath);
                    que.setMakeCallLog(ch.isChecked());


                    if (ch.isChecked()) {
                        getAlertMessage(pos, CALLLOGIMAGE1);
                    } else {
                        que.setConfirmedImage1(null);
                        que.setConfirmedImage2(null);
                    }
                    qlist.set(pos, que);
                    notifyDataSetChanged();

                }
            });
            holder.ivConfirmedCallLog1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onlistner.setImageListener(qlist, pos, defaultPath);
                    getAlertMessage(pos, CALLLOGIMAGE1);
                }
            });
            holder.ivConfirmedCallLog2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onlistner.setImageListener(qlist, pos, defaultPath);
                    getAlertMessage(pos, CALLLOGIMAGE2);
                }
            });
        }

        return convertView;
    }

    public void getAlertMessage(final int position,final int callLogImageStatus)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle("info!");
        builder.setMessage("Are you sure to have image for this calllog? ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                qlist.get(position).setSetPath(defaultPath);
                file = new File(defaultPath);
                Uri outputfile = Uri.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputfile);
                ((Activity)(context)). startActivityForResult(intent,callLogImageStatus);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               // CheckBox ch = (CheckBox) view;
              /*  QuestionList que = qlist.get(position);
                que.setMakeCallLog(holder.checkBox.isChecked());
                qlist.set(position, que);
                notifyDataSetChanged();*/
            }
        });
        builder.show();
    }


    public String getPathOfImage() {
        Date currDate = new Date();
        long timeStamp = currDate.getTime();
        String folderPath = Environment.getExternalStorageDirectory()
                + "/maudit/images/";
        File folder = new File(folderPath);
        if (!folder.exists())
            folder.mkdirs();

        String extStorageDirectory = folder.toString();
        String newPath = "maudit_" + timeStamp + ".png";
        File newfolder = new File(folder, newPath);
        try {
            if (!newfolder.exists())
                newfolder.createNewFile();
            // Log.v("If", "-- newfolder");
        } catch (Exception e) {

        }
        return extStorageDirectory + "/maudit_" + timeStamp + ".png";
    }

}