package com.writercorporation.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.query.In;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
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
import com.writercorporation.model.QuestionNotToShow;
import com.writercorporation.model.QuestionRequiredField;
import com.writercorporation.model.SiteList;
import com.writercorporation.model.SubAnswer;
import com.writercorporation.model.SubCategory;
import com.writercorporation.model.VisitPurposeAnswer;
import com.writercorporation.model.VisitPurposeModel;

/**
 * Created by amol.tate on 3/17/2016.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    // name of the database file for your application
    private static final String DATABASE_NAME = "MAudit.sqlite";

// any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 5;
    // the DAO object we use to access the SimpleData table
    private Dao<Answers, Integer> answerDao = null;
    private Dao<CallLoggedList, Integer> callLoggedListDao = null;
    private Dao<CategoryList, Integer> categoryListDao = null;
    private Dao<Login, Integer> loginDao = null;
    private Dao<QuestionListCache, Integer> questionListCacheDao = null;
    private Dao<MicroCategory, Integer> microCategoryDao = null;
    private Dao<QuestionList, Integer> questionListDao = null;
    private Dao<QuestionNotToShow, Integer> questionNotToShowDao = null;
    private Dao<QuestionRequiredField, Integer> questionRequiredFieldDao = null;
    private Dao<SiteList, Integer> siteListDao = null;
    private Dao<SubAnswer, Integer> subAnswerDao = null;
    private Dao<SubCategory, Integer> subCategoryDao = null;
    private Dao<CompletedSiteInfo, Integer> completedSiteInfoDao = null;
    private Dao<CompleteCallLogInfo, Integer> CompleteCallLogInfoDao = null;
    private Dao<ImageModel, Integer> imageDao = null;
    private Dao<VisitPurposeModel, Integer> visitDao = null;
    private Dao<VisitPurposeAnswer,Integer> visiAnswerDao=null;
    ConnectionSource connectionSource; int oldVersion;SQLiteDatabase database;
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTableIfNotExists(connectionSource, Answers.class);
            TableUtils.createTableIfNotExists(connectionSource, CallLoggedList.class);
            TableUtils.createTableIfNotExists(connectionSource, CategoryList.class);
            TableUtils.createTableIfNotExists(connectionSource, Login.class);
            TableUtils.createTableIfNotExists(connectionSource, MicroCategory.class);
            TableUtils.createTableIfNotExists(connectionSource, QuestionList.class);
            TableUtils.createTableIfNotExists(connectionSource, QuestionNotToShow.class);
            TableUtils.createTableIfNotExists(connectionSource, QuestionRequiredField.class);
            TableUtils.createTableIfNotExists(connectionSource, SiteList.class);
            TableUtils.createTableIfNotExists(connectionSource, SubAnswer.class);
            TableUtils.createTableIfNotExists(connectionSource, SubCategory.class);
            TableUtils.createTableIfNotExists(connectionSource, CompletedSiteInfo.class);
            TableUtils.createTableIfNotExists(connectionSource,CompleteCallLogInfo.class);
            TableUtils.createTableIfNotExists(connectionSource,ImageModel.class);
            TableUtils.createTableIfNotExists(connectionSource,VisitPurposeModel.class);
            TableUtils.createTableIfNotExists(connectionSource,VisitPurposeAnswer.class);

        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        } catch (java.sql.SQLException e) {
          //  e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

      try {
          this.connectionSource=connectionSource;
          this.oldVersion=oldVersion;
          this.database=database;
          TableUtils.dropTable(connectionSource, Answers.class, true);
          TableUtils.dropTable(connectionSource, CallLoggedList.class, true);
          TableUtils.dropTable(connectionSource, CategoryList.class, true);
          TableUtils.dropTable(connectionSource, Login.class, true);
          TableUtils.dropTable(connectionSource, MicroCategory.class, true);
          TableUtils.dropTable(connectionSource, QuestionList.class, true);
          TableUtils.dropTable(connectionSource, QuestionNotToShow.class, true);
          TableUtils.dropTable(connectionSource, QuestionRequiredField.class, true);
          TableUtils.dropTable(connectionSource, SiteList.class, true);
          TableUtils.dropTable(connectionSource, SubAnswer.class, true);
          TableUtils.dropTable(connectionSource, SubCategory.class, true);
          TableUtils.dropTable(connectionSource, CompletedSiteInfo.class, true);
          TableUtils.dropTable(connectionSource,CompleteCallLogInfo.class,true);
          TableUtils.dropTable(connectionSource, ImageModel.class, true);
          TableUtils.dropTable(connectionSource, VisitPurposeModel.class, true);
          TableUtils.dropTable(connectionSource,VisitPurposeAnswer.class,true);

          onCreate(database, connectionSource);
      }catch(SQLException e){
          //e.printStackTrace();
      }catch (java.sql.SQLException e) {
         // e.printStackTrace();
      }

    }

    public void clearAllData()
    {
        onUpgrade(database,getConnectionSource(),oldVersion,DATABASE_VERSION);
    }
    public Dao<Answers, Integer> getAnswersDao() {
        if (null == answerDao) {
            try {
                answerDao = getDao(Answers.class);
            }catch (java.sql.SQLException e) {
             //   e.printStackTrace();
            }
        }
        return answerDao;
    }
    public Dao<CallLoggedList, Integer> getCallLoggedListDao() {
        if (null == callLoggedListDao) {
            try {
                callLoggedListDao = getDao(CallLoggedList.class);
            }catch (java.sql.SQLException e) {
             //   e.printStackTrace();
            }
        }
        return callLoggedListDao;
    }
    public Dao<CompleteCallLogInfo, Integer> getCompleteCallLoggedListDao() {
        if (null == CompleteCallLogInfoDao) {
            try {
                CompleteCallLogInfoDao = getDao(CompleteCallLogInfo.class);
            }catch (java.sql.SQLException e) {
               // e.printStackTrace();
            }
        }
        return CompleteCallLogInfoDao;
    }
    public Dao<CategoryList, Integer> getCategoryListDao() {
        if (null == categoryListDao) {
            try {
                categoryListDao = getDao(CategoryList.class);
            }catch (java.sql.SQLException e) {
          //      e.printStackTrace();
            }
        }
        return categoryListDao;
    }
    public Dao<Login, Integer> getLoginDao() {
        if (null == loginDao) {
            try {
                loginDao = getDao(Login.class);
            }catch (java.sql.SQLException e) {
               // e.printStackTrace();
            }
        }
        return loginDao;
    }
    public Dao<MicroCategory, Integer> getMicroCategoryDao() {
        if (null == microCategoryDao) {
            try {
                microCategoryDao = getDao(MicroCategory.class);
            }catch (java.sql.SQLException e) {
               // e.printStackTrace();
            }
        }
        return microCategoryDao;
    }
    public Dao<QuestionList, Integer> getQuestionListDao() {
        if (null == questionListDao) {
            try {
                questionListDao = getDao(QuestionList.class);
            }catch (java.sql.SQLException e) {
               // e.printStackTrace();
            }
        }
        return questionListDao;
    }
    public Dao<VisitPurposeAnswer,Integer> getVisiAnswerDao()
    {
        if(null==visiAnswerDao)
        {
            try
            {
                visiAnswerDao=getDao(VisitPurposeAnswer.class);
            }
            catch (java.sql.SQLException e)
            {
               // e.printStackTrace();
            }
        }
        return  visiAnswerDao;
    }
      public Dao<QuestionNotToShow, Integer> getQuestionNotToShowDao() {
        if (null == questionNotToShowDao) {
            try {
                questionNotToShowDao = getDao(QuestionNotToShow.class);
            }catch (java.sql.SQLException e) {
                //e.printStackTrace();
            }
        }
        return questionNotToShowDao;
    }
    public Dao<QuestionRequiredField, Integer> getQuestionRequiredFieldDao() {
        if (null == questionRequiredFieldDao) {
            try {
                questionRequiredFieldDao = getDao(QuestionRequiredField.class);
            }catch (java.sql.SQLException e) {
              //  e.printStackTrace();
            }
        }
        return questionRequiredFieldDao;
    }
    public Dao<SiteList, Integer> getSiteListDao() {
        if (null == siteListDao) {
            try {
                siteListDao = getDao(SiteList.class);
            }catch (java.sql.SQLException e) {
              //  e.printStackTrace();
            }
        }
        return siteListDao;
    }
    public Dao<SubAnswer, Integer> getSubAnswerDao() {
        if (null == subAnswerDao) {
            try {
                subAnswerDao = getDao(SubAnswer.class);
            }catch (java.sql.SQLException e) {
               // e.printStackTrace();
            }
        }
        return subAnswerDao;
    }
    public Dao<SubCategory, Integer> getSubCategoryDao() {
        if (null == subCategoryDao) {
            try {
                subCategoryDao = getDao(SubCategory.class);
            }catch (java.sql.SQLException e) {
              //  e.printStackTrace();
            }
        }
        return subCategoryDao;
    }

    public Dao<QuestionListCache, Integer> getQuestionListCacheDao() {
        if (null == questionListCacheDao) {
            try {
                questionListCacheDao = getDao(QuestionListCache.class);
            }catch (java.sql.SQLException e) {
                //e.printStackTrace();
            }
        }
        return questionListCacheDao;
    }
    public Dao<CompletedSiteInfo, Integer> getCompletedSiteInfoDaoDao() {
        if (null == completedSiteInfoDao) {
            try {
                completedSiteInfoDao = getDao(CompletedSiteInfo.class);
            }catch (java.sql.SQLException e) {
               // e.printStackTrace();
            }
        }
        return completedSiteInfoDao;
    }
    public Dao<ImageModel,Integer> getImageDao()
    {
        if(null==imageDao)
        {
            try
            {
                imageDao=getDao(ImageModel.class);
            }catch (java.sql.SQLException e)
            {
              //  e.printStackTrace();
            }
        }
        return imageDao;
    }
    public Dao<VisitPurposeModel,Integer> getVisitPurposeDao()
    {
        if(null==visitDao)
        {
            try
            {
                visitDao=getDao(VisitPurposeModel.class);
            }catch (java.sql.SQLException e)
            {
             //   e.printStackTrace();
            }
        }
        return visitDao;
    }
}
