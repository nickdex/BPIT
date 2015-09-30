package com.nick.bpit.handler;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.nick.bpit.R;
import com.nick.bpit.gcm.GCMClientManager;
import com.nick.bpit.server.Config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MessageProcessor implements Config
{
    public static final int NOTIFICATION_ID_MSG = 1337;
    public static final int NOTIFICATION_ID_MEM = 1338;
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
                    String name = data.getString(MEMBER_NAME);
                    String isNewMem = data.getString(NEW_MEMBER);
                    if (isNewMem != null && isNewMem.equals(TRUE))
                        createNotification("New Member", name, context, NOTIFICATION_ID_MEM);
                    formatDownstream(data);
                    databaseHandler.insertMember(data);
                    break;
                }
                case ACTION_BROADCAST:
                {
                    String email = data.getString(EMAIL);
                    if (email != null && email.equals("SERVER"))
                        createNotification(email, msg_body, context, NOTIFICATION_ID_MSG);
                    createNotification(email, msg_body, context, NOTIFICATION_ID_MSG);
                    formatDownstream(data);
                    databaseHandler.insertMessage(data);
                    break;
                }
                case ACTION_DEBUG:
                {
                    //DEBUG_CODE
                    Log.d(TAG, "Downstream Bundle");
                    showBundle(data);
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
    }

    public void processUpstreamMessage(Bundle data, Activity activity)
    {
        GCMClientManager gcmClientManager = new GCMClientManager(activity);
        String action = data.getString(ACTION);
        if (action != null)
        {
            switch (action)
            {
                case ACTION_REFRESH:
                    DatabaseHandler databaseHandler = new DatabaseHandler(activity);
                    //data = databaseHandler.getRefreshMessages();
                    //data.putString(MODE, MESSAGE_TABLE);

                    data = databaseHandler.getRefreshMembers(data);
                    data.putString(MODE, MEMBER_TABLE);

                    Log.i(TAG, "Refresh Bundle");
                    showBundle(data);
                    gcmClientManager.sendMessage(data);
                    break;
                default:
                    data = formatUpstream(data);
                    gcmClientManager.sendMessage(data);
            }
        }
        //Changes to 'data' after sendMessage call have random chances to get applied
    }

    private void createNotification(String title, String body, Context context, int id)
    {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, new Intent("MAIN"), PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
        notification.setContentTitle(title).setContentText(body).setSmallIcon(R.drawable.small_icon).setContentIntent(pendingIntent).setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification.build());
    }

    private Bundle formatUpstream(Bundle data)
    {
        String timestamp = getDateTime();
        Log.i(TAG, "Timestamp is " + timestamp);
        data.putString(TIMESTAMP, timestamp);
        Log.d(TAG, "Upstream Bundle");
        showBundle(data);
        return data;
    }

    private Bundle formatDownstream(Bundle data)
    {
        Log.d(TAG, "Downstream Bundle");
        showBundle(data);
        data.remove("from");
        data.remove("collapse_key");
        data.remove(ACTION);
        data.remove(NEW_MEMBER);
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
