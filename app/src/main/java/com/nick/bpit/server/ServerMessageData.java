package com.nick.bpit.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ServerMessageData
{

    public static List<Message> ITEMS = new ArrayList<>();

    public static Map<String, Message> ITEM_MAP = new HashMap<>();

    
    public static void addItem(Message item)
    {
        ITEMS.add(item);
        ITEM_MAP.put(item.timestamp, item);
    }

    public static class Message
    {
        private String timestamp;
        private String body;

        public String getBody()
        {
            return body;
        }

        public void setBody(String body)
        {
            this.body = body;
        }

        public String getTimestamp()
        {
            return timestamp;
        }

        public void setTimestamp(String timestamp)
        {
            this.timestamp = timestamp;
        }

        public Message(String timestamp, String body)
        {
            this.timestamp = timestamp;
            this.body = body;
        }
        
        @Override
        public String toString()
        {
            return body;
        }
    }
}
