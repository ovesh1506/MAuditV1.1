package com.writercorporation.maudit;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.core.os.CancellationSignal;

import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.writercorporation.adapeter.SiteListAdapter;
import com.writercorporation.database.DatabaseManager;
import com.writercorporation.model.Answers;
import com.writercorporation.model.CategoryList;
import com.writercorporation.model.Login;
import com.writercorporation.model.LoginReq;
import com.writercorporation.model.LoginResp;
import com.writercorporation.model.MicroCategory;
import com.writercorporation.model.QuestionList;
import com.writercorporation.model.SiteList;
import com.writercorporation.model.SubCategory;
import com.writercorporation.model.VisitPurposeModel;
import com.writercorporation.network.ApiService;
import com.writercorporation.network.NetworkTask;
import com.writercorporation.network.OnTaskComplete;
import com.writercorporation.utils.AppConstant;
import com.writercorporation.utils.ConnectionDetector;
import com.writercorporation.utils.GenericType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.writercorporation.maudit.SiteListFragment.isExternalStorageWritable;


public class LoginActivity extends AppCompatActivity implements OnTaskComplete, View.OnClickListener {

    Toolbar toolbar = null;
    private ConnectionDetector cd;
    private AppConstant check;
    private Button btnLogin;
    private Button btnCancel;
    private EditText edtUsername, url;
    private EditText edtPassword;
    // private ProgressDialog mDialog;
    private String usernameValue;
    private String passwordValue;
    DatabaseManager dManager;
    String versionName = null;
    String apkPath = null;
    String filenamme;
    SweetAlertDialog sweetAlertDialog;
    ImageView imageViewCaptcha, imageReload,imageFingerPrint;
    EditText editTextCaptcha;
    Button reloadCaptcha;
    TextView txtFingertprint;
    TextCaptcha textCaptcha;


    int numberOfCaptchaFalse = 0;
    private String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //check = (AppConstant) getApplicationContext();
        //AppConstant.initial(LoginActivity.this);
        Log.e("OnLogin","Called");
        check = AppConstant.getInstance();


        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Title text goes here")
                .setSubtitle("Subtitle goes here")
                .setDescription("This is the description")
                .setNegativeButtonText("Cancel")
                .build();

        SiteListAdapter.selected_postion = check.getSiteLastPos();
        try {
            versionName = (getPackageManager().getPackageInfo(LoginActivity.this.getPackageName(), 0).versionName);
        } catch (Exception e) {
         //   e.printStackTrace();
        }
        cd = new ConnectionDetector(this);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        //btnCancel = (Button) findViewById(R.id.btnCancel);
        edtUsername = (EditText) findViewById(R.id.username);
        edtPassword = (EditText) findViewById(R.id.password);
        editTextCaptcha = (EditText) findViewById(R.id.editTextCaptcha);
        imageViewCaptcha = (ImageView) findViewById(R.id.imageCaptcha);
        //reloadCaptcha = (Button) findViewById(R.id.reloadCaptcha);
        imageReload = (ImageView) findViewById(R.id.imageReload);
        imageFingerPrint = (ImageView) findViewById(R.id.img_fingerprint);
        txtFingertprint = findViewById(R.id.fingerprint_error);
        url = (EditText) findViewById(R.id.url);
        url.setText(check.SERVER_URL);
        textCaptcha = new TextCaptcha(600, 150, 4, TextCaptcha.TextOptions.LETTERS_ONLY);

        imageViewCaptcha.setImageBitmap(textCaptcha.getImage());
        //btnCancel.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        imageReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textCaptcha = new TextCaptcha(600, 150, 4, TextCaptcha.TextOptions.LETTERS_ONLY);
                imageViewCaptcha.setImageBitmap(textCaptcha.getImage());
            }
        });
