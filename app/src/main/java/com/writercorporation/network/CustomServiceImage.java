package com.writercorporation.network;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;

import com.writercorporation.database.DatabaseManager;
import com.writercorporation.model.ImageModel;
import com.writercorporation.utils.ConnectionDetector;

import java.util.List;

/**
 * Created by hemina.shah on 4/26/2016.
 */
public class CustomServiceImage extends IntentService implements OnTaskComplete {

    ConnectionDetector connectionDetector;
    DatabaseManager dManager;

    public CustomServiceImage() {
        super("MAudit");
        connectionDetector = new ConnectionDetector(this);
        dManager = DatabaseManager.getInstance();
    }

    @SuppressWarnings("ResourceType")
    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            if (connectionDetector.isConnectingToInternet()) {

                List<ImageModel> completedSiteInfos = dManager.getCompletedUnsyncImages();
                for(ImageModel completedSiteInfo:completedSiteInfos) {
                NetworkTask networkTask = new NetworkTask(this, this, completedSiteInfo.getJsonString(), "PostImage", completedSiteInfo.getId());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    networkTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    networkTask.execute();
                }
                 }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public String onTaskComplete(String result) {
        return result;
    }

    @Override
    public void onTaskComplete(String result, int id) {
        try {
            result = result.replace("\"","");
            String status = result.split("-")[0];
            id = Integer.valueOf(result.split("-")[1]);
            if (status.equals("SAVED")) {

                dManager.changeSyncStatusImages("SYNC", id);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dismissDialog(String result) {

    }

    @Override
    public void onPublishResult(String msg) {

    }
}
