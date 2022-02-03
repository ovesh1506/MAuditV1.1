package com.writercorporation.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.dao.LazyForeignCollection;
import com.writercorporation.database.DatabaseManager;
import com.writercorporation.maudit.R;
import com.writercorporation.model.Answers;
import com.writercorporation.model.CategoryList;
import com.writercorporation.model.MicroCategory;
import com.writercorporation.model.QuestionList;
import com.writercorporation.network.CustomBroadCast;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.facebook.FacebookSdk.getCacheDir;
import static com.facebook.FacebookSdk.setAutoLogAppEventsEnabled;


public class AppConstant extends Application {
    private Context _context;
    private static AppConstant instance;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private DatabaseManager dManager;
    private static final String SITECODE = "sitecode";
    private static final String SITEID = "siteID";
    private static final String SITENAME = "siteName";
    public static final String LOGIN_TYPE = "LoginType";
    private static final String PTRANSID = "PTRANSID";
    private static final String USERID = "USERID";
    private static final String LastPosition = "LastPosition";
    private static final String SITE_AUDIT_DATE = "AUDITDATE";
    public static final String LockTitleMsg = "Enable Fingerprint Access for login";
    public static final String LockBodyMsg = "Press Yes to allow fingerprint access for MAudit Login";

    private static Retrofit retrofit;
    private static final String IsLock = "ISLOCK";

    //public static final String SERVER_URL = "https://wsgsvc.writercorporation.com/MAuditService/MAuditRest.svc";//Pilot
    //public static final String SERVER_URL = "https://wsgsvc.writercorporation.com/MAuditService_UAT/MAuditRest.svc";//UAT
    public static final String SERVER_URL = "https://wsguat.writercorporation.com/MAuditService_UAT/MAuditRest.svc/";//UAT
    //public static final String SERVER_URL = "https://wsg.writercorporation.com/MAuditService/MAuditRest.svc";//Live
    public static final String KEY_CATEGORY_LIST = "CategoryList";

    public static synchronized AppConstant getInstance() {
        Log.e("GetInstance","called");
//        if(instance == null){
//            instance = new AppConstant();
//        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("AppConstant class",this.getApplicationContext().getPackageName());
        DatabaseManager.init(this);
        instance = this;
        this._context = this;
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(_context);
        editor = sharedPrefs.edit();
        editor.apply();
        gson = new Gson();


        Type ansForeignType = new TypeToken<LazyForeignCollection<Answers, Integer>>() {
        }.getType();
        gson = new GsonBuilder().registerTypeAdapter(Answers.class, new AnswerSerialize()).
                registerTypeAdapter(MicroCategory.class, new MicroCategorySerialize()).
                registerTypeAdapter(ansForeignType, new ForeignAnswerCollection()).excludeFieldsWithoutExposeAnnotation().create();
        dManager = DatabaseManager.getInstance();

        Stetho.initializeWithDefaults(this.getApplicationContext());

        //facebook stuff
        //FacebookSdk.sdkInitialize(getApplicationContext());
        //AppEventsLogger.activateApp(this);
        //FacebookSdk.setAutoLogAppEventsEnabled(true);

    }

//    public AppConstant(Context _context) {
//        Log.e("AppConstant class","called");
//        this._context = _context;
//        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(_context);
//        editor = sharedPrefs.edit();
//        editor.apply();
//        gson = new Gson();
//
//
//        Type ansForeignType = new TypeToken<LazyForeignCollection<Answers, Integer>>() {
//        }.getType();
//        gson = new GsonBuilder().registerTypeAdapter(Answers.class, new AnswerSerialize()).
//                registerTypeAdapter(MicroCategory.class, new MicroCategorySerialize()).
//                registerTypeAdapter(ansForeignType, new ForeignAnswerCollection()).excludeFieldsWithoutExposeAnnotation().create();
//        dManager = DatabaseManager.getInstance();
//
//    }

    public Retrofit getClient(){

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);


        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(SERVER_URL)
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                            .setLenient()
                            .setDateFormat("yyyy-MM-dd HH:mm:ss")
                            .create()))
                    .client(new OkHttpClient.Builder()
                            .connectTimeout(10, TimeUnit.MINUTES)
                            .writeTimeout(10,TimeUnit.MINUTES)
                            .readTimeout(10, TimeUnit.MINUTES)
                            .addNetworkInterceptor(new StethoInterceptor())
//                            .retryOnConnectionFailure(true)
                            //.cache(new Cache(getCacheDir(), 1024 * 1024)) // 1 MiB
                            .addInterceptor(logging)

                            .build())
                    .build();
