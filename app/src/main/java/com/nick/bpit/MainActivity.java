package com.nick.bpit;

import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.nick.bpit.handler.DatabaseHandler;
import com.nick.bpit.handler.MessageProcessor;
import com.nick.bpit.server.Config;

public class MainActivity extends AppCompatActivity implements ActionBar.TabListener, MessageFragment.OnFragmentInteractionListener, MemberFragment.OnFragmentInteractionListener, SendToAdminFragment.OnFragmentInteractionListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{

    private static final String TAG = "Main Activity";
    static public Context context;
    SectionsPagerAdapter mSectionsPagerAdapter;
    GoogleApiClient googleApiClient;
    ViewPager mViewPager;
    private BroadcastReceiver messageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        googleApiClient = buildGoogleApiClient();
        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
        {
            @Override
            public void onPageSelected(int position)
            {
                if (actionBar != null)
                    actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++)
        {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            if (actionBar != null)
                actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
        }

        messageReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                Log.i(TAG, "New Server Message");
                MessageFragment.messageAdapter.notifyDataSetChanged();
            }
        };

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        databaseHandler.getAllMessages();
        databaseHandler.getAllMembers();
        databaseHandler.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.signOut:
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.refresh:
                doRefresh();
                break;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void doRefresh()
    {
        //TODO Refresh Logic
        Bundle refresh_bundle = new Bundle();
        refresh_bundle.putString(Config.ACTION, Config.ACTION_REFRESH);
        MessageProcessor processor = MessageProcessor.getInstance();
        processor.processUpstreamMessage(refresh_bundle, this);

    }

    @Override
    public void onFragmentInteraction(String Tag)
    {
        switch (Tag)
        {
            case SendToAdminFragment.TAG:

                String message = ((EditText) findViewById(R.id.message)).getText().toString();
                if (!message.equals(""))
                {
                    Bundle data = new Bundle();
                    MessageProcessor processor = MessageProcessor.getInstance();
                    SharedPreferences preferences = getSharedPreferences("OWNER", MODE_PRIVATE);
                    Log.d(TAG, "Message input by user = " + message);
                    data.putString(Config.ACTION, Config.ACTION_BROADCAST);
                    data.putString(Config.EMAIL, preferences.getString(Config.EMAIL, ""));
                    data.putString(Config.MESSAGE_BODY, message);
                    processor.processUpstreamMessage(data, MainActivity.this);
                    Toast.makeText(MainActivity.this, "Message Sent Successfully", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(MainActivity.this, "Can't Send Empty Message", Toast.LENGTH_SHORT).show();
                break;
            default:
                Log.w(TAG, Tag + "Yet to implement");
                break;
        }
    }

    private GoogleApiClient buildGoogleApiClient()
    {
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(Plus.API, Plus.PlusOptions.builder().build()).addScope(Plus.SCOPE_PLUS_LOGIN);
        return builder.build();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        context.registerReceiver(messageReceiver, new IntentFilter("message"));
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        context.unregisterReceiver(messageReceiver);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
    {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onConnected(Bundle bundle)
    {
    }

    @Override
    public void onConnectionSuspended(int i)
    {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
    }


    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
    {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
    {
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            // getItem is called to instantiate the fragment for the given page.
            switch (position)
            {
                case 0:
                    return MemberFragment.newInstance(position + 1);
                case 1:
                    return MessageFragment.newInstance(position + 1);
                case 2:
                    return SendToAdminFragment.newInstance(position + 1);
            }
            return null;
        }

        @Override
        public int getCount()
        {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            Locale l = Locale.getDefault();
            switch (position)
            {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

}
