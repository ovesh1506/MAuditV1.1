package com.writercorporation.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by hemina.shah on 3/16/2016.
 */
public class RecyclerViewOnTouchListener implements RecyclerView.OnItemTouchListener{

    private OnItemClickListener mlistner;
    GestureDetector gestureDetector;

    public RecyclerViewOnTouchListener(Context context, OnItemClickListener onItemClickListener)
    {
        this.mlistner=onItemClickListener;
        gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener()
        {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }
    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View childview=rv.findChildViewUnder(e.getX(),e.getY());
        if(childview!=null && mlistner!=null && gestureDetector.onTouchEvent(e))
        {
            childview.setActivated(true);
            childview.setPressed(true);
            mlistner.onItemClick(childview, rv.getChildAdapterPosition(childview));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

}
