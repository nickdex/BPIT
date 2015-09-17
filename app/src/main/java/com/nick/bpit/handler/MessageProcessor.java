package com.nick.bpit.handler;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.nick.bpit.MainActivity;
import com.nick.bpit.gcm.GCMClientManager;
import com.nick.bpit.server.Config;
import com.nick.bpit.server.ServerMemberData;

public class MessageProcessor implements Config
{
    private static final String TAG = "MessageProcessor";
    private static MessageProcessor instance = new MessageProcessor();

    public static MessageProcessor getInstance()
    {
        return instance;
    }

    public void processDownstreamMessage(Bundle data, Context context)
    {
        DatabaseHandler databaseHandler = new DatabaseHandler(context);
        databaseHandler.insertMessage(data);
        data.remove("collapse_key");
        MainActivity.updateActivity(data);
    }

    public void processUpstreamMessage(Bundle data, Context context)
    {
        DatabaseHandler databaseHandler = new DatabaseHandler(context);
        GCMClientManager gcmClientManager = new GCMClientManager(context);
        gcmClientManager.sendMessage(data);
        String action = data.getString(ACTION);
        if (action != null)
        {
            data.remove(ACTION);
            switch (action)
            {
                case ACTION_REGISTER:
                    databaseHandler.insertMember(data);
                    break;
                case ACTION_BROADCAST:
                    databaseHandler.insertMessage(data);
                    break;
                default:
                    Log.e(TAG, action + "Action unsupported");
                    break;
            }
        }
    }
}
