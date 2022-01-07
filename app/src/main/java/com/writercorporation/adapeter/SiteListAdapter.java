package com.writercorporation.adapeter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.writercorporation.maudit.R;
import com.writercorporation.model.SiteList;

import java.util.ArrayList;

/**
 * Created by hemina.shah on 3/14/2016.
 */
public class SiteListAdapter extends RecyclerView.Adapter<SiteListAdapter.SiteViewHolder> {

    ArrayList<SiteList> list;
    public static int selected_postion=-1;
    public static String siteCode="";
    private Context mContext;



    public class SiteViewHolder extends RecyclerView.ViewHolder
    {

        TextView lblsiteCode,txtsiteCode,txtCallloggedStatus,lblsitename,txtsiteName,txtVisit;
        LinearLayout layout;
        ArrayList<SiteList> allList;


        public SiteViewHolder(View itemView) {
            super(itemView);
//          lblsiteaddress=(TextView)itemView.findViewById(R.id.lblsiteCode);
            layout=(LinearLayout)itemView.findViewById(R.id.llLayout);
            txtsiteCode=(TextView)itemView.findViewById(R.id.txtsiteCode);
            txtCallloggedStatus=(TextView)itemView.findViewById(R.id.txtCallloggedStatus);
            lblsitename=(TextView)itemView.findViewById(R.id.lblsitename);
            txtsiteName=(TextView)itemView.findViewById(R.id.txtsiteName);
            txtVisit=(TextView)itemView.findViewById(R.id.txtVisit);
           // itemView.setOnClickListener(this);
            this.allList=list;

        }


    }




    public SiteListAdapter(ArrayList<SiteList> list, Context context)
    {
        this.list=list;
        this.mContext = context;
    }
    @Override
    public SiteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View myView= LayoutInflater.from(parent.getContext()).inflate(R.layout.site_list_recyclerview,parent,false);
        SiteViewHolder view=new SiteViewHolder(myView);
        return view;
    }

    @Override
    public void onBindViewHolder(SiteListAdapter.SiteViewHolder holder, final int position) {
        final int pos=position;
        SiteList model=list.get(position);
        TextView txtsiteCode=holder.txtsiteCode;
        txtsiteCode.setText(model.getSiteCode());
        TextView txtCalllog=holder.txtCallloggedStatus;
        txtCalllog.setText(model.getSiteIsLogged());
        TextView txtSiteName=holder.txtsiteName;
        txtSiteName.setText(model.getSiteName());
        LinearLayout llayout=holder.layout;
        TextView txtVisit1=holder.txtVisit;
        txtVisit1.setText(model.getIsVisit());
        llayout.setTag(model);

        if(llayout.getTag().equals(getItem(position))) {

            if (model.getSiteColor() != R.color.white)
                llayout.setBackgroundColor( Color.rgb(252  ,184,9));
            else if(siteCode.equalsIgnoreCase(model.getSiteCode()))
                llayout.setBackgroundColor(Color.rgb(128,128,128));
            else
                llayout.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemChanged(selected_postion);
              /*  selected_postion=position;
                notifyItemChanged(selected_postion);*/
            }
        });

      }
    public SiteList getItem(int position)
    {
             return list.get(position);
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


}
