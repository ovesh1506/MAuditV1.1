package com.writercorporation.maudit;


import android.Manifest;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.writercorporation.adapeter.SiteListAdapter;
import com.writercorporation.database.DatabaseManager;
import com.writercorporation.model.CallLoggedList;
import com.writercorporation.model.Login;
import com.writercorporation.model.SiteList;
import com.writercorporation.network.NetworkTask;
import com.writercorporation.network.OnTaskComplete;
import com.writercorporation.utils.AppConstant;
import com.writercorporation.utils.ConnectionDetector;
import com.writercorporation.utils.RecyclerViewOnTouchListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SiteList} interface
 * to handle interaction events.
 */
public class SiteListFragment extends Fragment implements SearchView.OnQueryTextListener, OnTaskComplete, View.OnKeyListener {
    //private OnFragmentInteractionListener mListener;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager recycleLayout;
    SiteListAdapter adapter;
    AppConstant check;
    ArrayList<SiteList> site = new ArrayList<SiteList>();
    SweetAlertDialog sweetAlertDialog;
    Dialog scanDialog = null;
    String title;
    private String siteCode;
    private String userId;
    private int siteId;
    int position1;
    String auditDate;
    DatabaseManager dManager;
    ConnectionDetector conn;
    String barcodeScanning;
    public String scanResult = "Tap To Scan";
    int siteSelectposition = -1;
    public int scanCount = 0;
    String[] failReason = {"Select Reason", "Barcode Not Available", "Barcode Damage",
            "Barcode mismatch", "Temporary Reason"};
    ArrayList<CallLoggedList> callLoggedListArrayList = null;
    String reasonScan = null;
    Spinner popupSpinner;
    Button dialogButton;
    TextView scannedText, lbltext;
    int wrongScan = 0;
    ArrayList<Login> isHousekeepingList;
    String isHousekeeping;

    public SiteListFragment() {
        // Required empty public constructor
    }

