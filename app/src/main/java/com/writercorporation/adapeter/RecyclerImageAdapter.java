package com.writercorporation.adapeter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.writercorporation.maudit.GridSelected;
import com.writercorporation.maudit.ItemObject;
import com.writercorporation.maudit.R;

import java.util.List;

/**
 * Created by hemina.shah on 4/22/2016.
 */
public class RecyclerImageAdapter  extends RecyclerView.Adapter<RecyclerImageAdapter.ImageViewHolder>{

    private List<ItemObject> itemList;
    private Context context;
    GridSelected onGridObj;
    public RecyclerImageAdapter(List<ItemObject> itemList, Context context, GridSelected onGridObj) {
        this.itemList = itemList;
        this.context = context;
        this.onGridObj = onGridObj;
    }
    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View imageView= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_xml_image, null);
        ImageViewHolder imageViewHolder=new ImageViewHolder(imageView,itemList,onGridObj);
        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        holder.edit_lable.setText(itemList.get(position).getName());
        holder.site_photo.setImageBitmap(itemList.get(position).getBitmap());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
    public void changeContent(int position, ItemObject obj){
        itemList.set(position,obj);
        notifyDataSetChanged();
    }
    class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        ImageView site_photo;
        TextView edit_lable;
        public List<ItemObject> imgObject;
        private GridSelected onGridObj;
        public ImageViewHolder(View itemView,List<ItemObject> imgObject,GridSelected onGridObj) {
            super(itemView);
            itemView.setOnClickListener(this);
            site_photo=(ImageView)itemView.findViewById(R.id.site_photo);
            edit_lable=(TextView)itemView.findViewById(R.id.edit_lable);
            this.imgObject = imgObject;
            this.onGridObj = onGridObj;

        }

        @Override
        public void onClick(View v) {

            ItemObject obj = imgObject.get(getPosition());
            onGridObj.onGridSelect(obj,v,getPosition());
        }
    }
}
