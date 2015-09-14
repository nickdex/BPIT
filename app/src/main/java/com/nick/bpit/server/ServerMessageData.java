package com.nick.bpit.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ServerMessageData
{

    public static List<Message> ITEMS = new ArrayList<Message>();

    public static Map<String, Message> ITEM_MAP = new HashMap<String, Message>();
    
    static
    {
        // Add 3 sample items.
        addItem(new Message("1", "Bunk"));
        addItem(new Message("2", "Test"));
        addItem(new Message("3", "Lab"));
    }
    
    private static void addItem(Message item)
    {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static class Message
    {
        public String id;
        public String content;
        
        public Message(String id, String content)
        {
            this.id = id;
            this.content = content;
        }
        
        @Override
        public String toString()
        {
            return content;
        }
    }
}
