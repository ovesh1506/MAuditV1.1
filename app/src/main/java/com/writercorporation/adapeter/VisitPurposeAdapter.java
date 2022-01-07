package com.writercorporation.adapeter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.writercorporation.maudit.R;
import com.writercorporation.model.VisitPurposeModel;

import java.util.ArrayList;

/**
 * Created by hemina.shah on 12/13/2016.
 */

public class VisitPurposeAdapter extends BaseAdapter {

    Context context;
    ArrayList<VisitPurposeModel> visitList;
    LayoutInflater inflater;
    int resLayout;
    public VisitPurposeAdapter(Context context, ArrayList<VisitPurposeModel> visitList) {
       // super(context, resource);
        this.context=context;

        this.visitList=visitList;
        //this.resLayout=resource;
    }

    @Override
    public int getCount() {
        return visitList.size();
    }

    @Override
    public Object getItem(int position) {
        return visitList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View value=inflater.inflate(R.layout.custom_spinnertextview,parent,false);
        LinearLayout ll=(LinearLayout)value.findViewById(R.id.sp_textView);
        TextView txt=(TextView)ll.findViewById(R.id.txtVisitCustom);
        txt.setText(visitList.get(position).getPurposeDesc());
        return value;
    }
}
