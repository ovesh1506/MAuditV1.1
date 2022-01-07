package com.writercorporation.adapeter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.writercorporation.maudit.R;
import com.writercorporation.model.CategoryList;

import java.util.ArrayList;

/**
 * Created by hemina.shah on 3/18/2016.
 */
public class CategoryAdapter extends BaseAdapter {

    Context context;
    ArrayList<CategoryList> categoryList;
    public CategoryAdapter(Context context, ArrayList<CategoryList> categoryList)
    {
        this.context=context;
        this.categoryList=categoryList;
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return categoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder1
    {
        TextView txt;
        ImageView imgArrow;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder1 holder=null;
        LayoutInflater minflater=(LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView==null)
        {
            convertView=minflater.inflate(R.layout.content_category,null);
            holder=new ViewHolder1();
            holder.txt=(TextView)convertView.findViewById(R.id.txtCategoryName);
            holder.imgArrow = (ImageView)convertView.findViewById(R.id.imgArrow);
            convertView.setTag(holder);
        }
        else
        {
            holder=(ViewHolder1)convertView.getTag();
        }
        holder.txt.setText(categoryList.get(position).getCategoryName());
        holder.imgArrow.setImageResource(categoryList.get(position).getImageResource());
        return convertView;
    }
}
