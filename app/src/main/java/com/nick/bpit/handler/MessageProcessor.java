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
    //Singleton, Check whether this will cause conflicts when downstreaming and upstreaming simultaneously
    private static MessageProcessor instance = new MessageProcessor();

    public static MessageProcessor getInstance()
    {
        return instance;
    }

    public void processDownstreamMessage(Bundle data, Context context)
    {
        DatabaseHandler databaseHandler = new DatabaseHandler(context);
        //DEBUG_CODE
        if(DEBUG_FLAG)
        {
            Log.d(TAG, "Downstream Bundle");
            showBundle(data);
        }
        else {
            data.remove("collapse_key");
            data.remove(ACTION);
            databaseHandler.insertMessage(data);
        }


        //DEBUG_CODE
        if (DEBUG_FLAG)
        {
            String key = data.getString(PAYLOAD_MESSAGE);
            if (key != null)
                switch (key)
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

        MainActivity.updateActivity(data);
    }

    public void processUpstreamMessage(Bundle data, Activity activity)
    {
        GCMClientManager gcmClientManager = new GCMClientManager(activity);

        //Changes to data have random chances to get applied for gcm send
        //DEBUG_CODE
        if (Config.DEBUG_FLAG)
        {
            Log.d(TAG, "Upstream Bundle");
            data.putString(ACTION, ACTION_DEBUG);
            showBundle(data);
        }

        gcmClientManager.sendMessage(data);
    }

    private void showBundle(Bundle data)
    {
        for(String key: data.keySet())
            Log.d(TAG, key+" = "+data.getString(key));
    }
}
