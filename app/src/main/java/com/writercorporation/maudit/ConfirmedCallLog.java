package com.writercorporation.maudit;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.writercorporation.adapeter.ConfirmCallLogAdapter;
import com.writercorporation.adapeter.OnConfirmCallLogListener;
import com.writercorporation.database.DatabaseManager;
import com.writercorporation.model.CategoryList;
import com.writercorporation.model.CompletedSiteInfo;
import com.writercorporation.model.Login;
import com.writercorporation.model.QuestionList;
import com.writercorporation.model.SiteList;
import com.writercorporation.utils.AppConstant;
import com.writercorporation.utils.BitmapLoader;
import com.writercorporation.utils.GPSTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static com.writercorporation.maudit.SiteListFragment.isExternalStorageWritable;

public class ConfirmedCallLog extends AppCompatActivity implements OnConfirmCallLogListener{

    ActionBar actionBar;
    AppConstant check;
    Toolbar toolbar;
    ListView lvCallLog;
    DatabaseManager dManager;
    ArrayList<QuestionList> value;
    ConfirmCallLogAdapter adapter;
    ArrayList<Integer> hiddenPositions;
    GPSTracker gps;
    String path;
    int temppost;
    ArrayList<Login> isHousekeepingList;
    String isHousekeeping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dManager=DatabaseManager.getInstance();
        try{
            isHousekeepingList=dManager.getHousekeeping();
            isHousekeeping = isHousekeepingList.get(0).getIsHousekeeping();
        }
        catch (Exception e)
        {

        }

        check=AppConstant.getInstance();

        gps = new GPSTracker(this);

        value = new ArrayList<QuestionList>();

