package com.nick.bpit.handler;

import android.os.Bundle;
import android.util.Log;

import com.nick.bpit.Config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageHandler implements Config
{
    private static final String TAG = "Message Handler";
    public static Bundle formatMessage(Bundle data)
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = dateFormat.format(new Date());
        Log.i(TAG, "Timestamp is "+timestamp);
        data.putString(MESSAGE_TIME, timestamp);
        return data;
    }



}
