package com.nick.bpit;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class SendToAdminFragment extends android.support.v4.app.Fragment implements View.OnClickListener
{
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static final String TAG = "SendToAdminFragment";
    

    private OnFragmentInteractionListener mListener;

    public static SendToAdminFragment newInstance(int sectionNumber)
    {
        SendToAdminFragment fragment = new SendToAdminFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public SendToAdminFragment()
    {
        // Required empty public constructor
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        /*
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        */
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_send_to_admin, container, false);
        (v.findViewById(R.id.send)).setOnClickListener(this);
        return v;
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
    public void onClick(View v)
    {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(TAG);
        }

    }

    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
         void onFragmentInteraction(String id);
    }
    
}
