package com.nick.bpit.gcm;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;
import com.nick.bpit.handler.MessageProcessor;
import com.nick.bpit.server.Config;

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