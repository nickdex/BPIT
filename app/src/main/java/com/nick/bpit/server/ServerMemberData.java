package com.nick.bpit.server;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerMemberData implements Config
{
    public final static String TAG = "ServerMemberData";

    public static List<Member> ITEMS = new ArrayList<>();

    public static Map<String, Member> ITEM_MAP = new HashMap<>();

    public static void addItem(Member item)
    {
        ITEMS.add(item);
        ITEM_MAP.put(item.email, item);
    }

    public static class Member
    {
        private String timestamp;
        private String name;
        private String email;
        private String token;

        public Member(Bundle data)
        {
            this.email = data.getString(EMAIL);
            this.timestamp = data.getString(TIMESTAMP);
            this.name = data.getString(MEMBER_NAME);
            this.token = data.getString(MEMBER_TOKEN);

        }

        public String getToken()
        {
            return token;
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
