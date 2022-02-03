package com.writercorporation.maudit;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.telecom.Call;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.writercorporation.adapeter.CallLogAdapter;
import com.writercorporation.adapeter.OnCallLogListener;
import com.writercorporation.database.DatabaseManager;
import com.writercorporation.model.CallLoggedList;
import com.writercorporation.model.CompleteCallLogInfo;
import com.writercorporation.model.QuestionList;
import com.writercorporation.network.CallLogService;
import com.writercorporation.network.CustomBroadCast;
import com.writercorporation.network.NetworkTask;
import com.writercorporation.network.OnTaskComplete;
import com.writercorporation.utils.AppConstant;
import com.writercorporation.utils.BitmapLoader;
import com.writercorporation.utils.ConnectionDetector;
import com.writercorporation.utils.GPSTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.writercorporation.maudit.SiteListFragment.isExternalStorageWritable;

/**
 * Created by hemina.shah on 4/11/2016.
 */
public class CallLogActivity extends AppCompatActivity implements OnCallLogListener,OnTaskComplete {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager recycleLayout;
    CallLogAdapter logAdapter;
    ActionBar actionBar;
    AppConstant check;
    Toolbar toolbar;
    int CHECK_POSITION;
    String defaultPath;
   // int position;
    File file;
    ConnectionDetector conn;
    DatabaseManager dManager;
    ArrayList<CallLoggedList> callList = new ArrayList<CallLoggedList>();
    ArrayList<Bitmap> value=new ArrayList<Bitmap>();
    ProgressDialog pd;
    GPSTracker gps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_log);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if(actionBar!=null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        check=AppConstant.getInstance();
        value.clear();
        pd=new ProgressDialog(this);
        pd.setMessage("CallLog data downloading....");
        pd.setCancelable(false);
        dManager=DatabaseManager.getInstance();
        actionBar.setDisplayHomeAsUpEnabled(true);
        defaultPath= getPathOfImage();
        file = new File(defaultPath);
        recyclerView = (RecyclerView) findViewById(R.id.list_callLog);
        recycleLayout = new LinearLayoutManager(getApplicationContext());
        conn=new ConnectionDetector(this);
        recyclerView.setLayoutManager(recycleLayout);
        gps = new GPSTracker(this);
        getData();


    }
    public String getPathOfImage() {
        Date currDate = new Date();
        long timeStamp = currDate.getTime();
        String folderPath = "";
        if(isExternalStorageWritable()){
            //uri = Uri.fromFile(new File(LoginActivity.this.getExternalFilesDir(null) + "/wsgMauditapk/" + filenamme));
            folderPath = this.getApplicationContext().getExternalFilesDir(null).getAbsolutePath() + "/maudit/images/";
        }else{
            folderPath = Environment.getExternalStorageDirectory()
                    + "/maudit/images/";
        }
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK && requestCode == CHECK_POSITION) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(CallLogActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                1);
                        return;
                    }
                    loadImage();
                }
                loadImage();
            }
        }
         catch (Exception e) {
      //  e.printStackTrace();
    }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.calllog, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch ((id))
        {
            case android.R.id.home:
                showAlertDialogBackButton();
                break;
            case R.id._callLog:

               getVerifyData();


                break;
            case R.id.refreshCallLog:
                if(conn.isConnectingToInternet())
                    isCallLogDetail(check.getSiteID());
                else
                    check.showErrorMessage(getString(R.string.error_100_slow_internet));

        }
        return super.onOptionsItemSelected(item);
    }

    public void showAlertDialogBackButton() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("info!");
        builder.setMessage("Are you sure to go back.\nPress 'YES'?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //  position=recyclerView.

                dialog.dismiss();
                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();


            }
        });

        builder.show();
    }

    public void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("info!");
        builder.setMessage("Do you want to audit on site " + check.getSiteCode() + "?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //  position=recyclerView.

                Intent newActivity = new Intent();

                newActivity.setClass(getApplicationContext(), CategoryActivity.class);
                startActivity(newActivity);
                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(CallLogActivity.this, SiteListActivity.class);
                startActivity(intent);
                finish();

            }
        });

        builder.show();
    }
    public void getVerifyData()
    {
        boolean isflag=false;
        int count=0;
        for(CallLoggedList callLoggedList:callList)
        {

            if(callLoggedList.getCallLogStatus()==null || callLoggedList.getCallLogStatus().equalsIgnoreCase(""))
            {
                check.showErrorMessage("Please fill all the fields before submit");

                isflag=false;
                break;
            }
            else
            {

                count++;
            }

        }
        if(count==callList.size())
        {
            StoreCallLogData storeCallLogData=new StoreCallLogData();
            storeCallLogData.execute();
        }
        else
        {
            check.showErrorMessage("Kindly attend all callLog ...");
        }
    }
    public void getData() {
        try {
            callList.clear();
            ArrayList<QuestionList> questionListArrayList=dManager.getQuestionText(check.getSiteID());
            callList=dManager.getCallLogList(check.getSiteID());

            for(int i=0;i<questionListArrayList.size();i++)
            {

                callList.get(i).setQuestionText(questionListArrayList.get(i).getQuestionText());
            }
            if(callList.size()>0)
            {
                logAdapter = new CallLogAdapter(callList, this,this);
                logAdapter.notifyDataSetChanged();

                recyclerView.setAdapter(logAdapter);
            }
        }
        catch (SQLException e)
        {
           // e.printStackTrace();
        }
        catch (Exception e){
          //  e.printStackTrace();
        }


    }

    @Override
    public void setOnCallLogListener(CallLoggedList callLoggedList, int position, String buttonClick) {
        CHECK_POSITION=position;
        callLoggedList.setIsButtonClick(buttonClick);

        if(buttonClick.equalsIgnoreCase("C"))
        {
            Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            defaultPath= getPathOfImage();
            file = new File(defaultPath);

            Uri outputfile = null;//  Uri.fromFile(file);
            if(isExternalStorageWritable()){
                //uri = Uri.fromFile(new File(LoginActivity.this.getExternalFilesDir(null) + "/wsgMauditapk/" + filenamme));
                outputfile = FileProvider.getUriForFile(CallLogActivity.this, getApplicationContext().getPackageName() + ".provider", file);

            }else{
                outputfile = Uri.fromFile(file);//"MAudit_1.0.0.5.apk"
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputfile);
            startActivityForResult(intent,position);
        }
        else
        {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            defaultPath = getPathOfImage();
            file = new File(defaultPath);
            Uri outputfile = null;//Uri.fromFile(file);

            if(isExternalStorageWritable()){
                //uri = Uri.fromFile(new File(LoginActivity.this.getExternalFilesDir(null) + "/wsgMauditapk/" + filenamme));
                outputfile = FileProvider.getUriForFile(CallLogActivity.this, getApplicationContext().getPackageName() + ".provider", file);
            }else{
                outputfile = Uri.fromFile(file);//"MAudit_1.0.0.5.apk"
            }

            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputfile);
            startActivityForResult(intent, position);
        }
    }

    public void showAlertDialogOnKeepOpen(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("info!");
        builder.setMessage("Do you want to take Picture press 'YES'");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                defaultPath = getPathOfImage();
                file = new File(defaultPath);
                Uri outputfile = null;//Uri.fromFile(file);
                if(isExternalStorageWritable()){
                    //uri = Uri.fromFile(new File(LoginActivity.this.getExternalFilesDir(null) + "/wsgMauditapk/" + filenamme));
                    outputfile = FileProvider.getUriForFile(CallLogActivity.this, getApplicationContext().getPackageName() + ".provider", file);
                }else{
                    outputfile = Uri.fromFile(file);//"MAudit_1.0.0.5.apk"
                }

                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputfile);
                startActivityForResult(intent, position);

            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callList.get(CHECK_POSITION).setPostion(CHECK_POSITION);
                callList.get(CHECK_POSITION).setIsflag(true);
                callList.get(CHECK_POSITION).setColor(R.color.calllogbutton);
                callList.get(CHECK_POSITION).setBitmapValue(null);
                logAdapter.notifyDataSetChanged();
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public String onTaskComplete(String result) {

        String response="";
        try {
            dManager.DeleteCallLogList(check.getSiteID());
            JSONArray callLogArray = new JSONArray(result);


            if(callLogArray.length()!=0)
            {
                callList =new ArrayList<>();
                callList.clear();
                for(int j=0;j<callLogArray.length();j++)
                {
                    JSONObject callLogJsonObj =  callLogArray.getJSONObject(j);
                    String callLogDate=callLogJsonObj.optString("CallLogDate");
                    String callLogID=callLogJsonObj.optString("CallLogID");
                    int callLogCheckPoint=callLogJsonObj.optInt("CheckPointItemID");
                    CallLoggedList callLoggedList=new CallLoggedList(check.getSiteID(),callLogID,callLogCheckPoint,callLogDate,"");
                    callList.add(callLoggedList);

                }

                dManager.insertCallLogedList(callList);
                response="Success";
            }
            else
                response=getString(R.string.data_not_found);
        }catch(JSONException je){
            response = getString(R.string.error_101_json);
        }catch(SQLiteException sle){
            response = getString(R.string.error_101_json);
        } catch(Exception e){
            response = getString(R.string.error_104_unexpected_error);
        }
        return response;
    }

    @Override
    public void onTaskComplete(String result, int id) {

    }

    @Override
    public void dismissDialog(String result) {
        if(pd!=null)
        {
            pd.dismiss();
        }

        getData();
    }

    @Override
    public void onPublishResult(String msg) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       /* if(broadCast!=null)
            unregisterReceiver(broadCast);*/
    }

    class StoreCallLogData extends AsyncTask<String,String,String>
    {
        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(CallLogActivity.this);
            pd.setCancelable(false);
            pd.setMessage("Please Wait...");
            pd.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s!=null)
            {
                try {
                    CompleteCallLogInfo completeCallLogInfo=new CompleteCallLogInfo(s,check.getSiteID());
                    dManager.insertCompleteCallLog(completeCallLogInfo);

                    if(conn.isConnectingToInternet())
                    {
                       /* intentFilter=new IntentFilter();
                        broadCast=new CustomBroadCast();*/
                        Intent intent=new Intent(Intent.ACTION_SYNC, null, CallLogActivity.this, CallLogService.class);
                        CallLogActivity.this.startService(intent);
                       /* registerReceiver(broadCast, intentFilter);*/

                    }
                for(CallLoggedList callLoggedList:callList) {

                        if(callLoggedList.getBitmapValue()!=null)
                        dManager.updateCallLogstatus(callLoggedList.getServerCallLogID(), callLoggedList.getCallLogStatus(),BitmapLoader.BitMapToString(callLoggedList.getBitmapValue()));
                        else
                            dManager.updateCallLogstatus(callLoggedList.getServerCallLogID(), callLoggedList.getCallLogStatus(),"");

                }
                }catch (SQLException e)
                {
                    check.showErrorMessage(getString(R.string.error_101_json));
                }
                catch (Exception e)
                {
                  //  e.printStackTrace();
                }
                showAlertDialog();
                if(pd!=null) {
                    pd.dismiss();
                    Intent broadcast=new Intent(getApplicationContext(),CustomBroadCast.class);
                    getApplicationContext().startService(broadcast);
                }
            }

        }


        @Override
        protected String doInBackground(String... params) {
            String jsonValue=null;
            try
            {
                JSONObject headjsonObject=new JSONObject();
                headjsonObject.put("UserName", check.getUserID());// HH:mm:ss
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");
                headjsonObject.put("CallClosedDateTime", sdf.format(new Date()));
                headjsonObject.put("SiteID",check.getSiteID());
                headjsonObject.put("Lattitude",gps.getLatitude());
                headjsonObject.put("Longitude",gps.getLongitude());
                JSONArray jsonArray=new JSONArray();
                for(CallLoggedList callLoggedList:callList)
                {
                    if(callLoggedList.getBitmapValue()!=null) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("CallLogID", callLoggedList.getServerCallLogID());
                        jsonObject.put("CallLogStatus", callLoggedList.getCallLogStatus());
                        jsonObject.put("ImageData", BitmapLoader.BitMapToString(callLoggedList.getBitmapValue()));
                        jsonArray.put(jsonObject);
                    }
                }
                headjsonObject.put("CallDetails",jsonArray);
                jsonValue=headjsonObject.toString();

            }catch (JSONException e){
               // e.printStackTrace();
            }
            catch (Exception e){
               // e.printStackTrace();
            }
            return jsonValue;
        }


    }
    public void isCallLogDetail(int siteID)
    {
        if(pd!=null)
            pd.show();
        try {

            JSONObject rootJsonObject = new JSONObject();
            //rootJsonObject.put("SiteID", siteID);
            rootJsonObject.put("SiteID", check.getSiteID());
            if(conn.isConnectingToInternet()) {
                NetworkTask task = new NetworkTask(this, this, rootJsonObject.toString(), "GetCallLogDetails");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    task.execute();
                }
            }
            else
            {
                check.showErrorMessage(getString(R.string.error_100_slow_internet));
            }
        }catch(JSONException je){
           // je.printStackTrace();
        }


    }

    private void loadImage(){
        Bitmap captureImage = new BitmapLoader().loadBitmap(defaultPath, 350, 300,check.getSiteCode());
        callList.get(CHECK_POSITION).setPostion(CHECK_POSITION);
        callList.get(CHECK_POSITION).setIsflag(true);
        callList.get(CHECK_POSITION).setBitmapValue(captureImage);
        callList.get(CHECK_POSITION).setColor(R.color.calllogbutton);
        logAdapter.notifyDataSetChanged();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    loadImage();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(CallLogActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
