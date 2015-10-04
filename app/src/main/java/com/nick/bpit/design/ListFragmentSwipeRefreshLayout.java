package com.nick.bpit.design;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;

public class ListFragmentSwipeRefreshLayout extends SwipeRefreshLayout
    {
        ListView listView;

        public void setListView(ListView listView)
        {
            this.listView = listView;
        }

        public ListFragmentSwipeRefreshLayout(Context context)
        {
            super(context);
        }

        @Override
        public boolean canChildScrollUp()
        {
            return listView.getVisibility() == View.VISIBLE && canListViewScrollUp(listView);
        }

        private boolean canListViewScrollUp(ListView listView)
        {
            //logic can be added for lower api level
            return ViewCompat.canScrollVertically(listView, -1);
        }
    }
