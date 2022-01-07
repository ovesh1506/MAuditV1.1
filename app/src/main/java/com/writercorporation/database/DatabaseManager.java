package com.writercorporation.database;

import android.content.Context;

import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.writercorporation.maudit.R;
import com.writercorporation.model.Answers;
import com.writercorporation.model.CallLoggedList;
import com.writercorporation.model.CategoryList;
import com.writercorporation.model.CompleteCallLogInfo;
import com.writercorporation.model.CompletedSiteInfo;
import com.writercorporation.model.ImageModel;
import com.writercorporation.model.Login;
import com.writercorporation.model.MicroCategory;
import com.writercorporation.model.QuestionList;
import com.writercorporation.model.QuestionListCache;
import com.writercorporation.model.SiteList;
import com.writercorporation.model.SiteListCompare;
import com.writercorporation.model.SubCategory;
import com.writercorporation.model.VisitPurposeAnswer;
import com.writercorporation.model.VisitPurposeModel;

import net.sqlcipher.database.SQLiteDatabase;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;

/**
 * Created by amol.tate on 3/17/2016.
 *
 */
public class DatabaseManager {

    static private DatabaseManager instance;

    static public void init(Context ctx) {
        if (null==instance) {

            instance = new DatabaseManager(ctx);
        }
    }

    static public DatabaseManager getInstance() {
        return instance;
    }
    private String password="writer@123";
    private DatabaseHelperCipher helper;
    private DatabaseManager(Context ctx) {
        SQLiteDatabase.loadLibs(ctx);
        helper = new DatabaseHelperCipher(ctx,password);
    }

    public DatabaseHelperCipher getHelper() {
        return helper;
    }