//        reloadCaptcha.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                textCaptcha = new TextCaptcha(600, 150, 4, TextCaptcha.TextOptions.LETTERS_ONLY);
//                imageViewCaptcha.setImageBitmap(textCaptcha.getImage());
//            }
//        });

        dManager = DatabaseManager.getInstance();

        check.getPendingAlarmManager();
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LoginActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA},
                        2);
                return;
            }

        }
        if(check.getIsLock()){
            imageFingerPrint.setVisibility(View.VISIBLE);
            txtFingertprint.setVisibility(View.VISIBLE);
            callBiometric();
        }else{
            imageFingerPrint.setVisibility(View.GONE);
            txtFingertprint.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void callBiometric(){

        FingerprintManager fingerprintManager = null;
        try{
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                fingerprintManager = (FingerprintManager) this.getSystemService(Context.FINGERPRINT_SERVICE);
                Log.e("FingerPrint",fingerprintManager.toString());
                if (!fingerprintManager.isHardwareDetected()) {
                    //Log.e("Device not support","fingerprint");
                    // Device doesn't support fingerprint authentication
                    changeActivity();
                } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                    changeActivity();
                    Log.e("Device has not enrolled","fingerprint");
                    // User hasn't enrolled any fingerprints to authenticate with
                } else {
                    setFingerPrint();
                    //Log.e("Device support","fingerprint");
                    // Everything is ready for fingerprint authentication
                }
            }else{
                changeActivity();
                //imageFingerPrint.setVisibility(View.GONE);
                //txtFingertprint.setVisibility(View.GONE);
            }
        }catch(Exception ex){
            //setFingerPrint();
            changeActivity();
            ex.printStackTrace();
        }
    }


    public void setFingerPrint(){

        SweetAlertDialog sweetAlertDialog;

        if(check.getIsLock()){

            FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from(LoginActivity.this);

            fingerprintManagerCompat.authenticate(null, 0, new CancellationSignal(), new FingerprintManagerCompat.AuthenticationCallback() {
                @Override
                public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    Log.e("onAuthenticationSucceed","success");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        imageFingerPrint.setImageDrawable(LoginActivity.this.getDrawable(R.drawable.fingerprint_dialog_fp_to_error));
                    }else{
                        imageFingerPrint.setImageDrawable(LoginActivity.this.getResources().getDrawable(R.drawable.fingerprint_dialog_fp_to_error));
                    }
                    txtFingertprint.setText("Fingerprint recognised successfully");
                    changeActivity();
                }

                @Override
                public void onAuthenticationError(int errorCode, CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    Log.e("onAuthenticationError","success");
                }

                @Override
                public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                    super.onAuthenticationHelp(helpCode, helpString);
                    Log.e("onAuthenticationHelp","success");
                }



                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    //Log.e("onAuthenticationFailed","success");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        //imageFingerPrint.setImageDrawable(LoginActivity.this.getDrawable(R.drawable.fingerprint_dialog_error_to_fp));
                        //imageFingerPrint.setColorFilter(R.color.toastBg);
                        Drawable myIcon = LoginActivity.this.getDrawable( R.drawable.fingerprint_dialog_fp_to_error);
                        ColorFilter filter = new LightingColorFilter( Color.RED, Color.RED );
                        myIcon.setColorFilter(filter);
                        imageFingerPrint.setImageDrawable(myIcon);
                    }else{
                        //imageFingerPrint.setImageDrawable(LoginActivity.this.getResources().getDrawable(R.drawable.fingerprint_dialog_error_to_fp));
                        Drawable myIcon = LoginActivity.this.getResources().getDrawable( R.drawable.fingerprint_dialog_fp_to_error);
                        ColorFilter filter = new LightingColorFilter( Color.RED, Color.RED );
                        myIcon.setColorFilter(filter);
                        imageFingerPrint.setImageDrawable(myIcon);
                    }
                    txtFingertprint.setText("Fingerprint not recognized");
                }
            }, null);
        }else{
             sweetAlertDialog = new SweetAlertDialog(LoginActivity.this);
             sweetAlertDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
             sweetAlertDialog.setTitleText(AppConstant.LockTitleMsg);
             sweetAlertDialog.setCancelText("No");
             sweetAlertDialog.setConfirmText("Yes");
             sweetAlertDialog.show();

             sweetAlertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                 @Override
                 public void onClick(SweetAlertDialog sweetAlertDialog) {
                    Log.e("On Cancel button","click");
                    changeActivity();
                    sweetAlertDialog.dismiss();
                 }
             });

             sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                 @Override
                 public void onClick(SweetAlertDialog sweetAlertDialog) {
                     Log.e("On Allow button","click");
                     check.setIsLock(true);
                     changeActivity();
                     sweetAlertDialog.dismiss();
                 }
             });
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch ((id)) {
            case R.id.clearData:
                ClearDataAlert();

        }
        return super.onOptionsItemSelected(item);
    }

    public void ClearDataAlert() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("info!");
            String message = "Are you sure to clear data?";
            int CompletedUnsyncImages = dManager.getCompletedUnsyncImages().size();
            int CompletedUnsyncSites = dManager.getCompletedUnsyncSites().size();
            int UnsyncCallLogList = dManager.getUnsyncCallLogList().size();
            if (CompletedUnsyncImages > 0) {
                message += " Images sync pending = " + CompletedUnsyncImages;
            }
            if (CompletedUnsyncSites > 0)
                message += " Site detail sync pending = " + CompletedUnsyncSites;
            if (UnsyncCallLogList > 0)
                message += " CallLog sync pending = " + UnsyncCallLogList;

            builder.setMessage(message);
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    edtUsername.setText("");
                    edtPassword.setText("");
                    dManager.getHelper().clearAllData();
                    check.clearPrefs();
                    check.showErrorMessage("Data cleared.");
                }
            });
            builder.show();
        } catch (SQLException e) {
            check.showErrorMessage("# 104 Database Not Found");

        }
    }

    @Override
    public String onTaskComplete(String result) {
        String response = "";
        try {
            JSONObject rootObject = new JSONObject(result);
            String loginStatus = rootObject.optString("LoginStatus");

            if (loginStatus.equals("N")) {
                response = getString(R.string.error_102_invalid_login);

            } else {
                String ApkVersion = rootObject.optString("APKVers");
                String isHousekeeping = rootObject.optString("VarIsHouseKeeping");
//                if (versionName != null & ApkVersion != null && !versionName.trim().equalsIgnoreCase(ApkVersion.trim())) {
//                    apkPath = rootObject.optString("APKPath");
//                    return "apkPath";
//                    // return upgradeversion(apkPath);
//                }
                check.setUserID(usernameValue);
                dManager = DatabaseManager.getInstance();
                //Insertion of category list
                List<CategoryList> categoryList = new ArrayList<>();
                List<SubCategory> subCategoryList = new ArrayList<>();
                List<MicroCategory> microCategoryList = new ArrayList<>();
                List<QuestionList> questionListList = new ArrayList<>();
                List<Answers> answerList = new ArrayList<>();
                List<SiteList> siteArrayList = new ArrayList<>();
                List<VisitPurposeModel> visitPurpose = new ArrayList<>();

                JSONArray categoryListJson = rootObject.optJSONArray("HouseCat");
                JSONArray subCategoryListJson = rootObject.optJSONArray("HouseGrp");
                JSONArray microCategoryListJson = rootObject.optJSONArray("HouseSubGrp");
                JSONArray questionListJson = rootObject.optJSONArray("Quest");
                JSONArray siteList = rootObject.optJSONArray("SiteDet");
                JSONArray visitPurposeArr = rootObject.optJSONArray("VisitPurpose");
                if (siteList == null)
                    return "Sites not yet mapped to user.";

                for (int i = 0; i < categoryListJson.length(); i++) {
                    JSONObject categoryJsonObject = categoryListJson.getJSONObject(i);
                    CategoryList categoryModel = new CategoryList();
                    int serverCategoryId = categoryJsonObject.optInt("AuditCategoryID");
                    categoryModel.setCategoryName(categoryJsonObject.optString("AuditCategoryDesc"));
                    categoryModel.setCategoryServerId(serverCategoryId);
                    categoryList.add(categoryModel);

                    //Adding Subcategories in to database against category ID.
                    for (int s = 0; s < subCategoryListJson.length(); s++) {
                        JSONObject subCategoryJsonObject = subCategoryListJson.getJSONObject(s);
                        int masterCategoryId = subCategoryJsonObject.optInt("AuditCategoryID");
                        if (masterCategoryId == serverCategoryId) {
                            int subCategoryServerId = subCategoryJsonObject.optInt("CheckpointGroupID");
                            String subCategoryName = subCategoryJsonObject.optString("CheckpointGroupDesc");
                            String subCategoryOrder = subCategoryJsonObject.optString("CheckpointGroupOrder");
                            SubCategory subCategory = new SubCategory(subCategoryServerId, subCategoryName, categoryModel, subCategoryOrder);
                            subCategoryList.add(subCategory);

                            //Adding Micro-categories into database against SubcategoryId
                            for (int m = 0; m < microCategoryListJson.length(); m++) {
                                JSONObject microCategoryJsonObject = microCategoryListJson.getJSONObject(m);
                                int masterSubCategoryId = microCategoryJsonObject.optInt("CheckpointGroupID");
                                if (masterSubCategoryId == subCategoryServerId) {
                                    int microCategoryServerId = microCategoryJsonObject.optInt("CheckpointSubGroupID");
                                    String microCategoryName = microCategoryJsonObject.optString("CheckpointSubGroupDesc");
                                    int microCategoryOrder = microCategoryJsonObject.optInt("CheckpointGroupOrder");
                                    MicroCategory microCategory = new MicroCategory(microCategoryServerId, microCategoryName, microCategoryOrder, subCategory);
                                    microCategoryList.add(microCategory);

                                    //Getting Question List and Store into DB

                                    for (int q = 0; q < questionListJson.length(); q++) {
                                        JSONObject questionJsonObj = questionListJson.getJSONObject(q);
                                        int masterMicroCategoryId = questionJsonObj.optInt("CheckpointSubGroupID");
                                        if (microCategoryServerId == masterMicroCategoryId) {
                                            int questionServerId = questionJsonObj.optInt("CheckpointItemID");
                                            String questionText = questionJsonObj.optString("CheckpointItemDesc");
                                            int toShow = 1;
                                            String isParent = questionJsonObj.optString("IsParent");
                                            String isOnetime = questionJsonObj.optString("IsOneTime");
                                            int parentCheckpointId = questionJsonObj.optInt("ParentCheckpointItemID");
                                            int questionOrder = questionJsonObj.optInt("CheckpointItemOrder");
                                            Bitmap cameraIcon = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_camera);
                                            QuestionList questionList = new QuestionList(questionServerId, questionText, toShow, isParent, parentCheckpointId, questionOrder, microCategory, isOnetime, cameraIcon, cameraIcon);
                                            questionListList.add(questionList);

                                            //GETTING ANSWERS
                                            JSONArray answerArray = questionJsonObj.getJSONArray("Ans");
                                            for (int a = 0; a < answerArray.length(); a++) {
                                                JSONObject answerJsonObj = answerArray.getJSONObject(a);
                                                int id = answerJsonObj.optInt("CheckpointItemRemarkID");
                                                String answerText = answerJsonObj.optString("CheckpointItemRemarks");
                                                String callLog = answerJsonObj.optString("IsCallLog");
                                                String controlCaption = answerJsonObj.optString("ControlCaption");
                                                String controlType = answerJsonObj.optString("ControlType");
                                                String textInputType = answerJsonObj.optString("ControlValueType");
                                                String isChildShow = answerJsonObj.optString("IsChildShow");
                                                String controlValue = answerJsonObj.optString("ControlValue");
                                                Answers answers = new Answers(id, answerText, callLog, controlCaption, controlType, textInputType, isChildShow, controlValue, questionList);
                                                answerList.add(answers);

                                            }
                                        }
                                    }

                                }
                            }

                            for (int si = 0; si < siteList.length(); si++) {
                                JSONObject siteJsonObject = siteList.getJSONObject(si);
                                int siteId = siteJsonObject.optInt("SiteID");
                                String siteCode = siteJsonObject.optString("SiteCode");
                                String atmCode = siteJsonObject.optString("ATMCode");
                                String siteName = siteJsonObject.optString("SiteName");
                                String lstAuditDate = siteJsonObject.optString("LastAuditDate");
                                String siteAddress = siteJsonObject.optString("SiteAddressLine1");
                                String siteIsLogged = siteJsonObject.optString("IsCallLogged");
                                String isBarcoded = siteJsonObject.optString("IsBarcoded");
                                //String isBarcoded = "Y";


                                SiteList site = new SiteList(siteId, siteCode, atmCode ,siteName, siteIsLogged, "UNSYNC", lstAuditDate, siteAddress, isBarcoded);
                                siteArrayList.add(site);
                            }
                        }
                    }

                }
                if (categoryList.size() != 0)
                    dManager.insertCategoryList(categoryList);
                else
                    return "Category not given";
                if (subCategoryList.size() != 0)
                    dManager.insertSubCategory(subCategoryList);
                else
                    return "Data not Found";
                if (microCategoryList.size() != 0)
                    dManager.insertMicroCategory(microCategoryList);
                else
                    return "Data not Found";
                if (questionListList.size() != 0)
                    dManager.insertQuestionList(questionListList);
                else
                    return "Data not Found";
                if (answerList.size() != 0)
                    dManager.insertAnswers(answerList);
                else
                    return "Data not Found";
                dManager.insertSites(siteArrayList);

                /**********Adding Visit Purposes in Database**********/
                for (int i = 0; i < visitPurposeArr.length(); i++) {
                    JSONObject visitObj = visitPurposeArr.getJSONObject(i);
                    VisitPurposeModel visit = new VisitPurposeModel();
                    visit.setControlCaption(visitObj.optString("ControlCaption"));
                    visit.setControlType(visitObj.optString("ControlType"));
                    visit.setControlValues(visitObj.optString("ControlValues"));
                    visit.setPurposeDesc(visitObj.optString("PurposeDesc"));
                    visit.setPurposeID(visitObj.optString("PurposeID"));
                    visitPurpose.add(visit);
                }

                dManager.insertVisitPurpose(visitPurpose);
                Calendar c = Calendar.getInstance();
                System.out.println("Current time => " + c.getTime());

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String logindatetime = df.format(c.getTime());

                Login login = new Login(usernameValue, passwordValue, logindatetime, "" + isHousekeeping);
                dManager.insertLogin(login);
                check.setUserID(usernameValue);

//                copyDataBaseToExternal();
                response = getString(R.string.success);
            }
        } catch (JSONException je) {
            response = getString(R.string.error_101_json);
        } catch (SQLiteException sle) {
            response = getString(R.string.error_101_json);
           // response = "Error #105 " + sle.getMessage();
        } catch (SQLException se) {
            response = getString(R.string.error_101_json);
           // response = "Error #106 " + se.getMessage();
        } catch (Exception e) {
            response = getString(R.string.error_104_unexpected_error);
            //response = "Error #104 " + e.getMessage();
        }
        return response;
    }

    @Override
    public void onTaskComplete(String result, int id) {

    }

    @Override
    public void dismissDialog(String result) {

        if (sweetAlertDialog != null) {
            sweetAlertDialog.dismiss();
        }
      /*  if(mDialog!=null)
            mDialog.dismiss();*/
        if (result.equalsIgnoreCase("apkPath")) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(LoginActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE},
                            1);
                    return;
                }
                callBiometric();
                //DownloadAPK downloadAPK = new DownloadAPK();
                //downloadAPK.execute();
            }
            callBiometric();
            //DownloadAPK downloadAPK = new DownloadAPK();
            //downloadAPK.execute();
            return;
        } else if (!result.equals(getString(R.string.success))) {
            check.showErrorMessage(result);
            return;
        }
        callBiometric();
        //changeActivity();
        //Intent intent = new Intent(this, SiteListActivity.class);
        //startActivity(intent);
    }

    public void changeActivity(){
        Intent intent = new Intent(this, SiteListActivity.class);
        startActivity(intent);

    }

    @Override
    public void onPublishResult(String msg) {
        if (sweetAlertDialog != null)
            sweetAlertDialog.setContentText(msg);
    }

    @Override
    public void onClick(View view) {
        view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.click_animation));
        switch (view.getId()) {
//            case R.id.btnCancel:
//                finish();
//                break;
            case R.id.btnLogin:
                doLogin();
                break;
        }
    }

    private void doLogin() {
        usernameValue = edtUsername.getText().toString().trim();
        passwordValue = edtPassword.getText().toString().trim();
        if (!textCaptcha.checkAnswer(editTextCaptcha.getText().toString().trim())) {
            editTextCaptcha.setError("Captcha does not match");
            numberOfCaptchaFalse++;
        } else {
          //  Toast.makeText(getApplicationContext(), "Captcha Matched...",
           //         Toast.LENGTH_SHORT).show();

            try {
                if (usernameValue.length() == 0) {
                    check.showErrorMessage(getString(R.string.enter_username));
                    //  mDialog.dismiss();
                    return;
                } else if (passwordValue.length() == 0) {
                    check.showErrorMessage(getString(R.string.enter_password));
                    //       mDialog.dismiss();

                    return;
                }
                if (dManager.authenticateLocal(usernameValue, passwordValue)) {
                    edtUsername.setHint("Username");
                    edtPassword.setHint("Password");
                    Intent intent = new Intent(this, SiteListActivity.class);
                    startActivity(intent);
                    return;
                } else {
                    if (dManager.authenticateCount(usernameValue, passwordValue) == 1) {
                        check.showErrorMessage(getString(R.string.error_102_invalid_login));
                        textCaptcha = new TextCaptcha(600, 150, 4, TextCaptcha.TextOptions.LETTERS_ONLY);
                        imageViewCaptcha.setImageBitmap(textCaptcha.getImage());

                        //  alertChangeUserLogin();
                    } else {
                        internetLogin();
                    }

                }


                if (!cd.isConnectingToInternet()) {
                    check.showErrorMessage(getString(R.string.error_103_i_connection));
                    //  mDialog.dismiss();
                    return;
                }

            } catch (SQLException e) {
              //
                //  e.printStackTrace();
            }
        }


    }

    public void alertChangeUserLogin() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("info!");
            String message = "Are you sure to change USER name.\nPress Yes.";


            builder.setMessage(message);
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    check.showErrorMessage("Please enter Correct username & password");
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {

                        internetLogin();
                    } catch (Exception e) {
                    }
                }
            });
            builder.show();
        } catch (Exception e) {
            check.showErrorMessage(getString(R.string.error_105_database_not_found));

        }
    }

    public void internetLogin() {
        try {
            sweetAlertDialog = null;
            sweetAlertDialog = check.showSweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE, false, "Authenticating....", null);
            sweetAlertDialog.show();
          /*  mDialog.setMessage("Authenticating...");
            mDialog.show();*/
            edtUsername.setHint("Username");
            edtPassword.setHint("Password");
            dManager.getHelper().clearAllData();
            LoginReq req = new LoginReq();
            req.setType("LoginInput:#MAuditRestService");
            req.setUserName(usernameValue);
            req.setPassword(passwordValue);
            req.setImei("1234");
//            JSONObject rootJsonObject = new JSONObject();
//            rootJsonObject.put("_type", "LoginInput:#MAuditRestService");
//            rootJsonObject.put("UserName", usernameValue);
//            rootJsonObject.put("Password", passwordValue);
//            rootJsonObject.put("IMEINO", "1234");
            check.setUserID(usernameValue);
            Log.e(" Login Request ",req.toString());

            GenericType<LoginReq> data= new GenericType(req);

            NetworkTask task = new NetworkTask(this, this,data, "Login");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                //task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                //task.execute();
            }
        } catch (Exception je) {
            sweetAlertDialog.dismiss();
            //je.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sweetAlertDialog != null && sweetAlertDialog.isShowing())
            sweetAlertDialog.dismiss();

        /*if(mDialog!=null && mDialog.isShowing())
            mDialog.dismiss();*/
    }

    /* public void copyDataBaseToExternal() {
            try {
                File sd = android.os.Environment.getExternalStorageDirectory();
                if (sd.canWrite()) {
                    File currentDBPath=getDatabasePath("MAudit.sqlite");
                    String backupDBPath = "WSG_Maudit_Mobile";
                    File currentDB = new File(currentDBPath.toString());
                    File backupDB = new File(sd.getAbsolutePath(),backupDBPath);


                    if (currentDB.exists()) {
                        FileChannel src = new FileInputStream(currentDB).getChannel();
                        FileChannel dst = new FileOutputStream(backupDB).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED) {

                    DownloadAPK downloadAPK = new DownloadAPK();
                    downloadAPK.execute();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(LoginActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case 2:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED
                        && grantResults[4] == PackageManager.PERMISSION_GRANTED) {


                } else {
                    finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(LoginActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    class DownloadAPK extends AsyncTask<String, String, String> {
        ProgressDialog pd = null;

        SweetAlertDialog alertDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            alertDialog=check.showSweetAlertDialog(LoginActivity.this,SweetAlertDialog.PROGRESS_TYPE,false,"Apk Downloading ",null);
            if(alertDialog!=null)
                alertDialog.show();
            pd = new ProgressDialog(LoginActivity.this);
            pd.setCancelable(false);
            pd.setMessage("Apk Downloading");
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            if (pd != null)
                pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                File root = null;

                if(isExternalStorageWritable()){
                    root = LoginActivity.this.getExternalFilesDir(null);
                }else{
                    root = Environment.getExternalStorageDirectory();
                }

                //File root = android.os.Environment.getExternalStorageDirectory();
                long total = 0;
                File dir = new File(root.getAbsolutePath() + "/wsgMauditapk");
                if (dir.exists() == false) {
                    dir.mkdirs();
                }
                if (apkPath == null)
                    return "Apk Not found";
                else
                    apkPath = "https://wsg.writercorporation.com/apkfile/MAudit_1.0.0.9.apk";
                URL url = new URL(apkPath); // you can write here any link
                String[] parts = apkPath.split("/");
                filenamme = parts[parts.length - 1];
                File file = new File(dir, filenamme);


					/* Open a connection to that URL. */
                HttpURLConnection ucon = (HttpURLConnection) url.openConnection();
                ucon.setRequestProperty("Accept-Encoding", "identity"); // <--- Add
                ucon.connect();
                int lenghtOfFile = ucon.getContentLength();
                InputStream in = new BufferedInputStream(url.openStream(), lenghtOfFile);
                OutputStream out = new FileOutputStream(file);
                byte[] buf = new byte[1024];
                int n = 0;

                while (-1 != (n = in.read(buf))) {
                    total += n;
                    out.write(buf, 0, n);
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                }
                out.flush();
                out.close();
                in.close();
