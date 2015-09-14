package com.nick.bpit.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.nick.bpit.R;

public class GcmMessageHandler extends GcmListenerService
{
    public static final int NOTIFICATION_ID = 1000;
    private final String TAG = "Message Handler";
    @Override
    public void onMessageReceived(String from, Bundle data)
    {
        super.onMessageReceived(from, data);
        String message = data.getString("SERVER_MESSAGE");
        Log.i(TAG, "Message - "+message);
        createNotification(from, message);
    }

    private void createNotification(String from, String message)
    {
        Context context = getBaseContext();
        Notification notification = new Notification.Builder(context)
                                               .setContentText(message).setContentTitle(from)
                                               .setSmallIcon(R.mipmap.bpit).build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
