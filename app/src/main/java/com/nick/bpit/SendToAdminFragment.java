package com.nick.bpit;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


public class SendToAdminFragment extends android.support.v4.app.Fragment implements View.OnClickListener
{
    public static final String TAG = "SendToAdminFragment";
    private static final String ARG_SECTION_NUMBER = "section_number";
    ProgressBar progressBar;
    Handler handler;
    int progressStatus;

    private OnFragmentInteractionListener mListener;

    public SendToAdminFragment()
    {
        // Required empty public constructor
    }

    public static SendToAdminFragment newInstance(int sectionNumber)
    {
        SendToAdminFragment fragment = new SendToAdminFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_send_to_admin, container, false);
        (view.findViewById(R.id.send)).setOnClickListener(this);
        return view;
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
        void onFragmentInteraction(String id);
    }
    
}