    public void showAlertChoice(final int position) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
                return;
            }

        }
        final CharSequence[] items = {
                "Do Visit", "Do Audit"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // builder.setTitle("Make your selection");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                // mDoneButton.setText(items[item]);
                if (item == 1)
                    doAudit(position);
                else
                    doVisit(position);

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.site_list, null);
        {
            dManager = DatabaseManager.getInstance();
            conn = new ConnectionDetector(getContext());
            try {
                isHousekeepingList = dManager.getHousekeeping();
                isHousekeeping = isHousekeepingList.get(0).getIsHousekeeping();
            } catch (Exception e) {
            }
            check = new AppConstant(getContext());
            sweetAlertDialog = check.showSweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE, false, "CallLog data downloading....", null);
            recyclerView = (RecyclerView) view.findViewById(R.id.list_recycle);
            recycleLayout = new LinearLayoutManager(getActivity());
            Bundle bundle = getArguments();
            recyclerView.setLayoutManager(recycleLayout);
            setHasOptionsMenu(true);
            if (bundle != null) {
                title = bundle.getString("Title");
                site = bundle.getParcelableArrayList(title);

            }

            /***********************Scan Dialog*************/

            scanDialog = new Dialog(getActivity());

            scanDialog.setContentView(R.layout.custom_scan_result_dialog);
            scanDialog.getWindow().setLayout(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            lbltext = (TextView) scanDialog.findViewById(R.id.lblScan);
            lbltext.setText("Site Code");
            if (Build.VERSION.SDK_INT < 23) {
                scanDialog.setTitle("Site Code");
                lbltext.setVisibility(View.GONE);
            }


            // set the custom dialog components - text, image and button
            popupSpinner = (Spinner) scanDialog.findViewById(R.id.sp_failreason);

            scannedText = (TextView) scanDialog.findViewById(R.id.textResult);
            scannedText.setText(scanResult);

            dialogButton = (Button) scanDialog.findViewById(R.id.dialogButtonOK);
            /***********************Scan Dialog End*************/

            recyclerView.setHasFixedSize(true);
            adapter = new SiteListAdapter(site, getActivity());
            recyclerView.setAdapter(adapter);


            recyclerView.addOnItemTouchListener(
                    new RecyclerViewOnTouchListener(getContext(), new RecyclerViewOnTouchListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            position1 = position;
                            if (Build.VERSION.SDK_INT >= 23) {
                                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(getActivity(),
                                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            1);
                                    return;
                                }

                            }
                            showAlertChoice(position);

                        }
                    })
            );
        }
        return view;
    }

    private void doVisit(int position) {
        siteCode = adapter.getItem(position).getSiteCode();

        siteSelectposition = position;
        userId = check.getUserID();
        auditDate = adapter.getItem(position).getLstAuditDate();
        siteId = adapter.getItem(position).getSiteID();
        check.setSitename(adapter.getItem(position).getSiteName());
        check.setSiteID(siteId);
        check.setSiteCode(siteCode);
        check.setUserID(userId);
        check.setPTransID();

        getActivity().startActivity(new Intent(getActivity(), VisitActivity.class));
    }

    private void doAudit(int position) {

        siteCode = adapter.getItem(position).getSiteCode();
        siteSelectposition = position;
        userId = check.getUserID();
        auditDate = adapter.getItem(position).getLstAuditDate();
        siteId = adapter.getItem(position).getSiteID();
        barcodeScanning = adapter.getItem(position).getIsBarcoded();
        if (barcodeScanning.equals("Y")) {
            scanCount = 0;
            scanResult = "";
            customScanResultDialog(position);
            return;
        } else {
            startAudit(position);
        }
    }


    public void startAudit(int position) {


        if (!check.getSiteCode().equalsIgnoreCase(adapter.getItem(position).getSiteCode())) {
            if (!"".equals(check.getSiteCode())) {
                final int pos = position;
                String message = "You have pending audit of site - " + check.getSiteCode() + "\n" + "Do you want to continue?";
                final SweetAlertDialog Sweetdialog = check.showSweetAlertDialog(getContext(), SweetAlertDialog.NORMAL_TYPE, false, "Alert!!!", message);

                Sweetdialog.setConfirmText("YES");
                Sweetdialog.setCancelText("NO");
                Sweetdialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        pendingCall(pos);
                        Sweetdialog.dismiss();
                    }
                });
                Sweetdialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Sweetdialog.dismiss();
                    }
                });
                Sweetdialog.show();
            } else {
                pendingCall(position);
            }

            return;
        }

        showAlertDialog(adapter.getItem(position).getSiteCode(), adapter.getItem(position).getSiteIsLogged(), position);
    }

    public void pendingCall(int position) {

        check.clearPrefs();
        check.setSiteID(siteId);
        check.setSiteCode(siteCode);
        check.setUserID(userId);
        check.setPTransID();
        check.setSiteLastPos(position);
        SiteListAdapter.selected_postion = position;
        SiteListAdapter.siteCode = siteCode;
        check.setSiteAuditDate(adapter.getItem(position).getLstAuditDate());
        Intent newActivity = new Intent();

        if (adapter.getItem(position).getSiteIsLogged().equalsIgnoreCase("Call log")) {

            try {
                if (conn.isConnectingToInternet()) {
                    if (!isHousekeeping.equals("true")) {
                        isCallLogDetail(siteId);
                    }
                    else{
                        newActivity.setClass(getActivity(), CategoryActivity.class);
                        startActivity(newActivity);
                    }

                } else {
                    if (isHousekeeping.equals("true")) {
                        newActivity.setClass(getActivity(), CategoryActivity.class);
                        startActivity(newActivity);
                    } else {
                        callLoggedListArrayList = dManager.getCallLogList(check.getSiteID());
                        if (callLoggedListArrayList.size() != 0) {
                            newActivity.setClass(getActivity(), CallLogActivity.class);
                            startActivity(newActivity);
                        } else
                            check.showErrorMessage(getString(R.string.error_100_slow_internet));
                    }

                }
            } catch (SQLException e) {
               // e.printStackTrace();
                check.showErrorMessage(getString(R.string.error_101_json));
            }

        } else {

            check.setSiteID(siteId);
            check.setSiteCode(siteCode);
            check.setPTransID();
            check.setUserID(userId);
            newActivity.setClass(getActivity(), CategoryActivity.class);
            startActivity(newActivity);
        }


        return;
    }

    public void isCallLogDetail(int siteID) {
        try {
            if (sweetAlertDialog != null)
                sweetAlertDialog.show();

            JSONObject rootJsonObject = new JSONObject();
            rootJsonObject.put("SiteID", siteID);
            if (conn.isConnectingToInternet()) {
                NetworkTask task = new NetworkTask(getActivity(), this, rootJsonObject.toString(), "GetCallLogDetails");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    task.execute();
                }
            } else {
                check.showErrorMessage(getString(R.string.error_100_slow_internet));
            }
        } catch (JSONException je) {
            //je.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_site, menu);
    }

    public void search(MenuItem item) {

        SearchView searchview = (SearchView) item.getActionView();
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchview.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(getContext(), SiteListActivity.class)));
        searchview.setOnQueryTextListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.search1:
                //MenuItem item=menu.findItem(R.id.search1);
                search(item);
                break;
            case R.id.copy:
                copyDataBaseToExternal();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // mListener = null;
    }

    public void copyDataBaseToExternal() {
        try {
            File sd = android.os.Environment.getExternalStorageDirectory();
            if (sd.canWrite()) {
                File currentDBPath = getActivity().getDatabasePath("MAudit.db");
                String backupDBPath = "WSG_Maudit_Mobile.db";
                File currentDB = new File(currentDBPath.toString());
                File backupDB = new File(sd.getAbsolutePath(), backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
                check.showErrorMessage("Copy database external path");
            }
        } catch (Exception e) {
           //
            // e.printStackTrace();
        }
    }

   /* public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/


    public void showAlertDialog(final String siteCode, final String callLogstatus, final int position) {

        final SweetAlertDialog alertDialog = check.showSweetAlertDialog(getContext(), SweetAlertDialog.NORMAL_TYPE, false, "Info!!!", "Do you want to continue with old data. \nPress Yes to continue and NO to fresh entry for " + siteCode + "?");
        alertDialog.setConfirmText("YES");
        alertDialog.setCancelText("NO");
        alertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                alertDialog.dismiss();
                Intent newActivity = new Intent();
                check.setSiteCode(siteCode);
                check.setUserID(userId);
                check.setPTransID();
                check.setSiteID(siteId);
                check.setSiteAuditDate(auditDate);
                SiteListAdapter.selected_postion = position;
                SiteListAdapter.siteCode = siteCode;
                check.setSiteLastPos(position);
                if (callLogstatus.equalsIgnoreCase("Call log")) {

                    try {

                        if (conn.isConnectingToInternet()) {
                            if (!isHousekeeping.equals("true")) {
                                isCallLogDetail(siteId);
                            }
                            else{
                                newActivity.setClass(getActivity(), CategoryActivity.class);
                                startActivity(newActivity);
                            }

                        } else {
                            if (isHousekeeping.equals("true")) {
                                newActivity.setClass(getActivity(), CategoryActivity.class);
                                startActivity(newActivity);
                            } else {
                                callLoggedListArrayList = dManager.getCallLogList(check.getSiteID());
                                if (callLoggedListArrayList.size() != 0) {
                                    newActivity.setClass(getActivity(), CallLogActivity.class);
                                    startActivity(newActivity);
                                } else
                                    check.showErrorMessage(getString(R.string.error_103_i_connection));
                            }
                        }
                    } catch (SQLException e) {
                       // e.printStackTrace();
                        check.showErrorMessage(getString(R.string.error_101_json));
                    }
                } else {
                    newActivity.setClass(getActivity(), CategoryActivity.class);
                    startActivity(newActivity);
                }
            }
        });
        alertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                check.clearPrefs();
                Intent newActivity = new Intent();
                check.setSiteCode(siteCode);
                check.setUserID(userId);
                check.setSiteID(siteId);
                check.setPTransID();
                check.setSiteAuditDate(auditDate);
                check.setSiteLastPos(position);
                SiteListAdapter.siteCode = siteCode;
                SiteListAdapter.selected_postion = position;
                if (callLogstatus.equalsIgnoreCase("Call log")) {
                    try {

                        if (conn.isConnectingToInternet()) {
                            if (!isHousekeeping.equals("true")) {
                                isCallLogDetail(siteId);
                            }
                            else{
                                newActivity.setClass(getActivity(), CategoryActivity.class);
                                startActivity(newActivity);
                            }

                        } else {

                            if (isHousekeeping.equals("true")){
                                newActivity.setClass(getActivity(), CategoryActivity.class);
                                startActivity(newActivity);
                            }
                            else {

                                callLoggedListArrayList = dManager.getCallLogList(check.getSiteID());
                                if (callLoggedListArrayList.size() != 0) {
                                    newActivity.setClass(getActivity(), CallLogActivity.class);
                                    startActivity(newActivity);
                                } else
                                    check.showErrorMessage(getString(R.string.error_100_slow_internet));
                            }
                        }

                    } catch (SQLException e) {
                       // e.printStackTrace();
                        check.showErrorMessage(getString(R.string.error_101_json));
                    }
                } else {
                    newActivity.setClass(getActivity(), CategoryActivity.class);
                    startActivity(newActivity);
                }
                alertDialog.cancel();

            }
        });
        alertDialog.show();

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final ArrayList<SiteList> filteredList = new ArrayList<>();
        for (int i = 0; i < site.size(); i++) {
            final SiteList text = site.get(i);
            if (text.getSiteCode().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(site.get(i));
            }
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new SiteListAdapter(filteredList, getActivity());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public String onTaskComplete(String result) {
        String response = "";
        try {
            dManager.DeleteCallLogList(check.getSiteID());
            JSONArray callLogArray = new JSONArray(result);


            if (callLogArray.length() != 0) {
                callLoggedListArrayList = new ArrayList<>();
                for (int j = 0; j < callLogArray.length(); j++) {
                    JSONObject callLogJsonObj = callLogArray.getJSONObject(j);
                    String callLogDate = callLogJsonObj.optString("CallLogDate");
                    String callLogID = callLogJsonObj.optString("CallLogID");
                    int callLogCheckPoint = callLogJsonObj.optInt("CheckPointItemID");
                    CallLoggedList callLoggedList = new CallLoggedList(check.getSiteID(), callLogID, callLogCheckPoint, callLogDate, "");
                    callLoggedListArrayList.add(callLoggedList);

                }

                dManager.insertCallLogedList(callLoggedListArrayList);
                response = "Success";
            } else if (callLogArray.length() == 0)
                response = "0";
            else
                response = getString(R.string.data_not_found);
        } catch (JSONException je) {
            response = getString(R.string.error_101_json);
        } catch (SQLiteException sle) {
           // response = "Error #105 " + sle.getMessage();
            check.showErrorMessage(getString(R.string.error_104_unexpected_error));
        } catch (Exception e) {
            //response = "Error #104 " + e.getMessage();
            check.showErrorMessage(getString(R.string.error_104_unexpected_error));
        }
        return response;

    }

    @Override
    public void onTaskComplete(String result, int id) {

    }

    @Override
    public void dismissDialog(String result) {

//        if (sweetAlertDialog != null)
//            sweetAlertDialog.dismiss();
        try {
            if (result.equalsIgnoreCase("Success")) {
                if (sweetAlertDialog != null) {
                    sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            if (sweetAlertDialog != null)
                                sweetAlertDialog.dismiss();

                            if (isHousekeeping.equals("true")) {
                                Intent newActivity = new Intent(getActivity(), CategoryActivity.class);
                                startActivity(newActivity);
                            } else {
                                Intent newActivity = new Intent(getActivity(), CallLogActivity.class);
                                startActivity(newActivity);
                            }

                        }
                    });
                }

            } else if (result.equalsIgnoreCase("0")) {
                check.showErrorMessage("Call Log List not found on server.");
                if (getActivity() != null) {
                    Intent newActivity1 = new Intent(getActivity(), CategoryActivity.class);
                    startActivity(newActivity1);
                }
            } else {
                check.showErrorMessage(result);
                if (sweetAlertDialog != null)
                    sweetAlertDialog.dismiss();
            }
        } catch (Exception e) {
            check.showErrorMessage("Call Log not downloaded");
        }
    }

    @Override
    public void onPublishResult(String msg) {

    }


    public void customScanResultDialog(final int position) {

        scannedText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wrongScan < 3) {
                    IntentIntegrator scanIntegrator = new IntentIntegrator(getActivity());
                    scanIntegrator.initiateScan();
                } else {
                    check.showErrorMessage("Wrong Scan Limit Over");
                    popupSpinner.setVisibility(View.VISIBLE);
                    scannedText.setVisibility(View.GONE);
                }
            }
        });

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (scannedText.getVisibility() == View.VISIBLE) {
                    String scannedSiteCode = scannedText.getText().toString();
                    if (!scannedSiteCode.equals("Tap To Scan")) {
                        if (scannedSiteCode.equals(siteCode)) {
                            check.showErrorMessage("Verification Successful");
                            pendingCall(position);
                        } else {
                            check.showErrorMessage("Verification Failed");
                            scannedText.setText("Tap To Scan");
                            wrongScan++;
                        }
                    } else {
                        check.showErrorMessage("Please scan the code");
                    }
                } else {
                    String selectedReason = popupSpinner.getSelectedItem().toString();
                    if (selectedReason.equals("Select Reason")) {
                        check.showErrorMessage("Please select scan failed reason");
                    } else {
                        pendingCall(position);
                    }
                }
            }
        });


        scanDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        scanResult = data.getStringExtra("SCAN_RESULT");
        scannedText.setText(scanResult);
        //customScanResultDialog();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {


        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    showAlertChoice(position1);
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
