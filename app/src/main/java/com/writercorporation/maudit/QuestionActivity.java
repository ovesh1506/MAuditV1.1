package com.writercorporation.maudit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.writercorporation.adapeter.QuestionAdapter;
import com.writercorporation.database.DatabaseManager;
import com.writercorporation.model.CategoryList;
import com.writercorporation.model.QuestionList;
import com.writercorporation.utils.AppConstant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class QuestionActivity extends AppCompatActivity implements OnCheckedChange,View.OnClickListener {

    ActionBar actionBar;
    Toolbar toolbar;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager recycleLayout;
    static ArrayList<QuestionList> callList = new ArrayList<>();
    static QuestionAdapter que;
    int categoryId;
    String categoryName = null;
    Intent data = null;
    AppConstant check;
    private DatabaseManager dManager;
    static Context context;
    static OnCheckedChange checkedChangeObj;
    private FrameLayout layout_footer;

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private Button btnSave;
    private int categoryPostion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        actionBar = getSupportActionBar();
        check = new AppConstant();
        context = this;
        checkedChangeObj = this;

        data = getIntent();
        if (data != null) {
            categoryId = data.getIntExtra("Id", 0);
            categoryName = data.getStringExtra("Name");
            categoryPostion = data.getIntExtra("position",0);
        } else {
            check.showErrorMessage(getString(R.string.data_not_found));
            finish();
            return;
        }
        dManager = DatabaseManager.getInstance();

        try {
            ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                    ActionBar.LayoutParams.WRAP_CONTENT,
                    ActionBar.LayoutParams.MATCH_PARENT,
                    Gravity.CENTER);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setTitle(null);

            LayoutInflater inflater = LayoutInflater.from(this);
            View v = inflater.inflate(R.layout.custom_actionbar, null);
            TextView txt = (TextView) v.findViewById(R.id.heading);
            final ImageView left = (ImageView) v.findViewById(R.id.leftimage);
            final ImageView right = (ImageView) v.findViewById(R.id.rightimage);
            txt.setText(categoryName);
            left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
                }
            });
            right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
                }
            });
            actionBar.setCustomView(v, params);
            mViewPager = (ViewPager) findViewById(R.id.container);
            btnSave = (Button) findViewById(R.id.btnSave);
            btnSave.setOnClickListener(this);
            layout_footer = (FrameLayout) findViewById(R.id.layout_footer);
            layout_footer.setVisibility(View.GONE);

            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    if(callList!=null) {
                        if (callList.size() <= 3 && position == 0) {
                            layout_footer.setVisibility(View.VISIBLE);
                        }
                        if(position==0)
                        {
                            left.setVisibility(View.GONE);
                        }
                        else
                            left.setVisibility(View.VISIBLE);

                        int pageCount = (int) Math.ceil(Float.valueOf(callList.size()) / 3.0);
                        if (position + 1 == pageCount) {
                              right.setVisibility(View.INVISIBLE);
                        } else {
                             right.setVisibility(View.VISIBLE);
                        }
                    }
                }


                @Override
                public void onPageSelected(int position) {
                    int pageCount = (int) Math.ceil(Float.valueOf(callList.size()) / 3.0);
                    if (position + 1 == pageCount) {
                        layout_footer.setVisibility(View.VISIBLE);
                        right.setVisibility(View.INVISIBLE);
                    } else {
                        layout_footer.setVisibility(View.GONE);
                        right.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            new LoadActivityAsync().execute();

        } /*catch (Exception sqe) {
            check.showErrorMessage("Error #106 " + sqe.getMessage());
            finish();
        }*/ catch (Exception e) {
            //check.showErrorMessage("Error #104 " + e.getMessage());
            check.showErrorMessage(getString(R.string.error_104_unexpected_error));
            finish();
        }



    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (mViewPager.getCurrentItem() == 0 && callList != null && callList.size() > 3) {
                layout_footer.setVisibility(View.GONE);
            }
        }
        catch (NullPointerException e)
        {
           // e.printStackTrace();
        }

    }

    @Override
    public void onCheckedChange(final int questionId,final boolean flag) {

             Runnable aRunnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < callList.size(); i++) {
                    QuestionList qList = callList.get(i);
                    if (qList.getParentCheckpointId() == questionId) {
                        qList = qList.setIsShow(flag);
                        callList.set(i, qList);

                    }
                }
                QuestionActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mViewPager.getAdapter().notifyDataSetChanged();
                    }
                });

            }
        };
        Thread thread = new Thread(aRunnable);
        thread.start();
    }

    @Override
    public void onListItemsChanged(QuestionList callList) {


        for(int i=0;i<this.callList.size();i++){
            if(this.callList.get(i).getId()==callList.getId()){
                this.callList.set(i,callList);
            }
        }
        // mViewPager.getAdapter().notifyDataSetChanged();
    }
    @Override
    public void onClick(View view) {
        boolean flag = true;
        View vFocused = getCurrentFocus();
        if(vFocused instanceof EditText)
        {
            vFocused.requestFocus();
            vFocused.clearFocus();

        }
        try {
            JSONObject root = new JSONObject();
            JSONArray rootArray = new JSONArray();
            //ArrayList<QuestionList> questionListArr = new ArrayList<>();
            for (int i = 0; i < callList.size(); i++) {
                QuestionList questionList = callList.get(i);
                if(questionList.isShow() && questionList.getParentCheckpointId()!=0)
                    continue;
                JSONObject questionObject = new JSONObject();


                if (questionList.getCheckedAnswer() != null)
                {
                    questionList.setList(questionList.getAnswerList());
                    callList.set(i, questionList);
                    if(("".equals(questionList.getExtraFieldValue()) || "Select".equals(questionList.getExtraFieldValue()) || "Select Date".equals(questionList.getExtraFieldValue())) && questionList.isChildShow()){
                        flag = false;
                    }
                    questionObject.put("QuestionID", questionList.getId());
                    questionObject.put("AnswerID", questionList.getCheckedAnswer().getId());
                    questionObject.put("CallLog", questionList.getCheckedAnswer().getCallLog());
                    questionObject.put("ExtraFieldValue", questionList.getExtraFieldValue());
                    rootArray.put(questionObject);
                }
                else {
                    flag = false;
                }
            }
            root.put("questionInfo",rootArray);
            if(callList.size()>0)
                check.setCategoryData(callList, categoryName);

            if(flag) {
                ArrayList<CategoryList> categoryArrList = check.getCatergoryList();
                CategoryList categoryList = categoryArrList.get(categoryPostion);
                categoryList.setImageResource(R.mipmap.ic_done);
                categoryList.setIsCompleted(flag);
                categoryArrList.set(categoryPostion,categoryList);
                check.setCategoryList(categoryArrList);
            }
            else {
                ArrayList<CategoryList> categoryArrList = check.getCatergoryList();
                CategoryList categoryList = categoryArrList.get(categoryPostion);
                categoryList.setImageResource(R.mipmap.arrow);
                categoryList.setIsCompleted(flag);
                categoryArrList.set(categoryPostion,categoryList);
                check.setCategoryList(categoryArrList);
            }
            finish();

        }catch(Exception jse){
           // jse.printStackTrace();
        }
    }

    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String START_SUB = "start_sub";
        private static final String END_SUB = "end_sub";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public PlaceholderFragment newInstance(int start,int end) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(START_SUB, start);
            args.putInt(END_SUB, end);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            /*TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));*/

            RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.list_callLog);
            RecyclerView.LayoutManager recycleLayout = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(recycleLayout);
            ArrayList<QuestionList> subList=new ArrayList(3);
            subList.addAll(callList.subList(getArguments().getInt(START_SUB),getArguments().getInt(END_SUB)+1));
            que = new QuestionAdapter(context, subList,checkedChangeObj);
            recyclerView.setAdapter(que);
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        int start=0;
        int end=2;
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            PlaceholderFragment newInstance;
            if(callList.size()<=3){
                newInstance = new PlaceholderFragment().newInstance(start,callList.size()-1);
            }else{
                newInstance = new PlaceholderFragment().newInstance(start,end);
            }
            start = end+1;
            end = end+3;
            end = callList.size() <= end ?  callList.size()-1 : end;
            return newInstance;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return (int)Math.ceil(Float.valueOf(callList.size())/3.0);
            //return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

    }

    class LoadActivityAsync extends AsyncTask<String,String,String>
    {
        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd=new ProgressDialog(QuestionActivity.this);
            pd.setMessage("Loading...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if("SQLException".equals(s)){
                //check.showErrorMessage("Error #106 " + s);
                check.showErrorMessage(getString(R.string.error_104_unexpected_error));
                finish();
                return;
            }if (callList == null) {
                check.showErrorMessage(getString(R.string.no_record_found));
                finish();
                return;
            } else if (callList.size() == 0) {
                check.showErrorMessage(getString(R.string.no_record_found));
                ArrayList<CategoryList> categoryArrList = check.getCatergoryList();
                CategoryList categoryList = categoryArrList.get(categoryPostion);
                categoryList.setImageResource(R.mipmap.ic_done);
                categoryList.setIsCompleted(true);
                categoryArrList.set(categoryPostion, categoryList);
                if(s.equalsIgnoreCase("No Data")) {
                    check.setCategoryList(categoryArrList);
                    check.showErrorMessage(getString(R.string.no_record_found));
                }
                finish();
                return;
            }
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
            mViewPager.setAdapter(mSectionsPagerAdapter);
            if(callList.size()>3)
                layout_footer.setVisibility(View.GONE);
            else
                layout_footer.setVisibility(View.VISIBLE);

            if(pd!=null)
                pd.dismiss();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                ArrayList<QuestionList> tempQue=new ArrayList<>();
                tempQue.clear();
                SimpleDateFormat ft = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                Log.e("Before","Audit Date code");
                Log.e("Audit Date",check.getSiteAuditDate());
                Date date=ft.parse(check.getSiteAuditDate());
                Log.e("Date",date.toString());

                Date currentDate=ft.parse("1/1/1900 12:00:00 AM");
                Log.e("Date",currentDate.toString());
            //  int value=date.compareTo(currentDate);


                callList = check.getCategoryData(categoryName);
                if (callList.size()==0) {
                    callList = dManager.getQuestionList(categoryId);

                }

                if (callList != null &&  date.compareTo(currentDate)==1)
                {
                    for(QuestionList q:callList)
                    {

                        if(!q.getIsOneTime().equalsIgnoreCase("True") )
                            tempQue.add(q);
                    }
                    callList.clear();
                    callList.addAll(tempQue);
                    if(callList.size()==0)
                        return "No Data";
                }
            }catch(SQLException sqe){
                //sqe.printStackTrace();
                return "SQLException";
            }
            catch (Exception e)
            {
                Log.e("Date Exception",e.getMessage());
                return "Date Conversion Exception";
            }
            return "";
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onClick(null);
    }


}
