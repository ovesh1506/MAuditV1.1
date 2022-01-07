package com.writercorporation.maudit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.writercorporation.adapeter.VisitPurposeAdapter;
import com.writercorporation.database.DatabaseManager;
import com.writercorporation.model.ImageModel;
import com.writercorporation.model.SiteList;
import com.writercorporation.model.VisitPurposeAnswer;
import com.writercorporation.model.VisitPurposeModel;
import com.writercorporation.utils.AppConstant;
import com.writercorporation.utils.BitmapLoader;
import com.writercorporation.utils.GPSTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class VisitActivity extends AppCompatActivity implements View.OnClickListener {

    TextView vp_codeShow, vp_NameShow, datePicker;
    Spinner vp_Reason, vp_optionalReson;
    EditText vp_remarks, vp_amount;
    Button vp_btnImage, vp_btnSubmit;
    ImageView vp_Image;
    AppConstant check;
    String defaultPath;
    File file;
    String dateValue;
    String selectedDate = "Select Date";
    DatabaseManager dManager = DatabaseManager.getInstance();
    ArrayList<VisitPurposeModel> visitPurposeModel;
    VisitPurposeAdapter visitAdapter;
    VisitPurposeModel model = null;
    String visitRemarks;
    Bitmap bitmap = null;
    String optionValue = null;
    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit);
        check = AppConstant.getInstance();
        vp_codeShow = (TextView) findViewById(R.id.vp_codeShow);
        vp_NameShow = (TextView) findViewById(R.id.vp_NameShow);
        datePicker = (TextView) findViewById(R.id.datePicker);
        vp_Reason = (Spinner) findViewById(R.id.vp_Reason);
        vp_remarks = (EditText) findViewById(R.id.vp_remarks);
        vp_btnImage = (Button) findViewById(R.id.vp_btnImage);
        vp_btnSubmit = (Button) findViewById(R.id.vp_btnSubmit);
        vp_Image = (ImageView) findViewById(R.id.vp_Image);
        vp_amount = (EditText) findViewById(R.id.vp_amount);
        vp_optionalReson = (Spinner) findViewById(R.id.vp_optionalReson);
        vp_btnImage.setOnClickListener(this);
        vp_btnSubmit.setOnClickListener(this);
        gps = new GPSTracker(this);
        if (check == null) {
            finish();
            return;
        }
        vp_codeShow.setText(check.getSiteCode());
        vp_NameShow.setText(check.getSiteName());
        try {
            if (dManager != null) {
                visitPurposeModel = dManager.getVisitPurposeList();
                visitAdapter = new VisitPurposeAdapter(getApplicationContext(), visitPurposeModel);
                vp_Reason.setAdapter(visitAdapter);
            }
        } catch (SQLException ex) {
        } catch (Exception ex) {
        }
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDate();
            }
        });
        vp_optionalReson.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                optionValue = (String) parent.getItemAtPosition(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        vp_Reason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //((TextView)view.findViewById(R.id.sp_textView)).getText().toString()
                model = (VisitPurposeModel) parent.getItemAtPosition(position);
                vp_optionalReson.setVisibility(View.GONE);
                datePicker.setVisibility(View.GONE);
                if (model.getControlType().equalsIgnoreCase("Date")) {
                    datePicker.setVisibility(View.VISIBLE);
                    vp_optionalReson.setVisibility(View.GONE);

                } else if (model.getControlType().equalsIgnoreCase("COMBO")) {
                    vp_optionalReson.setVisibility(View.VISIBLE);
                    datePicker.setVisibility(View.GONE);
                    getCombbo(model.getControlValues());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {

                try {
                    vp_Image.setVisibility(View.VISIBLE);
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(VisitActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    1);
                            return;
                        }
                        loadImage();
                    }
                    loadImage();
                } catch (Exception e) {
                    //e.printStackTrace();
                    check.showErrorMessage("" + e.getMessage());
                }

            }
        }
    }

    private void loadImage() {
        bitmap = new BitmapLoader().loadBitmap(defaultPath, 350, 300, check.getSiteCode());
        vp_Image.setImageBitmap(bitmap);


    }

    public void getCombbo(String value) {
        String comboValues = "Select," + value;
        String[] spinnerValues = comboValues.split(",");
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerValues);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        vp_optionalReson.setAdapter(spinnerArrayAdapter);

    }

    public void getDate() {
        final Dialog dialog = new Dialog(VisitActivity.this);

        dialog.setContentView(R.layout.custom_date_time);
        dialog.setTitle("Date & Time");
        dialog.show();
        final DatePicker dp = (DatePicker) dialog.findViewById(R.id.datePicker1);
        final TimePicker tp = (TimePicker) dialog.findViewById(R.id.timePicker1);
        final Button dateOkButn = (Button) dialog.findViewById(R.id.btnDateOK);
        final Button okButn = (Button) dialog.findViewById(R.id.btnOK);

        tp.setIs24HourView(true);
        dp.setVisibility(View.VISIBLE);
        dateOkButn.setVisibility(View.VISIBLE);
        tp.setVisibility(View.GONE);
        okButn.setVisibility(View.GONE);
        dateOkButn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateValue = String.format("%d-%02d-%02d", dp.getYear(), (dp.getMonth() + 1), dp.getDayOfMonth());
                dp.setVisibility(View.GONE);
                dateOkButn.setVisibility(View.GONE);
                tp.setVisibility(View.VISIBLE);
                okButn.setVisibility(View.VISIBLE);
            }
        });

        okButn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String dateAndTime = String.format("%d-%02d-%02d %02d:%02d:%02d", dp.getYear(), (dp.getMonth() + 1), dp.getDayOfMonth(), tp.getCurrentHour(), tp.getCurrentMinute(), 00);
                datePicker.setText(dateAndTime);
                selectedDate = dateAndTime;
                dialog.dismiss();
            }
        });


    }


    @Override
    public void onClick(View v) {

        try {


            switch (v.getId()) {
                case R.id.vp_btnSubmit:
                    if (bitmap != null && !model.getPurposeDesc().equalsIgnoreCase("Select Visit Purpose")) {
                        if (model.getControlType().equalsIgnoreCase("Date")) {
                            if (datePicker.getText().toString().equalsIgnoreCase("Select Date")) {
                                check.showErrorMessage("Please Select date");
                                return;
                            }

                        } else if (model.getControlType().equalsIgnoreCase("COMBO")) {
                            if (optionValue == null) {
                                check.showErrorMessage("Select Value");
                                return;
                            }
                        }
                        getJSON();
                    } else if (model.getPurposeDesc().equalsIgnoreCase("Select Visit Purpose"))
                        check.showErrorMessage("Select Visit Purpose");
                    else if (bitmap == null) {
                        check.showErrorMessage("Please capture image.");
                    }
                    break;
                case R.id.vp_btnImage:

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    defaultPath = getPathOfImage();
                    file = new File(defaultPath);
                    Uri outputfile = Uri.fromFile(file);

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputfile);
                    startActivityForResult(intent, 1);

                    break;
            }
        } catch (Exception e) {
         //   e.printStackTrace();
            check.showErrorMessage("" + e.getMessage());
        }
    }

    public void getJSON() {
        try {
            ArrayList<ImageModel> arrayListImage = new ArrayList<ImageModel>();
            JSONObject purposeObject = new JSONObject();
            purposeObject.put("PurposeID", model.getPurposeID());
            if (model.getControlType().equalsIgnoreCase("Date")) {
                purposeObject.put("PurposeData", selectedDate);

            } else if (model.getControlType().equalsIgnoreCase("COMBO")) {
                purposeObject.put("PurposeData", optionValue);
            } else
                purposeObject.put("PurposeData", " ");
            purposeObject.put("Remarks", visitRemarks);
            if (vp_amount.getText().toString().trim().length() == 0) {
                purposeObject.put("Amount", "0");
            } else
                purposeObject.put("Amount", vp_amount.getText().toString());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");
            JSONObject PurposeInfo = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(purposeObject);
            PurposeInfo.put("PurposeInfo", jsonArray);
            PurposeInfo.put("EODDate", sdf.format(new Date()));
            PurposeInfo.put("UserId", check.getUserID());
            PurposeInfo.put("PTransId", check.getPTransID());
            PurposeInfo.put("SiteId", check.getSiteID());
            PurposeInfo.put("Lattitude", gps.getLatitude());
            PurposeInfo.put("Longitude", gps.getLongitude());
            //  visitAnswer.clear();
            SiteList siteModel = dManager.getCompleteAllSites(check.getSiteID()).get(0);

            VisitPurposeAnswer visitPurposeData = new VisitPurposeAnswer(PurposeInfo.toString(), check.getSiteID(), check.getPTransID(), siteModel);
            // visitAnswer.add(visitPurposeData);
            dManager.insertVistPurposeAnswer(visitPurposeData);

            JSONObject imageJson = new JSONObject();
            imageJson.put("PTranID", check.getPTransID());
            imageJson.put("SiteId", check.getSiteID());
            JSONArray jsonimageArray = new JSONArray();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("AuditImage", BitmapLoader.BitMapToString(bitmap));
            jsonimageArray.put(jsonObject);

            imageJson.put("ImageData", jsonimageArray);
            ImageModel model = new ImageModel();

            model.setJsonString(imageJson.toString());
            model.setpTransId(check.getPTransID());
            model.setSiteId(check.getSiteID());
            model.setSyncStatus("UNSYNC");
            arrayListImage.add(model);
            dManager.insertImageModel(arrayListImage);

            Intent intent = new Intent(this, SiteListActivity.class);
            startActivity(intent);
            finish();
        } catch (JSONException ex) {
        } catch (java.sql.SQLException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    public String getPathOfImage() {
        Date currDate = new Date();
        long timeStamp = currDate.getTime();
        String folderPath = Environment.getExternalStorageDirectory()
                + "/maudit/images/";
        File folder = new File(folderPath);
        if (!folder.exists())
            folder.mkdirs();

        String extStorageDirectory = folder.toString();
        String newPath = "maudit_" + timeStamp + ".png";
        File newfolder = new File(folder, newPath);
        try {
            if (!newfolder.exists())
                newfolder.createNewFile();

        } catch (Exception e) {
            //e.printStackTrace();
        }
        return extStorageDirectory + "/maudit_" + timeStamp + ".png";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    loadImage();
                } else {

                    check.showErrorMessage("Permission denied to read your External storage");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    class VisitData extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(VisitActivity.this);
            pd.setCancelable(false);
            pd.setMessage("Please Wait...");
            pd.show();

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                ArrayList<ImageModel> arrayListImage = new ArrayList<ImageModel>();
                JSONObject purposeID = new JSONObject();
                purposeID.put("PurposeID", model.getPurposeID());
                if (model.getControlType().equalsIgnoreCase("Date")) {
                    purposeID.put("PurposeData", selectedDate);

                } else if (model.getControlType().equalsIgnoreCase("COMBO")) {
                    purposeID.put("PurposeData", optionValue);
                } else
                    purposeID.put("PurposeData", "null");
                purposeID.put("Remarks", visitRemarks);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");
                JSONObject PurposeInfo = new JSONObject();
                PurposeInfo.put("PurposeInfo", purposeID);
                PurposeInfo.put("EODDate", sdf.format(new Date()));
                PurposeInfo.put("UserId", check.getUserID());
                PurposeInfo.put("PTransId", check.getPTransID());
                PurposeInfo.put("SiteId", check.getSiteID());
                PurposeInfo.put("Lattitude", gps.getLatitude());
                PurposeInfo.put("Longitude", gps.getLongitude());
                VisitPurposeAnswer visitPurposeData = new VisitPurposeAnswer(PurposeInfo.toString(), check.getSiteID(), check.getPTransID());
                dManager.insertVistPurposeAnswer(visitPurposeData);

                JSONObject imageJson = new JSONObject();
                imageJson.put("PTranID", check.getPTransID());
                imageJson.put("SiteId", check.getSiteID());
                JSONArray jsonArray = new JSONArray();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("AuditImage", BitmapLoader.BitMapToString(bitmap));
                jsonArray.put(jsonObject);

                imageJson.put("ImageData", jsonArray);
                ImageModel model = new ImageModel();

                model.setJsonString(imageJson.toString());
                model.setpTransId(check.getPTransID());
                model.setSiteId(check.getSiteID());
                model.setSyncStatus("UNSYNC");
                arrayListImage.add(model);
                dManager.insertImageModel(arrayListImage);
            } catch (JSONException jsex) {
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }
    }
}
