package com.nick.bpit.server;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ServerMemberData implements Config
{
    public final static String TAG = "ServerMemberData";

    public static LinkedList<Member> ITEMS = new LinkedList<>();

    public static Map<String, Member> ITEM_MAP = new HashMap<>();

    public static void addItem(Member item)
    {
        ITEMS.addFirst(item);
        ITEM_MAP.put(item.email, item);
    }

    public static class Member
    {
        private String timestamp;
        private String name;
        private String email;

        public Member(Bundle data)
        {
            this.email = data.getString(EMAIL);
            this.timestamp = data.getString(TIMESTAMP);
            this.name = data.getString(MEMBER_NAME);

        }

        public String getTimestamp()
        {
            return timestamp;
        }

        public String getName()
        {
            return name;
        }

        public String getEmail()
        {
            return email;
        }

        @Override
        public String toString()
        {
            return name;
        }
    }
}
