package com.nick.bpit.handler;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.nick.bpit.MainActivity;
import com.nick.bpit.R;
import com.nick.bpit.gcm.GCMClientManager;
import com.nick.bpit.server.Config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MessageProcessor implements Config
{
    public static final int NOTIFICATION_ID = 1000;
    private static final String TAG = "MessageProcessor";
    //Singleton, Check whether this will cause conflicts when downstreaming and upstreaming simultaneously
    private static MessageProcessor instance = new MessageProcessor();

    public static MessageProcessor getInstance()
    {
        return instance;
    }

    public void processDownstreamMessage(Bundle data, Context context)
    {
        DatabaseHandler databaseHandler = new DatabaseHandler(context);
        String action = data.getString(ACTION);
        String msg_body = data.getString(MESSAGE_BODY);
        if (action != null)
            switch (action)
            {
                case ACTION_REGISTER:
                {
                    String content = data.getString(MEMBER_NAME) + " has joined us";
                    createNotification(content, context);
                    showBundle(data);
                    data.remove("collapse_key");
                    data.remove(ACTION);
                    databaseHandler.insertMember(data);
                    break;
                }
                case ACTION_BROADCAST:
                {
                    createNotification(msg_body, context);
                    data.remove("collapse_key");
                    data.remove(ACTION);
                    databaseHandler.insertMessage(data);
                    break;
                }
                case ACTION_DEBUG:
                {
                    //DEBUG_CODE
                    Log.d(TAG, "Downstream Bundle");
                    showBundle(data);
                    createNotification(ACTION_DEBUG, context);
                    if (msg_body != null)
                    {
                        switch (msg_body)
                        {

                            case SHOW_DB_MEMBERS:
                                databaseHandler.getAllMembers();
                                break;
                            case SHOW_DB_MSGS:
                                databaseHandler.getAllMessages();
                                break;
                            default:
                                Log.d(TAG, "Normal message");
                                break;
                        }
                    }
                    break;
                }
                default:
                    Log.e(TAG, "Action Not Supported");
            }
        else
            Log.e(TAG, "ACTION is null");


        MainActivity.updateActivity(data);
    }

    public void processUpstreamMessage(Bundle data, Activity activity)
    {
        GCMClientManager gcmClientManager = new GCMClientManager(activity);
        data = formatMessage(data);
/*
        //DEBUG_CODE
        if (Config.DEBUG_FLAG)
        {
            Log.d(TAG, "Upstream Bundle");
            data.putString(ACTION, ACTION_DEBUG);
            showBundle(data);
        }*/
        //Changes to 'data' have random chances to get applied for gcm send
        gcmClientManager.sendMessage(data);
    }

    private void createNotification(String message, Context context)
    {
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
        notification.setContentText(message).setContentTitle("Nick").setSmallIcon(R.mipmap.bpit);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification.build());
    }

    private Bundle formatMessage(Bundle data)
    {
        String timestamp = getDateTime();
        Log.i(TAG, "Timestamp is " + timestamp);
        data.putString(TIMESTAMP, timestamp);
        return data;
    }

    private String getDateTime()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH);
        return dateFormat.format(new Date());
    }

    private void showBundle(Bundle data)
    {
        for (String key : data.keySet())
            Log.d(TAG, key + " = " + data.getString(key));
    }
}