/*
                byte[] response = out.toByteArray();

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(response);
                fos.close();
*/

            } catch (IOException e) {
                Log.e("DownloadManager", "Error: " + e);
            }
            return "Done";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            for (int i = 0; i < values.length; i++)
                pd.setProgress(Integer.parseInt(values[i]));


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (pd != null) {
                pd.dismiss();

            }
            if (s.equalsIgnoreCase("Done")) {
                /*Intent installapp = null;//new Intent(Intent.ACTION_VIEW);
                Uri uri = null;
                if(isExternalStorageWritable()){
                    //uri = Uri.fromFile(new File(LoginActivity.this.getExternalFilesDir(null) + "/wsgMauditapk/" + filenamme));
                    uri = FileProvider.getUriForFile(LoginActivity.this, getApplicationContext().getPackageName() + ".provider", new File(LoginActivity.this.getExternalFilesDir(null) + "/wsgMauditapk/" + filenamme));

                }else{
                    uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/wsgMauditapk/" + filenamme));//"MAudit_1.0.0.5.apk"
                }


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    installapp = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                    installapp.setData(uri);
                    installapp.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                }else{
                    installapp = new Intent(Intent.ACTION_VIEW);
                    installapp.setDataAndType(uri, "application/vnd.android.package-archive");
                    installapp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
//                installapp.setDataAndType(uri, "application/vnd.android.package-archive");
                    startActivity(installapp);*/
                Intent intent = new Intent(LoginActivity.this, SiteListActivity.class);
                startActivity(intent);
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        edtUsername.setText("");
        edtPassword.setText("");
        moveTaskToBack(true);
        finish();
    }

}

