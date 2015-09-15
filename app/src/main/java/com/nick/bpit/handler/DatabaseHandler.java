package com.nick.bpit.handler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;

import com.nick.bpit.Config;
import com.nick.bpit.server.ServerMessageData;


public class DatabaseHandler extends SQLiteOpenHelper implements Config
{
    private static final String NAME = "BpitDatabase.db";
    private static final int VERSION = 1;
    private static final String TAG = "Database";
    private SQLiteDatabase database;
    private ContentValues contentValues = new ContentValues();

    public DatabaseHandler(Context context)
    {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table if not exists " + MESSAGE_TABLE + " ( " + MESSAGE_TIME + " INTEGER PRIMARY KEY, " + MESSAGE_BODY + " TEXT);");
        db.execSQL("create table if not exists " + MEMBER_TABLE + " ( " + MEMBER_EMAIL + " TEXT PRIMARY KEY, " + MEMBER_NAME + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("drop table if exists " + MEMBER_TABLE);
        db.execSQL("drop table if exists " + MESSAGE_TABLE);
        onCreate(db);
    }

    public void getAllMessages()
    {
        ServerMessageData.ITEMS.clear();
        ServerMessageData.ITEM_MAP.clear();
        database = getReadableDatabase();
        Cursor cursor = database.query(MESSAGE_TABLE, null, null, null, null, null, null);
        for (int i = 0; i < cursor.getCount(); i++)
        {
            cursor.moveToNext();
            String timestamp = cursor.getString(0);
            String message = cursor.getString(1);
            ServerMessageData.addItem(new ServerMessageData.Message(timestamp, message));
        }
        cursor.close();
    }

    public void insertMessage(Bundle data)
    {
        database = getWritableDatabase();
        String timestamp = data.getString(MESSAGE_TIME);
        String message = data.getString(MESSAGE_BODY);
        contentValues.put(MESSAGE_BODY, message);
        contentValues.put(MESSAGE_TIME, Long.parseLong(timestamp));
        ServerMessageData.addItem(new ServerMessageData.Message(timestamp, message));
        if (database.insert(MESSAGE_TABLE, null, contentValues) != -1)
            Log.i(TAG, "Message inserted successfully");
        else
            Log.i(TAG, "Error in Message insertion");
    }


}
