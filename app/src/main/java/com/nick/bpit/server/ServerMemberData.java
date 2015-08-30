package com.nick.bpit.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nik on 8/30/2015.
 */
public class ServerMemberData
{
    public static List<Member> ITEMS = new ArrayList<>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, Member> ITEM_MAP = new HashMap<>();

    static
    {
        // Add 3 sample items.
        addItem(new Member("1", "Nick"));
        addItem(new Member("2", "Ekta"));
        addItem(new Member("3", "Neha"));
    }

    private static void addItem(Member item)
    {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class Member
    {
        public String id;
        public String content;

        public Member(String id, String content)
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
