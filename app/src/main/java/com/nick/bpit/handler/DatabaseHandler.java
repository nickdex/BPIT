package com.nick.bpit.handler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;

import com.nick.bpit.server.Config;
import com.nick.bpit.server.ServerMemberData;
import com.nick.bpit.server.ServerMessageData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class DatabaseHandler extends SQLiteOpenHelper implements Config
{
    private static final String NAME = "BpitDatabase.db";
    private static final int VERSION = 1;
    private static final String TAG = "Database";
    private SQLiteDatabase database;

    public DatabaseHandler(Context context)
    {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table if not exists " + MESSAGE_TABLE + " ( " + EMAIL + " TEXT, " + MESSAGE_BODY + " TEXT, " + TIMESTAMP + " INTEGER PRIMARY KEY " + ");");
        db.execSQL("create table if not exists " + MEMBER_TABLE + " ( " + EMAIL + " TEXT PRIMARY KEY, " + MEMBER_NAME + " TEXT, " + MEMBER_TOKEN + " TEXT, " + TIMESTAMP + " INTEGER" + ");");
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
        populateList(MESSAGE_TABLE);
    }

    public void getAllMembers()
    {
        ServerMemberData.ITEM_MAP.clear();
        ServerMemberData.ITEMS.clear();
        populateList(MEMBER_TABLE);
    }

    private void populateList(String table)
    {
        database = getReadableDatabase();
        Bundle data = new Bundle();
        String order = " DESC";
        Cursor cursor = database.query(table, null, null, null, null, null, TIMESTAMP + order);
        //DEBUG_CODE
        if (DEBUG_FLAG)
            DatabaseUtils.dumpCursor(cursor);
        if (cursor != null && cursor.getCount() > 0)
        {
            do
            {
                cursor.moveToNext();
                for (String col : cursor.getColumnNames())
                    if (!TIMESTAMP.equals(col))
                        data.putString(col, cursor.getString(cursor.getColumnIndex(col)));
                    else
                        data.putLong(col, cursor.getLong(cursor.getColumnIndex(col)));

                switch (table)
                {
                    case MESSAGE_TABLE:
                        ServerMessageData.addItem(new ServerMessageData.Message(data));
                        break;
                    case MEMBER_TABLE:
                        ServerMemberData.addItem(new ServerMemberData.Member(data));
                        break;
                }
                data.clear();
            } while (!cursor.isLast());
        }
        //cursor.close();
    }

    public void insertMessage(Bundle data)
    {
        insert(data, MESSAGE_TABLE);
    }

    public void insertMember(Bundle data)
    {
        insert(data, MEMBER_TABLE);
    }

    private void insert(Bundle data, String table)
    {
        database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        data = formatMessage(data);

        for (String key : data.keySet())
            if (!TIMESTAMP.equals(key))
                contentValues.put(key, data.getString(key));
            else
                contentValues.put(key, data.getLong(key));

        switch (table)
        {
            case MESSAGE_TABLE:
                ServerMessageData.addItem(new ServerMessageData.Message(data));
                break;
            case MEMBER_TABLE:
                ServerMemberData.addItem(new ServerMemberData.Member(data));
        }
        try
        {
            if (database.insertOrThrow(table, null, contentValues) != -1)
                Log.i(TAG, table + " inserted successfully");
        }
        catch (SQLiteConstraintException e)
        {
            Log.e(TAG, data.getString(EMAIL) + " already exists");
        }
        catch (SQLiteException e)
        {
            Log.e(TAG, "SQL Operation Failure");
        }
    }

    private Bundle formatMessage(Bundle data)
    {
        Long timestamp = Long.parseLong(getDateTime());
        Log.i(TAG, "Timestamp is " + timestamp);
        data.putLong(TIMESTAMP, timestamp);
        return data;
    }

    private String getDateTime()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
        return dateFormat.format(new Date());
    }

}
