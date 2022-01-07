package com.writercorporation.maudit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.writercorporation.adapeter.CategoryAdapter;
import com.writercorporation.database.DatabaseManager;
import com.writercorporation.model.CategoryList;
import com.writercorporation.model.Login;
import com.writercorporation.utils.AppConstant;

import java.sql.SQLException;
import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ActionBar actionBar;
    Toolbar toolbar;
    ListView lv;
    public static ArrayList<CategoryList> categoryLists = new ArrayList<>();
    DatabaseManager dManager;
    ProgressDialog progressDialog;
    CategoryAdapter adapter;
    AppConstant check;
    ProgressDialog pd;
    ArrayList<Login> isHousekeepingList;
    String isHousekeeping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        lv = (ListView) findViewById(R.id.lvCategory);
        pd=new ProgressDialog(this);
        pd.setMessage("Please Wait");
        pd.setCancelable(false);
        if(pd!=null)
            pd.show();
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        check = new AppConstant(this);
        dManager = DatabaseManager.getInstance();
        try{
            isHousekeepingList=dManager.getHousekeeping();
            isHousekeeping = isHousekeepingList.get(0).getIsHousekeeping();
        }
        catch (Exception e)
        {}

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                    categoryLists =check.getCatergoryList();
                    if(categoryLists ==null) {
                        categoryLists = dManager.getCategoryList();
                        check.setCategoryList(categoryLists);
                    }
                    adapter = new CategoryAdapter(getApplicationContext(), categoryLists);
                    lv.setAdapter(adapter);
                    if(pd!=null)
                        pd.dismiss();
                    } catch (SQLException sle) {
                        check.showErrorMessage(getString(R.string.error_101_json));
                        finish();
                    }
                }
            });
        if(adapter!=null)
        adapter.notifyDataSetChanged();
        lv.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {


        try {
            if(check.getCatergoryList()!=null) {

                if(check.getCatergoryList().size()>0) {
                    categoryLists.clear();
                    categoryLists.addAll(check.getCatergoryList());
                    adapter.notifyDataSetChanged();
                    lv.getAdapter().notifyAll();

                }
            }


        }catch(Exception sq){
          //  sq.printStackTrace();
        }
        super.onResume();
    }

    @Override
        public void onItemClick (AdapterView < ? > parent, View view,int position, long id){

        CategoryList category = (CategoryList) parent.getItemAtPosition(position);
        Intent obj=new Intent(getApplicationContext(),QuestionActivity.class);
        obj.putExtra("Id",category.getCategoryServerId());
        obj.putExtra("Name",category.getCategoryName());
        obj.putExtra("position",position);
        startActivity(obj);
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
            case android.R.id.home:
                Intent intent1 = new Intent(CategoryActivity.this, SiteListActivity.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.submit_callLog:
                if(validate()) {
//                    if (isHousekeeping.equals("true")){
//                        Intent intent = new Intent(getApplicationContext(), ImageCapture.class);
//                        startActivity(intent);
//                    }
//                    else {
                        Intent intent = new Intent(getApplicationContext(), ConfirmedCallLog.class);
                        startActivity(intent);
                   // }

                }else{
                    check.showErrorMessage(getString(R.string.submit_validate_error));
                }


        }
        return super.onOptionsItemSelected(item);
    }
    private boolean validate(){
        for(CategoryList category : categoryLists){
            if(!category.isCompleted()){
                return false;
            }
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(CategoryActivity.this, SiteListActivity.class);
        startActivity(intent);
        finish();
    }
 }
