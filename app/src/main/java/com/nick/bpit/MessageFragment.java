package com.nick.bpit;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.drive.query.Filters;
import com.nick.bpit.handler.DatabaseHandler;
import com.nick.bpit.handler.MessageProcessor;
import com.nick.bpit.server.Config;
import com.nick.bpit.server.ServerMessageData;
import com.nick.bpit.design.ListFragmentSwipeRefreshLayout;

import com.google.android.gms.gcm.GoogleCloudMessaging;


public class MessageFragment extends android.support.v4.app.ListFragment
{
    
    public static final String TAG = "MessageFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static ArrayAdapter messageAdapter;
    private static GoogleCloudMessaging gcm;
    private AsyncTask<Void, Void, String> sendTask;
    private OnFragmentInteractionListener mListener;

    private boolean clicked = true;
    private ListView listView;
    private ListFragmentSwipeRefreshLayout swipeRefreshLayout;

    public MessageFragment()
    {
    }

    public static MessageFragment newInstance(int sectionNumber)
    {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        setListShown(false);
        DatabaseHandler databaseHandler = new DatabaseHandler(getActivity());
        databaseHandler.getAllMessages();
        databaseHandler.getAllMembers();
        databaseHandler.close();
        setListShown(true);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            mListener = (OnFragmentInteractionListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);
        Log.d(TAG, "item click detected");
        RelativeLayout relativeLayout = (RelativeLayout) v;
        TextView time = (TextView) relativeLayout.findViewById(R.id.message_time);
        //TextView message = (TextView)relativeLayout.findViewById(R.id.message_item);
        if (clicked)
        {
            relativeLayout.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
            time.setText(ServerMessageData.ITEMS.get(position).getTimestamp());
            time.setVisibility(View.VISIBLE);
            clicked = false;
        }
        else
        {
            relativeLayout.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, relativeLayout.getMinimumHeight()));
            time.setVisibility(View.GONE);
            clicked = true;
        }

        if (null != mListener)
        {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(ServerMessageData.ITEMS.get(position).getTimestamp());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View listFragmentView = super.onCreateView(inflater, container, savedInstanceState);
        swipeRefreshLayout = new ListFragmentSwipeRefreshLayout(container.getContext());

        swipeRefreshLayout.addView(listFragmentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        swipeRefreshLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        swipeRefreshLayout.setColorSchemeResources(R.color.swipe1, R.color.swipe2, R.color.swipe3, R.color.swipe4);
        return swipeRefreshLayout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        messageAdapter = new ArrayAdapter<>(getActivity(), R.layout.message_list_item, R.id.message_item, ServerMessageData.ITEMS);
        setListAdapter(messageAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                startRefresh();
            }
        });
        swipeRefreshLayout.setListView(getListView());
        super.onViewCreated(view, savedInstanceState);
    }

    public void startRefresh()
    {
        swipeRefreshLayout.setRefreshing(true);
        Bundle refresh_bundle = new Bundle();
        refresh_bundle.putString(Config.ACTION, Config.ACTION_REFRESH);
        MessageProcessor processor = MessageProcessor.getInstance();
        processor.processUpstreamMessage(refresh_bundle, getActivity());
        new AsyncTask<Void, Void, Boolean>()
        {
            @Override
            protected Boolean doInBackground(Void... params)
            {
                int timeToLive = 10;
                MessageProcessor processor = MessageProcessor.getInstance();
                for (int i = 0; i < timeToLive && !processor.actionComplete; i++)
                {
                    try
                    {
                        Thread.sleep(1000);
                        Log.d(TAG, "action in progress");
                    }
                    catch (Exception e)
                    {
                        Log.w(TAG, "Thread sleep error");
                    }
                    if (processor.actionComplete)
                        break;

                }
                return processor.actionComplete;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean)
            {
                super.onPostExecute(aBoolean);
                if (!aBoolean)
                    Toast.makeText(getActivity(), "Server is currently unavailable", Toast.LENGTH_SHORT).show();
                else
                    MessageProcessor.getInstance().actionComplete = false;
                swipeRefreshLayout.setRefreshing(false);
            }
        }.execute();
    }

    public interface OnFragmentInteractionListener
    {

        void onFragmentInteraction(String id);
    }
    

}
