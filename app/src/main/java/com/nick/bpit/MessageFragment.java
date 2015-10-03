package com.nick.bpit;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nick.bpit.handler.DatabaseHandler;
import com.nick.bpit.server.ServerMessageData;

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
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        messageAdapter = new ArrayAdapter<>(getActivity(), R.layout.message_list_item, R.id.message_item, ServerMessageData.ITEMS);
        setListAdapter(messageAdapter);

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

    public interface OnFragmentInteractionListener
    {

        void onFragmentInteraction(String id);
    }
    
}
