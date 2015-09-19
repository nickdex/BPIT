package com.nick.bpit.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.nick.bpit.handler.MessageProcessor;
import com.nick.bpit.server.Config;
import com.nick.bpit.R;

public class GCMMessageReceiver extends GcmListenerService implements Config
{

    private final String TAG = "GCM Message Handler";


    @Override
    public void onMessageReceived(String from, Bundle data)
    {
        super.onMessageReceived(from, data);
        MessageProcessor processor = MessageProcessor.getInstance();
        processor.processDownstreamMessage(data, getApplicationContext());
    }


}