//                            .authenticator(new RefTokenAuth())
//                            .addInterceptor(chain -> {
//                                Response res = chain.proceed(chain.request());
//                                if (res.code() == 406) {
//                                    BaseRepo.getInstance().truncateAllTables();
//                                    getDatabase().clearAllTables();
//                                    sharedpreferences.edit().clear().apply();
//                                    try {
//                                        JobManager.instance().cancelAll();
//                                    } catch (Exception a) {
//                                        Log.e("JobManager", "cancelAll", a);
//                                    }
//                                    startActivity(new Intent(mInstance, Login.class)
//                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
//                                }
//                                return res;
//                            })
                            //.build())
                    //.build();
        }
        Log.e("Retorfit",retrofit.toString());
        return retrofit;
    }



    public static String getLocalIpAddressString() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("IPADDRESS", ex.toString());
        }
        return null;
    }

    static public void initial(Context ctx) {
        if (null == instance) {
            instance = new AppConstant();
        }

    }

    public void setSiteCode(String SiteCode) {
        editor.putString(SITECODE, SiteCode);
        editor.commit();
    }

    public void setIsLock(Boolean lock){
        editor.putBoolean(IsLock,lock);
        editor.commit();
    }

    public Boolean getIsLock() {
        return sharedPrefs.getBoolean(IsLock,false);
    }

    public int getSiteID() {
        return sharedPrefs.getInt(SITEID, 0);
    }

    public void setSiteID(int SiteCode) {
        editor.putInt(SITEID, SiteCode);
        editor.commit();
    }

    public int getSiteLastPos() {
        return sharedPrefs.getInt(LastPosition, -1);
    }

    public void setSiteLastPos(int postion) {
        editor.putInt(LastPosition, postion);
        editor.commit();
    }

    public void setSiteAuditDate(String date) {
        editor.putString(SITE_AUDIT_DATE, date);
        editor.commit();
    }

    public void setSitename(String siteName) {
        editor.putString(SITENAME, siteName);
        editor.commit();
    }

    public String getSiteName() {
        return sharedPrefs.getString(SITENAME, "");
    }

    public String getSiteAuditDate() {
        return sharedPrefs.getString(SITE_AUDIT_DATE, "");
    }

    public String getSiteCode() {
        return sharedPrefs.getString(SITECODE, "");
    }

    public String PtransID() {
        long unixtimeStamp = 0l;
        try {

            DateFormat sdm = new SimpleDateFormat("yyyyMMddHHmmss");
            String datecurrent = sdm.format(new Date());
            Date dat = sdm.parse(datecurrent);
            unixtimeStamp = (long) dat.getTime() / 1000;
        } catch (Exception e) {
        }
        return String.valueOf(unixtimeStamp);
    }

    public void setPTransID() {
        editor.putString(PTRANSID, PtransID());
        editor.commit();
    }

    public String getPTransID() {
        return sharedPrefs.getString(PTRANSID, "");
    }

    public void setUserID(String userID) {
        editor.putString(USERID, userID);
        editor.commit();
    }

    public String getUserID() {
        return sharedPrefs.getString(USERID, "");
    }


    public void showErrorMessage(String msg) {
        Toast toast = Toast.makeText(_context, "", Toast.LENGTH_SHORT);
        LayoutInflater inflater = LayoutInflater.from(_context);
        View toastRoot = inflater.inflate(R.layout.toast_msg, null);
        TextView errorText = (TextView) toastRoot.findViewById(R.id.errorText);
        LinearLayout messageLayOut = (LinearLayout) toastRoot.findViewById(R.id.connectionErrorLayout);
        errorText.setTextSize(14);
        errorText.setText("\n" + msg + "\n");
        messageLayOut.setBackground(_context.getResources().getDrawable(R.color.toastBg));

        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(toastRoot);
        toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
        toast.show();
    }

    public SweetAlertDialog showSweetAlertDialog(Context context, int type, boolean isCancalable, String message, String contentText) {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context);
        sweetAlertDialog.changeAlertType(type);
        sweetAlertDialog.setTitleText(message);
        if (contentText != null)
            sweetAlertDialog.setContentText(contentText);
        sweetAlertDialog.setCancelable(isCancalable);
        return sweetAlertDialog;

    }
   /* public void dismissSweetAlertDialog()
    {
        if(sweetAlertDialog.isShowing() && sweetAlertDialog!=null)
              sweetAlertDialog.dismiss();
    }*/

    public String getDeviceUniqueID(Activity activity) {
        String device_unique_id = Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return device_unique_id;
    }

    public void setCategoryData(ArrayList<QuestionList> questionList, String KEY) {
        Type type = new TypeToken<List<QuestionList>>() {
        }.getType();
        String json = gson.toJson(questionList, type);
        editor.putString(KEY, json);
        editor.commit();
    }

    public void setCategoryList(ArrayList<CategoryList> categoryList) {
        Type type = new TypeToken<List<CategoryList>>() {
        }.getType();
        String json = gson.toJson(categoryList, type);
        editor.putString(KEY_CATEGORY_LIST, json);
        editor.commit();
    }

    public ArrayList<CategoryList> getCatergoryList() {
        String json = sharedPrefs.getString(KEY_CATEGORY_LIST, null);

        if (json == null)
            return null;
        ArrayList<CategoryList> arrayList = null;
        try {
            Type type = new TypeToken<ArrayList<CategoryList>>() {
            }.getType();
            arrayList = gson.fromJson(json, type);
            for (int i = 0; i < arrayList.size(); i++) {
                CategoryList categoryList = arrayList.get(i);
                dManager.getHelper().getCategoryListDao().assignEmptyForeignCollection(categoryList, "subCategories");
                // que.setAnswersForeignCollection(que.getList());
                arrayList.set(i, categoryList);
            }
        } catch (SQLException sqe) {
            //sqe.printStackTrace();
        }
        return arrayList;
    }

    public ArrayList<QuestionList> getCategoryData(String KEY) {
        String json = sharedPrefs.getString(KEY, null);
        ArrayList<QuestionList> arrayList = new ArrayList<>();
        if (json == null)
            return arrayList;

        try {
            Type type = new TypeToken<ArrayList<QuestionList>>() {
            }.getType();
            arrayList = gson.fromJson(json, type);
            for (int i = 0; i < arrayList.size(); i++) {
                QuestionList que = arrayList.get(i);
                dManager.getHelper().getQuestionListDao().assignEmptyForeignCollection(que, "answersForeignCollection");
                // que.setAnswersForeignCollection(que.getList());
                arrayList.set(i, que);
            }
        } catch (Exception se) {
            se.printStackTrace();
        }
        return arrayList;
    }

    public static class AnswerSerialize implements JsonSerializer<Answers>, JsonDeserializer<Answers> {
        public JsonElement serialize(final Answers answer, final Type type, final JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.add("id", new JsonPrimitive(answer.getId()));
            result.add("answerText", new JsonPrimitive(answer.getAnswerText()));
            result.add("callLog", new JsonPrimitive(answer.getCallLog()));
            result.add("controlCaption", new JsonPrimitive(answer.getControlCaption()));
            result.add("controlType", new JsonPrimitive(answer.getControlType()));
            result.add("isChildShow", new JsonPrimitive(answer.getIsChildShow()));
            result.add("controlValue", new JsonPrimitive(answer.getControlValue()));
            return result;
        }

        @Override
        public Answers deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject rootJson = (JsonObject) json;
            if (rootJson.has("answersForeignCollection")) {
                JsonArray jsonArray = rootJson.get("answersForeignCollection").getAsJsonArray();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
                    int id = jsonObject.get("id").getAsInt();
                    String answerText = jsonObject.get("answerText").getAsString();
                    String callLog = jsonObject.get("callLog").getAsString();
                    String controlCaption = jsonObject.get("controlCaption").getAsString();
                    String controlType = jsonObject.get("controlType").getAsString();
                    String isChildShow = jsonObject.get("isChildShow").getAsString();
                    String controlValue = jsonObject.get("controlValue").getAsString();

                    String textInputType = jsonObject.get("textInputType").getAsString();
                    return new Answers(id, answerText, callLog, controlCaption, controlType, textInputType,isChildShow, controlValue, null);
                }
            } else {
                JsonObject jsonObject = (JsonObject) json;
                int id = jsonObject.get("id").getAsInt();
                String answerText = jsonObject.get("answerText").getAsString();
                String callLog = jsonObject.get("callLog").getAsString();
                String controlCaption = jsonObject.get("controlCaption").getAsString();
                String controlType = jsonObject.get("controlType").getAsString();
                String isChildShow = jsonObject.get("isChildShow").getAsString();
                String controlValue = jsonObject.get("controlValue").getAsString();
                String textInputType = jsonObject.get("textInputType").getAsString();
                return new Answers(id, answerText, callLog, controlCaption, controlType,textInputType, isChildShow, controlValue, null);
            }
            return null;
        }
    }

    public static class MicroCategorySerialize implements JsonSerializer<MicroCategory>, JsonDeserializer<MicroCategory> {
        public JsonElement serialize(final MicroCategory microCategory, final Type type, final JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.add("microCategoryServerId", new JsonPrimitive(microCategory.getId()));
            result.add("microCategoryName", new JsonPrimitive(microCategory.getMicroCategoryName()));
            result.add("microCategoryOrder", new JsonPrimitive(microCategory.getMicroCategoryOrder()));
            return result;
        }

        @Override
        public MicroCategory deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObj = (JsonObject) jsonElement;
            int microCategoryServerId = jsonObj.get("microCategoryServerId").getAsInt();
            String microCategoryName = jsonObj.get("microCategoryName").getAsString();
            int microCategoryOrder = jsonObj.get("microCategoryOrder").getAsInt();
            return new MicroCategory(microCategoryServerId, microCategoryName, microCategoryOrder, null);
        }
    }

    public static class ForeignAnswerCollection implements JsonSerializer<LazyForeignCollection<Answers, Integer>>, JsonDeserializer<LazyForeignCollection<Answers, Integer>> {
        @Override
        public JsonElement serialize(LazyForeignCollection<Answers, Integer> src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject rootObject = new JsonObject();
            JsonArray jsonArray = new JsonArray();
            for (Answers answer : src) {
                JsonObject result = new JsonObject();
                result.add("id", new JsonPrimitive(answer.getId()));
                result.add("answerText", new JsonPrimitive(answer.getAnswerText()));
                result.add("callLog", new JsonPrimitive(answer.getCallLog()));
                result.add("controlCaption", new JsonPrimitive(answer.getControlCaption()));
                result.add("controlType", new JsonPrimitive(answer.getControlType()));
                result.add("isChildShow", new JsonPrimitive(answer.getIsChildShow()));
                result.add("controlValue", new JsonPrimitive(answer.getControlValue()));
                jsonArray.add(result);
            }
            rootObject.add("answersForeignCollection", jsonArray);
            return null;
        }

        @Override
        public LazyForeignCollection<Answers, Integer> deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                JsonObject rootJson = (JsonObject) jsonElement;
                if (rootJson.has("answersForeignCollection")) {
                    JsonArray jsonArray = rootJson.get("answersForeignCollection").getAsJsonArray();
                    DatabaseManager dManager = DatabaseManager.getInstance();
                    LazyForeignCollection<Answers, Integer> foreignCollection = new LazyForeignCollection<>(null, null, null, null, null, false);
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
                        int id = jsonObject.get("id").getAsInt();
                        String answerText = jsonObject.get("answerText").getAsString();
                        String callLog = jsonObject.get("callLog").getAsString();
                        String controlCaption = jsonObject.get("controlCaption").getAsString();
                        String controlType = jsonObject.get("controlType").getAsString();
                        String isChildShow = jsonObject.get("isChildShow").getAsString();
                        String controlValue = jsonObject.get("controlValue").getAsString();
                        String textInputType = jsonObject.get("textInputType").getAsString();
                        Answers answer = new Answers(id, answerText, callLog, controlCaption, controlType, textInputType,isChildShow, controlValue, null);
                        foreignCollection.add(answer);
                        //return foreignCollection;
                        return null;
                    }
                }
            } catch (Exception s) {
                s.printStackTrace();
            }
            return null;
        }
    }

    public void clearPrefs() {
        String userId = sharedPrefs.getString(USERID, "");
        editor.clear();
        editor.putString(USERID, userId);
        editor.commit();

    }

    public void getPendingAlarmManager() {
        Intent atmtransactionuploader = new Intent(_context, CustomBroadCast.class);
        atmtransactionuploader.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(_context, 1, atmtransactionuploader, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), 900000, pendingIntent);
    }

    public static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
          //  e.printStackTrace();
        }
    }
}
