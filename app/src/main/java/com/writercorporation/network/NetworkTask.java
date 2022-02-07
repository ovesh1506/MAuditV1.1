package com.writercorporation.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.writercorporation.database.DatabaseManager;
import com.writercorporation.maudit.R;
import com.writercorporation.model.LoginReq;
import com.writercorporation.model.LoginResp;
import com.writercorporation.model.SiteReq;
import com.writercorporation.utils.AppConstant;
import com.writercorporation.utils.GenericType;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.writercorporation.utils.AppConstant.SERVER_URL;
import static com.writercorporation.utils.AppConstant.getCallDetails;
import static com.writercorporation.utils.AppConstant.login;

/**
 * Created by amol.tate on 9/29/2015.
 */
 public class NetworkTask extends AsyncTask<String,String,String> {
    Context context;
    GenericType params;
    OnTaskComplete taskCompleteObj;
    String method;
    DatabaseManager dManager;
    int id;
    AppConstant app;
    ApiService api;

    public NetworkTask(Context context, OnTaskComplete taskCompleteObj, GenericType params, String method) {
        this.context = context;
        this.taskCompleteObj = taskCompleteObj;
        this.params = params;
        this.method = method;
    }

    public NetworkTask(Context context,  OnTaskComplete taskCompleteObj,String params, String method, int id) {
        this.context = context;
        //this.params = params;
        this.taskCompleteObj = taskCompleteObj;
        this.method = method;
        this.id = id;
    }

    @Override
    protected String doInBackground(String... param) {
        return performPostCall();
    }

    @Override
    protected void onPostExecute(String result) {
        //taskCompleteObj.dismissDialog(result);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        taskCompleteObj.onPublishResult(values[0]);
    }

    public String performPostCall() {
        URL url;
        String apiResponse = "";

        try {
            AppConstant.trustAllHosts();
            if (method.equalsIgnoreCase("Login")) {
                dManager = DatabaseManager.getInstance();
                app = AppConstant.getInstance();
                api = app.getClient().create(ApiService.class);
                LoginReq req = (LoginReq) params.getObject();
                //JSONObject creadentials = new JSONObject(params);

                //if (dManager.authenticateLocal(creadentials.optString("UserName"), creadentials.optString("Password"))) {
                //    return context.getString(R.string.success);
                //}
                if(dManager.authenticateLocal(req.getUserName(),req.getPassword())){
                    return context.getString(R.string.success);
                }
            }


            switch (method){
                case login :{
                    LoginReq para = (LoginReq) params.getObject();
                    apiResponse = callLoginApi(para);
                    break;
                }

                case getCallDetails :{
                    SiteReq para = (SiteReq) params.getObject();
                    apiResponse = callLogDetails(para);
                    break;
                }
            }

//            url = new URL(SERVER_URL+"/"+method);
//
//            Log.e("Url",SERVER_URL + "/"+method);
//            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
//            OkHttpClient client = new OkHttpClient();
//            client.networkInterceptors().add(new StethoInterceptor());
//
//
//
//            conn.setRequestProperty("Content-Type","application/json");
//            //conn.setReadTimeout(6000000);
//            //conn.setConnectTimeout(6000000);
//            conn.setRequestMethod("POST");
//            conn.setDoInput(true);
//            conn.setDoOutput(true);
//
//
//
//
//            OutputStream os = conn.getOutputStream();
//            BufferedWriter writer = new BufferedWriter(
//                    new OutputStreamWriter(os, "UTF-8"));
//            //String str = "{\"__type\":\"LoginInput:#MAuditRestService\",\"UserName\":\"777\",\"Password\":\"7777\",\"IMEINO\":\"1234\"}";
//            os.write(params.getBytes());
//           // os.write(str.getBytes());
//          //writer.write(getPostDataString(postDataParams));
//
//            writer.flush();
//            writer.close();
//            os.close();
//            int responseCode=conn.getResponseCode();
//
//            Log.e("Response 0",conn.getResponseMessage());
//            //Log.e("Response 1",conn.toString());
//
//
//
//
//            if (responseCode == HttpsURLConnection.HTTP_OK) {
//                String line;
//                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                while ((line=br.readLine()) != null) {
//                    response+=line;
//                }
//            }
//            else {
//                return "Bad Request";
//
//            }
//            Log.e("Try Response ",response);
//        }catch(SocketTimeoutException ste){
//            Log.e("Socket Exception",ste.getMessage());
//            response =context.getString(R.string.error_100_slow_internet);
//            return response;
//        }


        }
        catch (Exception e) {
            Log.e("Exception e",e.getMessage());
            e.printStackTrace();
        }
        //publishProgress("Saving Data...");
        //apiResponse = taskCompleteObj.onTaskComplete(apiResponse);
        //taskCompleteObj.onTaskComplete(apiResponse,id);
        //Log.e("Final Response ", apiResponse);
        return apiResponse;
    }

    public String callLoginApi(LoginReq req){
        final String[] result = {""};
        app = AppConstant.getInstance();
        api = app.getClient().create(ApiService.class);
        Call<LoginResp> calllog = api.login(req);

        calllog.enqueue(new Callback<LoginResp>() {
        @Override
        public void onResponse(Call<LoginResp> call, Response<LoginResp> response) {

          if(response.body()!=null && response.body().getLoginStatus().equalsIgnoreCase("Y")){
               Log.e("Response Success",response.body().getLoginStatus() + " ");
               result[0] = response.body().toString();
          }else{
              result[0] = "";
              //if(response.body()!=null && response.body().getLoginStatus().equalsIgnoreCase("N"))
                //result[0] = "Invalid username & Password";
              //else
                //result[0] = response.code()  + response.message();
          }
            publishProgress("Saving Data...");
            result[0] = taskCompleteObj.onTaskComplete(result[0]);
            taskCompleteObj.onTaskComplete(result[0],id);
        }
        @Override
        public void onFailure(Call<LoginResp> call, Throwable t) {
            result[0] = "";//t.getMessage();
            Log.e("onFailure",t.getLocalizedMessage());
            publishProgress("Unable to save data...");
            result[0] = taskCompleteObj.onTaskComplete(result[0]);
            taskCompleteObj.onTaskComplete(result[0],id);
        }
        });
        //publishProgress("Saving Data...");
        //result[0] = taskCompleteObj.onTaskComplete(result[0]);
        //taskCompleteObj.onTaskComplete(result[0],id);
        //Log.e("Final Response ", apiResponse);
        return null;
    }

    public String callLogDetails(SiteReq req){
        final String[] result = {""};
        Call<LoginResp> call = api.getCallLog(req);

        call.enqueue(new Callback<LoginResp>() {
            @Override
            public void onResponse(Call<LoginResp> call, Response<LoginResp> response) {

                if(response.body()!=null && response.body().getLoginStatus().equalsIgnoreCase("Y")){
                    Log.e("Response Success",response.body().getLoginStatus() + " ");
                    result[0] = response.body().toString();
                }else{
                    if(response.body()!=null && response.body().getLoginStatus().equalsIgnoreCase("N"))
                        result[0] = "Invalid username & Password";
                    else
                        result[0] = response.code()  + response.message();
                }
            }
            @Override
            public void onFailure(Call<LoginResp> call, Throwable t) {
                result[0] = t.getMessage();
                Log.e("onFailure",t.getLocalizedMessage());
            }
        });
        return result[0];
    }


    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }


}
