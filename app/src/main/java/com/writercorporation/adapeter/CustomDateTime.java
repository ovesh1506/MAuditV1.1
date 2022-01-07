package com.writercorporation.adapeter;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.writercorporation.maudit.R;
import com.writercorporation.maudit.VisitActivity;

/**
 * Created by hemina.shah on 1/17/2017.
 */

public class CustomDateTime extends DatePicker {


    public CustomDateTime(Context context) {
        super(context);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN)
        {
            ViewParent p = getParent();
            if (p != null)
                p.requestDisallowInterceptTouchEvent(true);
        }
        return false;

    }

    public void getDate(Context context)
    {
        final Dialog dialog = new Dialog(context);

        dialog.setContentView(R.layout.custom_date_time);
        dialog.setTitle("Date & Time");
        dialog.show();
        final DatePicker dp = (DatePicker)dialog.findViewById(R.id.datePicker1);
        dp.setScrollContainer(true);

        final TimePicker tp = (TimePicker) dialog.findViewById(R.id.timePicker1);
        Button okButn =  (Button)dialog.findViewById(R.id.btnOK);

        tp.setIs24HourView(true);
        // dp.setMinDate(new Date().getTime() - 1000);
        // dp.setMaxDate(new Date().getTime() + (1000 * 60 * 60 * 24));
        okButn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String dateAndTime = String.format("%d-%02d-%02d %02d:%02d:%02d", dp.getYear(), (dp.getMonth()+1),dp.getDayOfMonth(),tp.getCurrentHour(),tp.getCurrentMinute(),00);

                dialog.dismiss();
            }
        });


    }

}
