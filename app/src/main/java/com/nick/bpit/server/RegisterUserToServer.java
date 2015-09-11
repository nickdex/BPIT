package com.nick.bpit.server;

import android.os.AsyncTask;
import android.util.Log;

import com.nick.bpit.Config;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class RegisterUserToServer
{
    private String TAG = "RegisterToServer";
    public AsyncTask<String, Void, String> sendRegId = new AsyncTask<String, Void, String>()
    {
        @Override
        protected String doInBackground(String... params)
        {
            String result = "";
            String email = params[0];
            String registrationId = params[1];

            try
            {
                URL serverUrl = null;
                try
                {
                    serverUrl = new URL(Config.APP_SERVER_URL);
                }
                catch (MalformedURLException e)
                {
                    Log.e(TAG, "URL Connection Error: " + Config.APP_SERVER_URL);
                    result = "Invalid URL: " + Config.APP_SERVER_URL;
                }

                StringBuilder temp = new StringBuilder();
                temp.append(email).append(Config.SEPARATOR).append(registrationId);
                String record = URLEncoder.encode("record", "UTF-8") + "=" + URLEncoder.encode(temp.toString(), "UTF-8");
                Log.i(TAG, record);
                byte[] bytes = record.getBytes();
                HttpURLConnection httpURLConnection = null;
                try
                {
                    httpURLConnection = (HttpURLConnection) serverUrl.openConnection();
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setFixedLengthStreamingMode(bytes.length);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                    OutputStream out = httpURLConnection.getOutputStream();
                    out.write(bytes);
                    out.close();
                    int status = httpURLConnection.getResponseCode();
                    if (status == 200)
                        result = "RegId shared with Application Server. RegId: " + record;
                    else
                        result = "Post Failure." + " Status: " + status;
                }
                catch (NullPointerException e)
                {
                    Log.e(TAG, "Exception : " + e);
                } finally
                {
                    if (httpURLConnection != null)
                        httpURLConnection.disconnect();
                }
            }
            catch (IOException e)
            {
                result = "Post Failure. Error in sharing with App Server.";
                Log.e("AppUtil", "Error in sharing with App Server: " + e);
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            Log.i(TAG, result);
        }
    };
}