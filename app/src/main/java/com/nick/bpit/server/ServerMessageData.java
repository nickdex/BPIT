package com.nick.bpit.server;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ServerMessageData implements Config
{
    public final static String TAG = "ServerMessageData";

    public static List<Message> ITEMS = new ArrayList<>();

    public static Map<Long, Message> ITEM_MAP = new HashMap<>();

    public static void addItem(Message item)
    {
        ITEMS.add(item);
        ITEM_MAP.put(item.timestamp, item);
    }

    public static class Message
    {
        private Long timestamp;
        private String body;
        private String email;

        public Message(Bundle data)
        {
            this.email = data.getString(EMAIL);
            this.timestamp = data.getLong(TIMESTAMP);
            this.body = data.getString(MESSAGE_BODY);
        }

        public String getEmail()
        {
            return email;
        }

        public String getBody()
        {
            return body;
        }

        public Long getTimestamp()
        {
            return timestamp;
        }
        
        @Override
        public String toString()
        {
            return body;
        }
    }
}
