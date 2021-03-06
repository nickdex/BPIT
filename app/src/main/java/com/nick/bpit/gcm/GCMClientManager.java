package com.nick.bpit.gcm;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.nick.bpit.server.Config;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class GCMClientManager implements Config
{
    private static final String TAG = "GCMClientManager";
    private static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private Activity activity;
    private GoogleCloudMessaging gcm;
    private String regId;
    private AsyncTask<Void, Void, String> sendTask;
    private AtomicInteger msgId = new AtomicInteger();

    public GCMClientManager(Activity activity)
    {
        this.activity = activity;
        this.gcm = GoogleCloudMessaging.getInstance(activity);
    }

    private static int getAppVersion(Context context)
    {
        try
        {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        }
        catch (PackageManager.NameNotFoundException ex)
        {
            throw new RuntimeException("Could not get package name : " + ex);
        }
    }

    public Context getContext()
    {
        return activity;
    }

    public Activity getActivity()
    {
        return activity;
    }

    public void registerIfNeeded(final RegistrationCompleteHandler handler)
    {
        if (checkPlayServices())
        {
            regId = getRegistrationId(getContext());

            if (regId.isEmpty())
                registerInBackground(handler);
            else
            {
                Log.i(TAG, regId);
                handler.onSuccess(regId, false);
            }
        }
        else
        {
            Log.i(TAG, "No valid Google Play Services APK found");
        }
    }

    private void registerInBackground(final RegistrationCompleteHandler handler)
    {
        new AsyncTask<Void, Void, String>()
        {
            @Override
            protected String doInBackground(Void... params)
            {
                try
                {
                    if (gcm == null)
                        gcm = GoogleCloudMessaging.getInstance(getContext());
                    InstanceID instanceID = InstanceID.getInstance(getContext());
                    regId = instanceID.getToken(PROJECT_NUMBER, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
                    Log.i(TAG, "Token = "+regId);

                    storeRegistrationId(getContext(), regId);
                }
                catch (IOException ex)
                {
                    handler.onFailure("Error : " + ex.getMessage());
                }
                return regId;
            }

            @Override
            protected void onPostExecute(String regId)
            {
                super.onPostExecute(regId);
                if (regId != null)
                    handler.onSuccess(regId, true);
            }
        }.execute();
    }

    private String getRegistrationId(Context context)
    {
        final SharedPreferences sharedPreferences = getGCMPreferences(context);
        String registrationId = sharedPreferences.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty())
        {
            Log.i(TAG, "App version Changed.");
            return "";
        }
        return registrationId;
    }

    private void storeRegistrationId(Context context, String regId)
    {
        final SharedPreferences sharedPreferences = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version : " + appVersion);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.apply();
        //editor.commit();
    }

    private boolean checkPlayServices()
    {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());
        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(), PLAY_SERVICES_RESOLUTION_REQUEST).show();
            else
                Log.i(TAG, "This device is not supported.");
            return false;
        }
        return true;
    }

    public void sendMessage(final Bundle data)
    {
        sendTask = new AsyncTask<Void, Void, String>()
        {
            @Override
            protected String doInBackground(Void... params)
            {
                String id = Integer.toString(msgId.incrementAndGet());
                try
                {
                    Log.d(TAG, "message_id: " + id);
                    gcm.send(PROJECT_NUMBER + "@gcm.googleapis.com", id, data);
                }
                catch (Exception e)
                {
                    Log.d(TAG, "Exception: " + e);
                    e.printStackTrace();
                }
                return "GCM send is a success";
            }

            @Override
            protected void onPostExecute(String result)
            {
                super.onPostExecute(result);
                sendTask = null;
                Log.d(TAG, "Result = " + result);
                //Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
            }
        };
        sendTask.execute();
    }

    private SharedPreferences getGCMPreferences(Context context)
    {
        return getContext().getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public static abstract class RegistrationCompleteHandler
    {
        public abstract void onSuccess(String registrationId, boolean isNewRegistration);

        public void onFailure(String ex)
        {
            Log.e(TAG, ex);
        }
    }
}