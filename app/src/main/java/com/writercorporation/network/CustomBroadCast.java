package com.writercorporation.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.writercorporation.utils.ConnectionDetector;

/**
 * Created by hemina.shah on 4/26/2016.
 */
public class CustomBroadCast extends BroadcastReceiver {
    ConnectionDetector connectionDetector;
    @Override
    public void onReceive(Context context, Intent intent) {
        connectionDetector=new ConnectionDetector(context);
        if(connectionDetector.isConnectingToInternet()) {
            intent = new Intent(Intent.ACTION_SYNC, null, context, CustomService.class);
            context.startService(intent);

           Intent intentImg = new Intent(Intent.ACTION_SYNC, null, context, CustomServiceImage.class);
            context.startService(intentImg);

            Intent intentCallLog = new Intent(Intent.ACTION_SYNC, null, context, CallLogService.class);
            context.startService(intentCallLog);

            Intent intentVisit=new Intent(Intent.ACTION_SYNC,null,context,VisitAnswerService.class);
            context.startService(intentVisit);
        }
    }
}
