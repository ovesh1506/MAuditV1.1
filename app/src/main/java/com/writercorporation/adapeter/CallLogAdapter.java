package com.writercorporation.adapeter;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.writercorporation.maudit.R;
import com.writercorporation.model.CallLoggedList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by hemina.shah on 4/11/2016.
 */
public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.CallLogView>  {


    Button calllogKeepOpen, calllogClose;
    Context context;
    public ImageView callLogImage;
    ArrayList<CallLoggedList> allList;
    OnCallLogListener callLogListener;
    TextView txtDate;


    public class CallLogView extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView txtcalllog,txtcallDate;
        Button calllogKeepOpen, calllogClose;
        ArrayList<CallLoggedList> allList;
        ImageView imageView;
        OnCallLogListener callLogListener;
        public CallLogView(View itemView,ArrayList<CallLoggedList> list,OnCallLogListener callLogListener) {
            super(itemView);
            txtcalllog = (TextView) itemView.findViewById(R.id.txtcalllog);
            txtcallDate=(TextView)itemView.findViewById(R.id.txtcallDate);
            calllogKeepOpen = (Button) itemView.findViewById(R.id.calllogKeepOpen);
            calllogClose = (Button) itemView.findViewById(R.id.calllogClose);
            imageView=(ImageView)itemView.findViewById(R.id.ivCallLog1);
            this.allList=list;
            this.callLogListener=callLogListener;
            calllogClose.setOnClickListener(this);
            calllogKeepOpen.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            CallLoggedList callLoggedLists=allList.get(getAdapterPosition());
            String status;
            if(v.getId()==calllogClose.getId())
            {
                status="C";
            }
            else
                status="O";

            callLogListener.setOnCallLogListener(callLoggedLists,getAdapterPosition(),status);
        }

    }
    public CallLogAdapter(ArrayList<CallLoggedList> list, Context context,OnCallLogListener callLogListener) {
        this.allList = list;
        this.context=context;
        this.callLogListener=callLogListener;
    }

    @Override
    public CallLogView onCreateViewHolder(ViewGroup parent, int viewType) {
        View myView = LayoutInflater.from(context).inflate(R.layout.call_log_recycler, parent, false);
        CallLogView callView = new CallLogView(myView,allList,callLogListener);
        return callView;
    }

    @Override
    public void onBindViewHolder(CallLogAdapter.CallLogView holder,final int position) {
        try{
       final int pos=position;
        CallLoggedList callLog=allList.get(position);
        TextView txt=holder.txtcalllog;
        txt.setText(callLog.getQuestionText());
        txtDate=holder.txtcallDate;
        SimpleDateFormat ft = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        Date date= ft.parse(callLog.getCallLogDate());
        txtDate.setText(ft.format(date));
        calllogClose=holder.calllogClose;
        calllogKeepOpen=holder.calllogKeepOpen;
        callLogImage=holder.imageView;
        callLogImage.setTag(callLog.getPostion());
        txt.setTag(callLog);
        if( callLogImage.getTag().equals(position)) {
            CallLoggedList obj=(CallLoggedList)txt.getTag();
            if(obj.getBitmapValue()!=null) {
                callLogImage.setVisibility(View.VISIBLE);
                callLogImage.setImageBitmap(obj.getBitmapValue());


            }
            else
                callLogImage.setVisibility(View.GONE);

            if( obj.getIsButtonClick().equalsIgnoreCase("C")) {
                calllogClose.setBackgroundColor( Color.rgb(252, 184, 9));
                calllogKeepOpen.setBackgroundColor(Color.WHITE);
                obj.setCallLogStatus("C");
            }
            else if( obj.getIsButtonClick().equalsIgnoreCase("O")) {
                calllogKeepOpen.setBackgroundColor( Color.rgb(252, 184, 9));
                calllogClose.setBackgroundColor(Color.WHITE);
                obj.setCallLogStatus("O");
            }
            else
            {
                calllogClose.setBackgroundColor(Color.WHITE);
                calllogKeepOpen.setBackgroundColor(Color.WHITE);
            }

        }
        else
        {
            callLogImage.setVisibility(View.GONE);
            calllogClose.setBackgroundColor(Color.WHITE);
            calllogKeepOpen.setBackgroundColor(Color.WHITE);
        }

        }
        catch (Exception e){
            //e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return allList.size();
    }

}
