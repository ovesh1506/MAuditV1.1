package com.writercorporation.network;

import android.content.Context;
import android.os.AsyncTask;

import com.writercorporation.database.DatabaseManager;
import com.writercorporation.maudit.R;
import com.writercorporation.utils.AppConstant;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static com.writercorporation.utils.AppConstant.SERVER_URL;

/**
 * Created by amol.tate on 9/29/2015.
 */
 public class NetworkTask extends AsyncTask<String,String,String> {
    Context context;
    String params;
    OnTaskComplete taskCompleteObj;
    String method;
    DatabaseManager dManager;
    int id;



    public NetworkTask(Context context, OnTaskComplete taskCompleteObj, String params, String method) {
        this.context = context;
        this.taskCompleteObj = taskCompleteObj;
        this.params = params;
        this.method = method;

    }

    public NetworkTask(Context context,  OnTaskComplete taskCompleteObj,String params, String method, int id) {
        this.context = context;
        this.params = params;
        this.taskCompleteObj = taskCompleteObj;
        this.method = method;
        this.id = id;
    }

    @Override
    protected String doInBackground(String... params) {
        return performPostCall();
    }

    @Override
    protected void onPostExecute(String result) {
        taskCompleteObj.dismissDialog(result);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        taskCompleteObj.onPublishResult(values[0]);
    }

    public String performPostCall() {
        URL url;
        String response = "";
        try {
            AppConstant.trustAllHosts();
            if(method.equalsIgnoreCase("Login")) {
                dManager = DatabaseManager.getInstance();
                JSONObject creadentials = new JSONObject(params);

                if (dManager.authenticateLocal(creadentials.optString("UserName"), creadentials.optString("Password")) ) {
                    return context.getString(R.string.success);
                }
            }
            url = new URL(SERVER_URL+"/"+method);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type","application/json");
            conn.setReadTimeout(60000);
            conn.setConnectTimeout(60000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            //String str = "{\"__type\":\"LoginInput:#MAuditRestService\",\"UserName\":\"777\",\"Password\":\"7777\",\"IMEINO\":\"1234\"}";
            os.write(params.getBytes());
           // os.write(str.getBytes());
          //writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";

            }
        }catch(SocketTimeoutException ste){
            response =context.getString(R.string.error_100_slow_internet);
            return response;
        }

        catch (Exception e) {
            e.printStackTrace();

        }
        publishProgress("Saving Data...");
        response = taskCompleteObj.onTaskComplete(response);
        taskCompleteObj.onTaskComplete(response,id);
        return response;
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
