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
        db.execSQL("create table if not exists " + MESSAGE_TABLE + " ( " + EMAIL + " TEXT, " + MESSAGE_BODY + " TEXT, " + TIMESTAMP + " DATETIME PRIMARY KEY " + ");");
        db.execSQL("create table if not exists " + MEMBER_TABLE + " ( " + EMAIL + " TEXT PRIMARY KEY, " + MEMBER_NAME + " TEXT, " + TIMESTAMP + " DATETIME" + ");");
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
        Cursor cursor = database.query(table, null, null, null, null, null, TIMESTAMP);
        //DEBUG_CODE
        if (DEBUG_FLAG)
            DatabaseUtils.dumpCursor(cursor);
        if (cursor != null && cursor.getCount() > 0)
        {
            do
            {
                cursor.moveToNext();
                //Row of Cursor
                for (String col : cursor.getColumnNames())
                    data.putString(col, cursor.getString(cursor.getColumnIndex(col)));

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
            cursor.close();
        }
        database.close();
    }

    public Bundle getRefreshMessages(Bundle data)
    {
        return fillForSync(MESSAGE_TABLE, data);
    }

    public Bundle getRefreshMembers(Bundle data)
    {
        return fillForSync(MEMBER_TABLE, data);
    }

    private Bundle fillForSync(String table, Bundle refreshData)
    {
        database = getReadableDatabase();
        int index = 1;
        Cursor cursor = database.query(table, null, null, null, null, null, TIMESTAMP);
        if (cursor != null && cursor.getCount() > 0)
        {
            do
            {
                cursor.moveToNext();
                switch (table)
                {
                    case MESSAGE_TABLE:
                        refreshData.putString(TIMESTAMP + (index++), cursor.getString(2));
                        break;
                    case MEMBER_TABLE:
                        refreshData.putString(EMAIL + (index++), cursor.getString(0));
                        break;
                }
            } while (!cursor.isLast());
            cursor.close();
        }
        database.close();
        return refreshData;
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

        for (String key : data.keySet())
            contentValues.put(key, data.getString(key));
        try
        {
            if (database.insertOrThrow(table, null, contentValues) != -1)
                Log.i(TAG, table + " inserted successfully");
            switch (table)
            {
                case MESSAGE_TABLE:
                    ServerMessageData.addItem(new ServerMessageData.Message(data));
                    break;
                case MEMBER_TABLE:
                    ServerMemberData.addItem(new ServerMemberData.Member(data));
            }
        }
        catch (SQLiteConstraintException e)
        {
            Log.e(TAG, data.getString(EMAIL) + " already exists");
        }
        catch (SQLiteException e)
        {
            Log.e(TAG, "SQL Operation Failure");
        }
        database.close();
    }
}