    public void insertCategoryList(final List<CategoryList> categoryList) throws Exception{
        getHelper().getCategoryListDao().callBatchTasks(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                for (CategoryList category : categoryList) {
                    getHelper().getCategoryListDao().createOrUpdate(category);

                }
                return null;
            }
        });
    }
    public void insertCallLogedList(final List<CallLoggedList> callLoggedLists)throws Exception{
       getHelper().getCallLoggedListDao().callBatchTasks(new Callable<Void>() {
           @Override
           public Void call() throws Exception {
               for(CallLoggedList callLoggedList1:callLoggedLists) {
                   getHelper().getCallLoggedListDao().createOrUpdate(callLoggedList1);
               }
               return null;
           }
       });
    }
    public void insertSubCategory(final List<SubCategory> subCategoryList) throws Exception{
        getHelper().getSubCategoryDao().callBatchTasks(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for (SubCategory subCategory : subCategoryList) {
                    getHelper().getSubCategoryDao().createOrUpdate(subCategory);

                }
                return null;
            }
        });
    }
    public void insertMicroCategory(final List<MicroCategory> microCategoryList) throws Exception{
        getHelper().getSubCategoryDao().callBatchTasks(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for (MicroCategory microCategory : microCategoryList) {
                    getHelper().getMicroCategoryDao().createOrUpdate(microCategory);

                }
                return null;
            }
        });
    }
    public void insertQuestionList(final List<QuestionList> questionList) throws Exception{
        getHelper().getQuestionListDao().callBatchTasks(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for (QuestionList question : questionList) {
                    getHelper().getQuestionListDao().createOrUpdate(question);
                }
                return null;
            }
        });
    }
    public void insertAnswers(final List<Answers> answers) throws Exception{
        getHelper().getQuestionListDao().callBatchTasks(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for (Answers answer : answers) {
                    getHelper().getAnswersDao().createOrUpdate(answer);

                }
                return null;
            }
        });
    }
    public void insertSites(final List<SiteList> siteLists) throws Exception{
        getHelper().getQuestionListDao().callBatchTasks(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for (SiteList site : siteLists) {
                    getHelper().getSiteListDao().createOrUpdate(site);
                }
                return null;
            }
        });
    }
    public void insertCompletedSites(final CompletedSiteInfo siteLists) throws Exception{
        getHelper().getCompletedSiteInfoDaoDao().createOrUpdate(siteLists);
    }

    public void insertImageModel(final ArrayList<ImageModel> image)throws Exception{
        getHelper().getImageDao().callBatchTasks(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for (ImageModel img : image)
                    getHelper().getImageDao().createOrUpdate(img);
                return null;
            }
        });
    }
    public void insertVisitPurpose(final List<VisitPurposeModel> visitPurpose)throws Exception{
        getHelper().getImageDao().callBatchTasks(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for (VisitPurposeModel visit : visitPurpose)
                    getHelper().getVisitPurposeDao().createOrUpdate(visit);
                return null;
            }
        });
    }
    public void insertCompleteCallLog(final CompleteCallLogInfo completeCallLogInfo) throws Exception{
        getHelper().getCompleteCallLoggedListDao().callBatchTasks(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                getHelper().getCompleteCallLoggedListDao().createOrUpdate(completeCallLogInfo);
                return null;
            }
        });
    }


    public void insertVistPurposeAnswer(final VisitPurposeAnswer vistAnswer) throws Exception
    {
        getHelper().getVisiAnswerDao().callBatchTasks(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                getHelper().getVisiAnswerDao().createOrUpdate(vistAnswer);
                return null;
            }
        });
    }

    public void insertLogin(Login login) throws SQLException{
        DeleteBuilder<Login, Integer> deleteBuilder = getHelper().getLoginDao().deleteBuilder();
        deleteBuilder.delete();
        getHelper().getLoginDao().create(login);
    }

    public List<SiteList> getAllSites() throws SQLException{
       return getHelper().getSiteListDao().queryForAll();
    }
    public ArrayList<SiteList> getCompleteAllSites(int ID) throws SQLException
    {
        ArrayList<SiteList> completeSiteList = null;
        QueryBuilder<SiteList,Integer> siteListIntegerQueryBuilder=getHelper().getSiteListDao().queryBuilder();
        siteListIntegerQueryBuilder.where().eq("siteID", ID);
        List<SiteList> siteLists=siteListIntegerQueryBuilder.query();
        completeSiteList=new ArrayList<>(siteLists.size());
        completeSiteList.addAll(siteLists);
        return completeSiteList;
    }
    public ArrayList<SiteList> getCompleteAllSites() throws SQLException
    {
        ArrayList<SiteList> completeSiteList = null;
        QueryBuilder<CompletedSiteInfo, Integer> completedSiteInfoIntegerQueryBuilder=getHelper().getCompletedSiteInfoDaoDao().queryBuilder();
        QueryBuilder<SiteList,Integer> siteListIntegerQueryBuilder=getHelper().getSiteListDao().queryBuilder();
        siteListIntegerQueryBuilder.join(completedSiteInfoIntegerQueryBuilder);
        List<SiteList> siteLists=siteListIntegerQueryBuilder.query();
        completeSiteList=new ArrayList<>(siteLists.size());
        completeSiteList.addAll(siteLists);
        return completeSiteList;
    }
    public ArrayList<SiteList> getCallLog() throws SQLException
    {
        ArrayList<SiteList> CallLogList = null;
        QueryBuilder<SiteList,Integer> siteListIntegerQueryBuilder=getHelper().getSiteListDao().queryBuilder();
        siteListIntegerQueryBuilder.where().eq("siteIsLogged", "Y");
        List<SiteList> siteLists=siteListIntegerQueryBuilder.query();
        CallLogList=new ArrayList<>(siteLists.size());
        CallLogList.addAll(siteLists);
        return CallLogList;
    }
    public ArrayList<SiteList> getPendingAllSites() throws SQLException
    {
        ArrayList<SiteList> completeSiteList = null;
        QueryBuilder<CompletedSiteInfo, Integer> completedSiteInfoIntegerQueryBuilder=getHelper().getCompletedSiteInfoDaoDao().queryBuilder();
        completedSiteInfoIntegerQueryBuilder.selectColumns("siteID").where().not().eq("siteID",0);
        QueryBuilder<SiteList,Integer> siteListIntegerQueryBuilder=getHelper().getSiteListDao().queryBuilder();
        siteListIntegerQueryBuilder.where().notIn("siteID", completedSiteInfoIntegerQueryBuilder);
        List<SiteList> siteLists=siteListIntegerQueryBuilder.query();
        completeSiteList=new ArrayList<>(siteLists.size());
        completeSiteList.addAll(siteLists);
        return completeSiteList;
    }
    public ArrayList<SiteList> getImageUnsyncList() throws SQLException
    {
        ArrayList<SiteList> completeSiteList = null;
        QueryBuilder<ImageModel, Integer> imageModelIntegerQueryBuilder=getHelper().getImageDao().queryBuilder();
        imageModelIntegerQueryBuilder.selectColumns("siteId").where().not().eq("siteId", 0).and().eq("syncStatus","UNSYNC");
        QueryBuilder<SiteList,Integer> siteListIntegerQueryBuilder=getHelper().getSiteListDao().queryBuilder();
        siteListIntegerQueryBuilder.where().in("siteID", imageModelIntegerQueryBuilder);
        List<SiteList> siteLists=siteListIntegerQueryBuilder.query();
        completeSiteList=new ArrayList<>(siteLists.size());
        completeSiteList.addAll(siteLists);
        return completeSiteList;
    }


    public ArrayList<QuestionList> getQuestionText(int siteID) throws SQLException
    {
        ArrayList<QuestionList> questionListArrayList;
        QueryBuilder<CallLoggedList,Integer> callLoggedListIntegerQueryBuilder=getHelper().getCallLoggedListDao().queryBuilder();
       // callLoggedListIntegerQueryBuilder.selectColumns("questionID").where().eq("callLogStatus", "O").eq("callLogStatus","").or(2).eq("siteID", siteID);

        Where where=callLoggedListIntegerQueryBuilder.where();
        where.and(where.eq("siteID", siteID), where.or(where.eq("callLogStatus", "O"), where.eq("callLogStatus", "")));

        PreparedQuery<CallLoggedList> preparedQuery=callLoggedListIntegerQueryBuilder.prepare();
        String query=preparedQuery.toString();
       // callLoggedListIntegerQueryBuilder.selectColumns("questionID");
        QueryBuilder<QuestionList,Integer> questionListIntegerQueryBuilder=getHelper().getQuestionListDao().queryBuilder();
        List<QuestionList> questionLists=  questionListIntegerQueryBuilder.where().in("id", callLoggedListIntegerQueryBuilder.selectColumns("questionID")).query();
        questionListArrayList=new ArrayList<>(questionLists.size());
        questionListArrayList.addAll(questionLists);
        return questionListArrayList;
    }
    public Integer[] getCallLogQuestionID(int siteID) throws SQLException
    {
        ArrayList<CallLoggedList> questionListArrayList;
        QueryBuilder<CallLoggedList,Integer> callLoggedListIntegerQueryBuilder=getHelper().getCallLoggedListDao().queryBuilder();
        // callLoggedListIntegerQueryBuilder.selectColumns("questionID").where().eq("callLogStatus", "O").eq("callLogStatus","").or(2).eq("siteID", siteID);

        Where where=callLoggedListIntegerQueryBuilder.where();
        where.and(where.eq("siteID", siteID), where.or(where.eq("callLogStatus", "O"), where.eq("callLogStatus", "")));

        PreparedQuery<CallLoggedList> preparedQuery=callLoggedListIntegerQueryBuilder.prepare();
        String query=preparedQuery.toString();
        RawRowMapper<Integer> mapper=new RawRowMapper<Integer>() {
            @Override
            public Integer mapRow(String[] columnNames, String[] resultColumns) throws SQLException {

                return Integer.parseInt(resultColumns[0]);
            }
        };
        List<Integer> results  =getHelper().getCallLoggedListDao().queryRaw(callLoggedListIntegerQueryBuilder.selectColumns("questionID").prepareStatementString(), mapper).getResults();
        Integer[] resultArray = (Integer[]) results.toArray(new Integer[results.size()]);
        return resultArray;
    }

    public ArrayList<CallLoggedList> getCallLogList(int siteID) throws SQLException
    {
        ArrayList<CallLoggedList> questionListArrayList;
        QueryBuilder<CallLoggedList, Integer> callLoggedListIntegerQueryBuilder=getHelper().getCallLoggedListDao().queryBuilder();
      //  callLoggedListIntegerQueryBuilder.where().eq("siteID", siteID).and().eq("callLogStatus", "");
        Where where=callLoggedListIntegerQueryBuilder.where();
        where.and( where.eq("siteID", siteID),where.or(where.eq("callLogStatus", "O"), where.eq("callLogStatus", "")));
        PreparedQuery<CallLoggedList> preparedQuery=callLoggedListIntegerQueryBuilder.prepare();
        String query=preparedQuery.toString();
        List<CallLoggedList> obj=callLoggedListIntegerQueryBuilder.distinct().query();
        questionListArrayList=new ArrayList<>(obj.size());
        questionListArrayList.addAll(obj);
        return questionListArrayList;
    }
    public void DeleteCallLogList(int siteID) throws SQLException
    {
        DeleteBuilder<CallLoggedList, Integer> deleteBuilder=getHelper().getCallLoggedListDao().deleteBuilder();
        deleteBuilder.where().eq("siteID",siteID);
        deleteBuilder.delete();

    }
    public void updateCallLogstatus(String callLogID,String callLogStatus,String bitmap) throws SQLException
    {
        UpdateBuilder<CallLoggedList, Integer> updateBuilder = getHelper().getCallLoggedListDao().updateBuilder();
        updateBuilder.updateColumnValue("callLogImage", bitmap);
        updateBuilder.updateColumnValue("callLogStatus", callLogStatus);
        updateBuilder.where().eq("serverCallLogID", callLogID);

        updateBuilder.update();

    }
    public ArrayList<SiteList> getCompleteSyncSitesStatus(String synced) throws SQLException
    {
        ArrayList<SiteList> completeSiteList = null;

        QueryBuilder<CompletedSiteInfo, Integer> completedSiteInfoIntegerQueryBuilder=getHelper().getCompletedSiteInfoDaoDao().queryBuilder();
        QueryBuilder<SiteList,Integer> siteListIntegerQueryBuilder=getHelper().getSiteListDao().queryBuilder();
        completedSiteInfoIntegerQueryBuilder.where().eq("syncStatus", synced);
        siteListIntegerQueryBuilder.join(completedSiteInfoIntegerQueryBuilder);//.join(visitQueryBuilder);
        List<SiteList> siteLists=siteListIntegerQueryBuilder.query();
        /*** For the Visit Purpose Sync Status ****/
        QueryBuilder<SiteList,Integer> siteListVisitIntegerQueryBuilder=getHelper().getSiteListDao().queryBuilder();
        QueryBuilder<VisitPurposeAnswer,Integer> visitQueryBuilder=getHelper().getVisiAnswerDao().queryBuilder();
        visitQueryBuilder.where().eq("syncStatus",synced);
        siteListVisitIntegerQueryBuilder.join(visitQueryBuilder);
        List<SiteList> VistSiteList=siteListVisitIntegerQueryBuilder.query();
        //VistSiteList.for
        SiteList objSite=new SiteList();
        ArrayList<SiteList> TagValue= objSite.setIsVisit((ArrayList<SiteList>) VistSiteList);
        completeSiteList=new ArrayList<>();
        completeSiteList.addAll(TagValue);
        completeSiteList.addAll(siteLists);
        Set<SiteList> obj=new TreeSet<SiteList>(new SiteListCompare());
        obj.addAll(completeSiteList);

        //completeSiteList=new ArrayList<>();
        completeSiteList.clear();
//        completeSiteList.addAll(obj);
ArrayList<SiteList> dupli=new ArrayList<>(obj);


        return dupli;
    }
    public ArrayList<Answers> getAnswerID(int qID)throws SQLException
    {
        ArrayList<Answers> anserID=null;
        QueryBuilder<QuestionList,Integer> questionListIntegerQueryBuilder=getHelper().getQuestionListDao().queryBuilder();
        questionListIntegerQueryBuilder.where().eq("id", qID);
        List<QuestionList> temp2=questionListIntegerQueryBuilder.query();
        QueryBuilder<Answers,Integer> answersIntegerQueryBuilder=getHelper().getAnswersDao().queryBuilder();
        answersIntegerQueryBuilder.leftJoin(questionListIntegerQueryBuilder);
        List<Answers> temp1=answersIntegerQueryBuilder.query();
        List<Answers> temp=  answersIntegerQueryBuilder.where().eq("callLog","true").query();
        anserID=new ArrayList<>(temp.size());
        anserID.addAll(temp);
        return anserID;
    }

    public boolean authenticateLocal(String username, String password) throws SQLException{
        QueryBuilder<Login,Integer> queryBuilder = getHelper().getLoginDao().queryBuilder();

        Where where = queryBuilder.where();
        where.eq("username", username);
        where.and();
        where.eq("password", password);
        int size = queryBuilder.query().size();
        if(size==1)
             return true;
        else
            return false;
    }

    public int authenticateCount(String username, String password) throws SQLException{
        QueryBuilder<Login,Integer> queryBuilder = getHelper().getLoginDao().queryBuilder();

     /*   Where where = queryBuilder.where();
        where.eq("username", username);
        where.and();
        where.eq("password", password);*/
        int size = queryBuilder.query().size();
        if(size==1)
            return size;
        else
            return 0;
    }
    public ArrayList<CategoryList> getCategoryList() throws SQLException{
        List<CategoryList> allCategory = getHelper().getCategoryListDao().queryForAll();
        ArrayList<CategoryList> categoryArrayList = new ArrayList<>(allCategory.size());
        categoryArrayList.addAll(allCategory);
        return categoryArrayList;
    }

    public ArrayList<Login> getHousekeeping() throws SQLException{
        List<Login> logindetails = null;
        QueryBuilder<Login, Integer> loginIntegerQueryBuilder = getHelper().getLoginDao().queryBuilder();
        //loginIntegerQueryBuilder.where().eq("isHousekeeping","isHousekeeping" );
        logindetails=loginIntegerQueryBuilder.query();
        ArrayList<Login> ishouseKeepingValue=new ArrayList<Login>(logindetails.size());
        ishouseKeepingValue.addAll(logindetails);
        return ishouseKeepingValue;
    }

    public ArrayList<QuestionList> getQuestionList(int categoryId) throws SQLException{
        ArrayList<QuestionList> questionListList = null;
            QueryBuilder<SubCategory, Integer> categoryListIntegerQueryBuilder = getHelper().getSubCategoryDao().queryBuilder();
            QueryBuilder<MicroCategory, Integer> microCategoryIntegerQueryBuilder = getHelper().getMicroCategoryDao().queryBuilder();
            QueryBuilder<QuestionList, Integer> questionListIntegerQueryBuilder = getHelper().getQuestionListDao().queryBuilder();
            categoryListIntegerQueryBuilder.where().eq("categoryList_id", categoryId);
            microCategoryIntegerQueryBuilder.join(categoryListIntegerQueryBuilder);
            questionListIntegerQueryBuilder.join(microCategoryIntegerQueryBuilder);
            List<QuestionList> questionList = questionListIntegerQueryBuilder.query();
            questionListList = new ArrayList<>(questionList.size());
            questionListList.addAll(questionList);
            return questionListList;
    }
 /*   public void insertQuestionCacheList(final List<QuestionListCache> questionList) throws Exception{
        getHelper().getQuestionListCacheDao().callBatchTasks(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for (QuestionListCache question : questionList) {
                    getHelper().getQuestionListCacheDao().createOrUpdate(question);
                }
                return null;
            }
        });
    }*/
    public ArrayList<QuestionListCache> getQuestionListCache(String category) throws SQLException{
        QueryBuilder<QuestionListCache, Integer> questionListCacheIntegerQueryBuilder = getHelper().getQuestionListCacheDao().queryBuilder();
        questionListCacheIntegerQueryBuilder.where().eq("categoryType", category);
        List<QuestionListCache> allCache = questionListCacheIntegerQueryBuilder.query();
        ArrayList<QuestionListCache> categoryArrayList = new ArrayList<>(allCache.size());
        categoryArrayList.addAll(allCache);
        return categoryArrayList;
    }

    public List<CompletedSiteInfo> getCompletedUnsyncSites()throws SQLException{
        QueryBuilder<CompletedSiteInfo,Integer> queryBuilder = getHelper().getCompletedSiteInfoDaoDao().queryBuilder();
        Where where = queryBuilder.where();
        where.eq("syncStatus", "UNSYNC");
        return queryBuilder.query();
    }

    public List<VisitPurposeAnswer> getCompletedVisitUnsyncSites()throws SQLException{
        QueryBuilder<VisitPurposeAnswer,Integer> queryBuilder = getHelper().getVisiAnswerDao().queryBuilder();
        Where where = queryBuilder.where();
        where.eq("syncStatus", "UNSYNC");
        return queryBuilder.query();
    }

    /*public void updateVisitSiteList(int id)throws SQLException,Exception
    {
        QueryBuilder<VisitPurposeAnswer,Integer> completedSiteInfoQueryBuilder=getHelper().getVisiAnswerDao().queryBuilder();
        completedSiteInfoQueryBuilder.where().eq("siteID", id).and().like("jsonString","%\"CallLog\":\"true\"%");
        String s=completedSiteInfoQueryBuilder.prepareStatementString();
        List<VisitPurposeAnswer> completedSiteInfos=completedSiteInfoQueryBuilder.query();
        if(completedSiteInfos!=null && completedSiteInfos.size()!=0)
        {
            SimpleDateFormat ft = new SimpleDateFormat("MM/dd/yyyy HH:MM:SS am", Locale.US);
            Date date=new Date();
            UpdateBuilder<SiteList,Integer> updateBuilder=getHelper().getSiteListDao().updateBuilder();
            updateBuilder.updateColumnValue("siteIsLogged","Y");
            updateBuilder.updateColumnValue("lstAuditDate",ft.format(date));
            updateBuilder.where().eq("siteID", id);
            updateBuilder.update();
        }
    }*/
    public List<ImageModel> getCompletedUnsyncImages()throws SQLException{
        QueryBuilder<ImageModel,Integer> queryBuilder = getHelper().getImageDao().queryBuilder();
        Where where = queryBuilder.where();
        where.eq("syncStatus", "UNSYNC");
        return queryBuilder.query();
    }
    public List<CompleteCallLogInfo> getUnsyncCallLogList()throws  SQLException{
        QueryBuilder<CompleteCallLogInfo,Integer> queryBuilder=getHelper().getCompleteCallLoggedListDao().queryBuilder();
        Where where = queryBuilder.where();
        where.eq("syncStatus", "UNSYNC");
        return queryBuilder.query();
    }
    public void changeCallLogSyncStatus(String syncStatus,int id) throws SQLException
    {
        UpdateBuilder<CompleteCallLogInfo, Integer> updateBuilder = getHelper().getCompleteCallLoggedListDao().updateBuilder();
        updateBuilder.where().eq("siteID", id);
        updateBuilder.updateColumnValue("syncStatus" , syncStatus );
        updateBuilder.update();
    }
    public void changeSyncStatus(String syncStatus, int id)throws SQLException{
        UpdateBuilder<CompletedSiteInfo, Integer> updateBuilder = getHelper().getCompletedSiteInfoDaoDao().updateBuilder();
        updateBuilder.where().eq("siteID", id);
        updateBuilder.updateColumnValue("syncStatus" , syncStatus );
        updateBuilder.update();
    }

    public void changeVisitSyncStatus(String syncStatus, int id)throws SQLException{
        UpdateBuilder<VisitPurposeAnswer, Integer> updateBuilder = getHelper().getVisiAnswerDao().updateBuilder();
        updateBuilder.where().eq("siteId", id);
        updateBuilder.updateColumnValue("syncStatus" , syncStatus );
        updateBuilder.update();
    }


    public void setColorDatabase(int siteID) throws SQLException
    {


            UpdateBuilder<SiteList,Integer> updateBuilder=getHelper().getSiteListDao().updateBuilder();
            updateBuilder.updateColumnValue("siteColor", R.color.calllogbutton);

            updateBuilder.where().eq("siteID", siteID);
            updateBuilder.update();

    }
    public void updateSiteListCallLog(int id)throws SQLException,Exception
    {
        QueryBuilder<CompletedSiteInfo,Integer> completedSiteInfoQueryBuilder=getHelper().getCompletedSiteInfoDaoDao().queryBuilder();
        completedSiteInfoQueryBuilder.where().eq("siteID", id).and().like("jsonString","%\"CallLog\":\"true\"%");
        String s=completedSiteInfoQueryBuilder.prepareStatementString();
        List<CompletedSiteInfo> completedSiteInfos=completedSiteInfoQueryBuilder.query();
        if(completedSiteInfos!=null && completedSiteInfos.size()!=0)
        {
            SimpleDateFormat ft = new SimpleDateFormat("MM/dd/yyyy HH:MM:SS am", Locale.US);
            Date date=new Date();
            UpdateBuilder<SiteList,Integer> updateBuilder=getHelper().getSiteListDao().updateBuilder();
            updateBuilder.updateColumnValue("siteIsLogged","Y");
            updateBuilder.updateColumnValue("lstAuditDate",ft.format(date));
            updateBuilder.where().eq("siteID", id);
            updateBuilder.update();
        }
    }
    public void changeSyncStatusImages(String syncStatus, int id)throws SQLException{
        UpdateBuilder<ImageModel, Integer> updateBuilder = getHelper().getImageDao().updateBuilder();
        updateBuilder.updateColumnValue("syncStatus", syncStatus );
        updateBuilder.where().eq("siteId", id);
        updateBuilder.update();
    }
    /***Get Visit Purpose Code ****/
    public ArrayList<VisitPurposeModel> getVisitPurposeList() throws SQLException,Exception
    {
        ArrayList<VisitPurposeModel> visitList = null;
       // QueryBuilder<VisitPurposeModel, Integer> visitListQueryBuilder=getHelper().getVisitPurposeDao().queryBuilder();
        List<VisitPurposeModel> visitLists=getHelper().getVisitPurposeDao().queryBuilder().query();
        visitList=new ArrayList<>(visitLists.size());
        visitList.add(new VisitPurposeModel("","","","Select Visit Purpose",""));
        visitList.addAll(visitLists);
        return visitList;
    }
}
