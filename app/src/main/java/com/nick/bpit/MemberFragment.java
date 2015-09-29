package com.nick.bpit;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.nick.bpit.server.ServerMemberData;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class MemberFragment extends android.support.v4.app.ListFragment
{
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static final String TAG = "MemberFragment";
    private OnFragmentInteractionListener mListener;

    public static ArrayAdapter memberAdapter;

    public MemberFragment()
    {
    }
    
    // TODO: Rename and change types of parameters
    public static MemberFragment newInstance(int sectionNumber)
    {
        MemberFragment fragment = new MemberFragment();
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
        memberAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_activated_1, android.R.id.text1, ServerMemberData.ITEMS);
        setListAdapter(memberAdapter);
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
        
        if (null != mListener)
        {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(ServerMemberData.ITEMS.get(position).getEmail());
        }
    }

    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(String id);
    }
    
}
