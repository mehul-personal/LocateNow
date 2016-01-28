package com.analytics.locatenow;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GcmRegistrationAsyncTask extends AsyncTask<Void, Void, String> {
    // TODO: change to your own sender ID to Google Developers Console project number, as per instructions above
    private static final String SENDER_ID = "852758362485";
    String regId = "";
    // private static Registration regService = null;
    private GoogleCloudMessaging gcm;
    private Context context;
    static final String SERVER_URL = "http://192.95.6.213/locate/register_your_mobile_device.php";
    public GcmRegistrationAsyncTask(Context context) {
        this.context = context;
    }

    @SuppressWarnings("deprecation")
	@Override
    protected String doInBackground(Void... params) {
        SharedPreferences prefers = context.getSharedPreferences(
                "LOGIN_DETAIL", 0);
        final String user_id = prefers.getString("USER_ID", "");

        String msg = "";
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(context);
            }
            String regId = gcm.register(SENDER_ID);
            msg = "Device registered, registration ID=" + regId;

            // You should send the registration ID to your server over HTTP,
            // so it can use GCM/HTTP or CCS to send messages to your app.
            // The request to your server should be authenticated if your app
            // is using accounts.
            // regService.register(regId).execute();
            String device_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            String device_Name = android.os.Build.MODEL;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(SERVER_URL);
            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        3);

                nameValuePairs
                        .add(new BasicNameValuePair(
                                "Action",
                                "RegisterDevice"));
                nameValuePairs
                        .add(new BasicNameValuePair(
                                "device_name",
                                device_Name));
                nameValuePairs
                        .add(new BasicNameValuePair(
                                "device_id",
                                device_id));
                nameValuePairs
                        .add(new BasicNameValuePair(
                                "device_type",
                                "Android"));
                nameValuePairs
                        .add(new BasicNameValuePair(
                                "device_token",
                                regId));
                nameValuePairs
                        .add(new BasicNameValuePair(
                                "user_id",
                                user_id));

                httppost.setEntity(new UrlEncodedFormEntity(
                        nameValuePairs));

                // Execute HTTP Post
                // Request
                HttpResponse response = httpclient
                        .execute(httppost);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                response.getEntity()
                                        .getContent()));
                StringBuffer sb = new StringBuffer(
                        "");
                String line = "";
                while ((line = in
                        .readLine()) != null) {
                    sb.append(line);
                }
                in.close();
                Log.e("register device data", ""
                        + sb.toString());
                return sb.toString();
            } catch (Exception e) {
                Log.e("error on reg device",
                        "" + e);
                return "";
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            msg = "Error: " + ex.getMessage();
        }
        return msg;
    }

    @Override
    protected void onPostExecute(String msg) {
        //  Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        Logger.getLogger("REGISTRATION").log(Level.INFO, msg);
    }

}
