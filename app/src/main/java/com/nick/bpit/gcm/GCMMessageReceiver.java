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
import com.nick.bpit.server.Config;
import com.nick.bpit.MainActivity;
import com.nick.bpit.R;

public class GCMMessageReceiver extends GcmListenerService implements Config
{
    public static final int NOTIFICATION_ID = 1000;
    private final String TAG = "GCM Message Handler";


    @Override
    public void onMessageReceived(String from, Bundle data)
    {
        super.onMessageReceived(from, data);
        String message = data.getString(Config.MESSAGE_BODY);
        //code for Announcement handling
        MainActivity.updateActivity(data);

        Log.i(TAG, "Message - " + message);
        createNotification(message);
    }

    private void createNotification(String message)
    {
        Context context = getBaseContext();
        Notification notification = new Notification.Builder(context).setContentText(message).setContentTitle("Nick").setSmallIcon(R.mipmap.bpit).build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}