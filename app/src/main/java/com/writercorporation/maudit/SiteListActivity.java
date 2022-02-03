package com.writercorporation.maudit;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.writercorporation.database.DatabaseManager;
import com.writercorporation.model.SiteList;
import com.writercorporation.utils.AppConstant;

import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class SiteListActivity extends AppCompatActivity {
    ActionBar actionBar;
    Toolbar toolbar;
    private AppConstant check;
    TabLayout tab;
    ViewPager vw;
    DatabaseManager dManager =  DatabaseManager.getInstance();
    List<SiteList> siteLists=new ArrayList<>();
    ArrayList<SiteList> pending=new ArrayList<>();
    ArrayList<SiteList> completed=new ArrayList<>();
    ArrayList<SiteList> synced=new ArrayList<>();
    ArrayList<SiteList> unsynced=new ArrayList<>();
    ArrayList<SiteList> imageUnsynced=new ArrayList<>();
    ArrayList<SiteList> callLog=new ArrayList<>();
    SiteListFragment siteListFragment;
    Handler mHandler;
    Runnable statusCheker;
    ViewpagerAdapter viewAdapter;
    int currentpos=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_list);
        toolbar = (Toolbar) findViewById(R.id.toolbar);


        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.toolbarContainer);
        appBarLayout.setExpanded(true, true);
        appBarLayout.setExpanded(false, true);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        check = new AppConstant();
        if(actionBar!=null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        try {
            vw = (ViewPager) findViewById(R.id.viewDesign);
            tab = (TabLayout) findViewById(R.id.statusTab);



            mHandler=new Handler();
             statusCheker=new Runnable() {
                @Override
                public void run() {
                    try {
                        AsyncTaskResponse response=new AsyncTaskResponse(SiteListActivity.this);
                        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB )
                            response.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        else
                            response.execute();
                    }
                    catch (Exception e)
                    {
                       // e.printStackTrace();;
                    }
                    mHandler.postDelayed(statusCheker,100000);
                }
            };
            statusCheker.run();

            vw.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    if(vw.getAdapter()!=null){
                        currentpos=position;
                    }
                }
                @Override
                public void onPageSelected(int position) {
                    if(vw.getAdapter()!=null){
                        currentpos=position;
                    }
                }
                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }
        catch(Exception e){
           // check.showErrorMessage("Error #104 "+e.getMessage());
            check.showErrorMessage(getString(R.string.error_104_unexpected_error));
        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SiteListActivity.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
                return;
            }
            //loadImage();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {

        if (requestCode == 49374) {
            if (resultCode == RESULT_OK) {
                final String  scannedcode = intent.getStringExtra("SCAN_RESULT");
                Fragment fragment=    viewAdapter.getItem(currentpos);
                if (fragment != null){
                    fragment.onActivityResult(requestCode, resultCode, intent);
                }
            } else if (resultCode ==RESULT_CANCELED) {
                check.showErrorMessage("SCAN CANCELLED");
            }
        }
    }

    public void setupWithViewPager(ViewPager vw) {

        viewAdapter = new ViewpagerAdapter(getSupportFragmentManager());

        ArrayList<SiteList> siteArrayList = new ArrayList<>(siteLists.size());
        siteArrayList.addAll(siteLists);
        siteListFragment = new SiteListFragment();
        viewAdapter.addFragment(siteListFragment, "TOTAL", siteArrayList);
        siteListFragment = new SiteListFragment();
        viewAdapter.addFragment(siteListFragment, "PENDING",pending);
        siteListFragment = new SiteListFragment();
        viewAdapter.addFragment(siteListFragment, "COMPLETED",completed);
        siteListFragment = new SiteListFragment();
        viewAdapter.addFragment(siteListFragment, "CallLog",callLog);
        siteListFragment = new SiteListFragment();
        viewAdapter.addFragment(siteListFragment, "SYNCED", synced);
        siteListFragment = new SiteListFragment();
        viewAdapter.addFragment(siteListFragment, "UNSYNCED", unsynced);
        siteListFragment = new SiteListFragment();
        viewAdapter.addFragment(siteListFragment, "IMAGE UNSYNCED", imageUnsynced);
        vw.setAdapter(viewAdapter);
        viewAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void tabDesign(TabLayout tab, String title, String detail, int position) {
        TabLayout.Tab customTab = tab.getTabAt(position);
        View linearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.tabdesign, null);
        TextView txtTitle = (TextView) linearLayout.findViewById(R.id.txtTitle);
        TextView txtdetail = (TextView) linearLayout.findViewById(R.id.txtdetail);
        txtTitle.setText(title);
        txtdetail.setText(detail);
        customTab.setCustomView(linearLayout);
        //customTab.select();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                Intent intent = new Intent(SiteListActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                startActivity(intent);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }



    class ViewpagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        private String fragmentname;
        public ViewpagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title,ArrayList<SiteList> siteLists) {

            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
            Bundle b = new Bundle();
            b.putString("Title", title);
            b.putParcelableArrayList(title,siteLists);
            fragment.setArguments(b);
            getSupportFragmentManager().beginTransaction().add(fragment,title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    class AsyncTaskResponse extends AsyncTask<String,String,String>
    {
        ProgressDialog pd;
        Context context;
        AsyncTaskResponse(Context context)
        {
            this.context=context;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd=new ProgressDialog(context);
            pd.setCancelable(false);
            pd.setMessage("Please wait....");
            /*try {
              //  if(pd!=null)
               //     pd.show();
            }
            catch (Exception e){}*/

        }

        @Override
        protected String doInBackground(String[] params) {
            try {
                siteLists = dManager.getAllSites();
                completed= dManager.getCompleteAllSites();
                synced=(dManager.getCompleteSyncSitesStatus("SYNC"));
                unsynced=(dManager.getCompleteSyncSitesStatus("UNSYNC"));
                pending=(dManager.getPendingAllSites());
                imageUnsynced=(dManager.getImageUnsyncList());
                callLog=dManager.getCallLog();
                return "Success";
            }
        catch(SQLException sqe){
       // check.showErrorMessage("Error #106 "+sqe.getMessage());
            check.showErrorMessage(getString(R.string.error_104_unexpected_error));
        }
        catch(Exception e){
        //check.showErrorMessage("Error #104 "+e.getMessage());
            check.showErrorMessage(getString(R.string.error_104_unexpected_error));
        }
                        return "";
        }


        @Override
        protected void onPostExecute(String result) {
            try {
              /*  if (pd != null)
                    pd.dismiss();*/

            if(result.equalsIgnoreCase("Success"))
            {
                setupWithViewPager(vw);
                tab.setupWithViewPager(vw);
                tabDesign(tab, "TOTAL", siteLists.size() + "", 0);
                tabDesign(tab, "PENDING", String.format("%02d", pending.size()), 1);
                tabDesign(tab, "COMPLETED", String.format("%02d", completed.size()), 2);
                tabDesign(tab, "CallLog", String.format("%02d", callLog.size()), 3);
                tabDesign(tab, "SYNCED", String.format("%02d", synced.size()), 4);
                tabDesign(tab, "UNSYNCED", String.format("%02d", unsynced.size()), 5);
                tabDesign(tab, "IMAGE Unsynced", String.format("%02d", imageUnsynced.size()), 6);

                  /*Tab Layout Item Clickable false
                *
                LinearLayout tabStrip=((LinearLayout)tab.getChildAt(0));
                tabStrip.setEnabled(false);
                for(int j=0;j<tabStrip.getChildCount();j++)
                {
                    tabStrip.getChildAt(j).setClickable(false);
                }*/
            }
            else
            {
                //check.showErrorMessage("Error #107");
                check.showErrorMessage(getString(R.string.error_104_unexpected_error));
            }
            }catch(Exception e){}
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(SiteListActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    //finish();
                    viewAdapter.notifyDataSetChanged();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(SiteListActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
