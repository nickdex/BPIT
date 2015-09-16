package com.nick.bpit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.nick.bpit.server.ServerMessageData;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MessageFragment extends android.support.v4.app.ListFragment
{
    
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static GoogleCloudMessaging gcm;
    public static final String TAG = "MessageFragment";
    private AsyncTask<Void, Void, String> sendTask;
    public static ArrayAdapter messageAdapter;
    private OnFragmentInteractionListener mListener;
    
    public MessageFragment()
    {
    }

    // TODO: Rename and change types of parameters
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

       /* if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        */
        messageAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, ServerMessageData.ITEMS);
        // TODO: Change Adapter to display your content
        setListAdapter(messageAdapter);

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
        if (null != mListener)
        {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(ServerMessageData.ITEMS.get(position).getTimestamp().toString());
        }
    }

    public interface OnFragmentInteractionListener
    {

        void onFragmentInteraction(String id);
    }
    
}