        if (isHousekeeping.equals("true")){
            ConfirmedCallLog.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new LoadCallLogList().execute();

                    new StoreCallLogData().execute();
                }
            });

        }
        else{
            setContentView(R.layout.activity_confirmed_call_log);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            actionBar = getSupportActionBar();
            lvCallLog=(ListView)findViewById(R.id.lvCallLog);
            new LoadCallLogList().execute();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.call_log_submit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch ((id))
        {
            case R.id.submit_callLog:
                showAlertDialog();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setImageListener( ArrayList<QuestionList> value, int pos,String path) {
        temppost=pos;
        this.path=path;
    }


    class LoadCallLogList extends AsyncTask<String,String,String>
    {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(ConfirmedCallLog.this);
            pd.setCancelable(false);
            pd.setMessage("Loading...");
            pd.show();

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                ArrayList<CategoryList> categoryLists = dManager.getCategoryList();
                hiddenPositions = new ArrayList<>();
                for(CategoryList category : categoryLists){
                    ArrayList<QuestionList> questionLists = check.getCategoryData(category.getCategoryName());
                    value.addAll(questionLists);
                }
                Integer[] callLogOpenId=dManager.getCallLogQuestionID(check.getSiteID());
                for(int i=0;i<value.size();i++){
                    QuestionList que = value.get(i);
                    if(que.getCheckedAnswer()==null)
                        hiddenPositions.add(i);

                    else if (!que.getCheckedAnswer().getCallLog().toLowerCase().equalsIgnoreCase("true"))
                            hiddenPositions.add(i);

                    else if(Arrays.asList(callLogOpenId).contains(que.getId()))
                        hiddenPositions.add(i);
                }


            } catch (SQLException e) {
               // e.printStackTrace();
                return "SQLException";
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if("SQLException".equals(s))
            {
                //check.showErrorMessage("Error #106 SQLException.");
                check.showErrorMessage(getString(R.string.error_104_unexpected_error));
                return;
            }
            if(value!=null)
            {
                setData();
            }
            if(pd!=null)
                pd.dismiss();
        }
    }
    public void setData()
    {


        if (isHousekeeping.equals("true")){
            adapter=new ConfirmCallLogAdapter(ConfirmedCallLog.this,value,hiddenPositions,this);
            adapter.notifyDataSetChanged();
        }
        else{
            adapter=new ConfirmCallLogAdapter(ConfirmedCallLog.this,value,hiddenPositions,this);
            lvCallLog.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }
    class StoreCallLogData extends AsyncTask<String,String,String>
    {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(ConfirmedCallLog.this);
            pd.setCancelable(false);
            pd.setMessage("Please Wait...");
            pd.show();

        }

        @Override
        protected String doInBackground(String... strings) {
            ArrayList<QuestionList> upDatedList = new ArrayList<QuestionList>();
            if (isHousekeeping.equals("true")) {
                upDatedList.addAll(value);
            }
            else{
                upDatedList = adapter.qlist;
            }
            try {
                JSONObject rootObject = new JSONObject();// HH:mm:SS
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");
                rootObject.put("EODDate", sdf.format(new Date()));
                rootObject.put("UserId", check.getUserID());
                rootObject.put("PTransId",check.getPTransID());
                rootObject.put("SiteId",check.getSiteID());
                rootObject.put("Lattitude",gps.getLatitude());
                rootObject.put("Longitude",gps.getLongitude());
                JSONArray questionInfo = new JSONArray();
                for (QuestionList question:upDatedList){
                   if(question.getCheckedAnswer()==null)
                        continue;

                    JSONObject questionObj = new JSONObject();

                    if(question.isMakeCallLog() && question.getCheckedAnswer().getCallLog().equalsIgnoreCase("true")) {
                        questionObj.put("CallLog", "true");

                    }
                    else
                        questionObj.put("CallLog","false");

                    if(question.getConfirmedImage1()!=null)
                    {
                        questionObj.put("CallLogImg1",BitmapLoader.BitMapToString(question.getConfirmedImage1()));
                    }
                    else
                        questionObj.put("CallLogImg1","");
                    if(question.getConfirmedImage2()!=null)
                    {
                        questionObj.put("CallLogImg2",BitmapLoader.BitMapToString(question.getConfirmedImage2()));
                    }
                    else
                        questionObj.put("CallLogImg2","");

                    if(question.getPriority()!=null){
                        questionObj.put("Priority",question.getPriority());
                        questionObj.put("PriorityRemark",question.getRemarks());
                    }else{

                        questionObj.put("Priority","");
                        questionObj.put("PriorityRemark","");
                    }

                    questionObj.put("QuestionID", question.getId());
                    questionObj.put("ExtraFieldValue",question.getExtraFieldValue());
                    questionObj.put("AnswerID", question.getCheckedAnswer().getId());
                    questionInfo.put(questionObj);

                }
                rootObject.put("questionInfo", questionInfo);
                SiteList siteModel=dManager.getCompleteAllSites(check.getSiteID()).get(0);
                CompletedSiteInfo siteInfo = new CompletedSiteInfo(rootObject.toString(),check.getSiteID(),siteModel);
                dManager.insertCompletedSites(siteInfo);

                return "DONE";

            }catch(JSONException je)
            {
               // je.printStackTrace();
                return je.getMessage();
            } catch (Exception e) {
               // e.printStackTrace();
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if("DONE".equals(s))
            {
                Intent intent = new Intent(getApplicationContext(), ImageCapture.class);
                startActivity(intent);
                finish();
            }
            else
            {
                //check.showErrorMessage("Error #104 "+s);
                check.showErrorMessage(getString(R.string.error_104_unexpected_error));
            }

            if(pd!=null)
                pd.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            if (resultCode == RESULT_OK) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ConfirmedCallLog.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                1);
                        return;
                    }
                    loadImage(requestCode);
                }
                loadImage(requestCode);
            }
            }
        catch (Exception e){
            //e.printStackTrace();
        }
    }

    public void getAlertMessage()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("info!");
        builder.setMessage("Are you sure to have 2nd image for this calllog? ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file=new File(path);
                Uri outputfile = null;//Uri.fromFile(file);

                if(isExternalStorageWritable()){
                    //uri = Uri.fromFile(new File(LoginActivity.this.getExternalFilesDir(null) + "/wsgMauditapk/" + filenamme));
                    outputfile = FileProvider.getUriForFile(ConfirmedCallLog.this, getApplicationContext().getPackageName() + ".provider", file);
                }else{
                    outputfile = Uri.fromFile(file);//"MAudit_1.0.0.5.apk"
                }

                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputfile);
                startActivityForResult(intent, ConfirmCallLogAdapter.CALLLOGIMAGE2);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // CheckBox ch = (CheckBox) view;
              /*  QuestionList que = qlist.get(position);
                que.setMakeCallLog(holder.checkBox.isChecked());
                qlist.set(position, que);
                notifyDataSetChanged();*/
            }
        });
        builder.show();
    }
    public void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("info!");
        builder.setMessage("Once you submit data you can't change the data. Press YES to confirm, Press NO to cancel.");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showAlertDialogSecond();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    public void showAlertDialogSecond() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("info!");
        builder.setMessage("Are You Sure?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new StoreCallLogData().execute();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    loadImage(requestCode);
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(ConfirmedCallLog.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    private void loadImage(int requestCode){
        if (requestCode == ConfirmCallLogAdapter.CALLLOGIMAGE1) {
            Bitmap captureImage = new BitmapLoader().loadBitmap(path,350, 300, check.getSiteCode());
            QuestionList temp=value.get(temppost);
            temp.setConfirmedImage1(captureImage);
            value.set(temppost,temp);
            getAlertMessage();
        }
        else if(requestCode==ConfirmCallLogAdapter.CALLLOGIMAGE2)
        {
            Bitmap captureImage = new BitmapLoader().loadBitmap(path,350, 300, check.getSiteCode());
            QuestionList temp=value.get(temppost);
            temp.setConfirmedImage2(captureImage);
            value.set(temppost,temp);
        }
        adapter.notifyDataSetChanged();
    }
}
