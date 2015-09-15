package com.nick.bpit;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.nick.bpit.gcm.GCMClientManager;
import com.nick.bpit.server.RegisterUserToServer;

public class LoginActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnClickListener, Config
{


    private GCMClientManager clientManager;
    private GoogleApiClient googleApiClient;
    private boolean mIsResolving = false;
    private boolean mShouldResolve = false;
    private String TAG = "Google Sign In";
    private RegisterUserToServer server;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        server = new RegisterUserToServer();
        googleApiClient = buildGoogleApiClient();
        SignInButton signInButton = (SignInButton) findViewById(R.id.gSignIn);
        signInButton.setStyle(SignInButton.SIZE_WIDE, SignInButton.COLOR_DARK);
        signInButton.setOnClickListener(this);
        clientManager = new GCMClientManager(this, Config.PROJECT_NUMBER);
    }

    void showSignedInUI()
    {
        if (Plus.PeopleApi.getCurrentPerson(googleApiClient) != null)
        {
            Intent intent = new Intent(this, MainActivity.class);
            Bundle bundle = new Bundle();
            Person person = Plus.PeopleApi.getCurrentPerson(googleApiClient);
            String personName = person.getDisplayName();
            final String email = Plus.AccountApi.getAccountName(googleApiClient);

            //String personPhoto = person.getImage().getUrl();
            Log.i(TAG, personName + '%' + email);
            clientManager.registerIfNeeded(new GCMClientManager.RegistrationCompleteHandler()
            {
                @Override
                public void onSuccess(String registrationId, boolean isNewRegistration)
                {
                    new RegisterUserToServer().execute(email, registrationId);
                    //task can only be executed once, add logic for repeated sign out and sign in
                    //if required, send device id to server
                }

                @Override
                public void onFailure(String ex)
                {
                    super.onFailure(ex);
                    Log.i(TAG, "GCM registration failed");
                    //click again or perform back-off when retrying
                }
            });
            bundle.putString("Email", email);
            bundle.putString("Name", personName);
            intent.putExtras(bundle);
            startActivityForResult(intent, SIGN_IN_SUCCESS);
        }
        else
        {
            Toast.makeText(LoginActivity.this, "No User Found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.gSignIn:
                onSignInClicked();
                break;
        }
    }

    private GoogleApiClient buildGoogleApiClient()
    {
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(Plus.API, Plus.PlusOptions.builder().build()).addScope(Plus.SCOPE_PLUS_LOGIN);

        return builder.build();
    }

    private void onSignInClicked()
    {
        mShouldResolve = true;
        googleApiClient.connect();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        //if(googleApiClient.isConnected())
        googleApiClient.connect();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (googleApiClient.isConnected())
            googleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        Log.d(TAG, "onConnectionFailed: " + connectionResult);
        if (!mIsResolving && mShouldResolve)
        {
            if (connectionResult.hasResolution())
            {
                try
                {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                }
                catch (IntentSender.SendIntentException e)
                {
                    Log.e(TAG, "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    googleApiClient.connect();
                }
            }
            else
            {
                showErrorDialog(connectionResult);
            }
        }
    }

    void showErrorDialog(ConnectionResult result)
    {
        int errorCode = result.getErrorCode();
        if (GooglePlayServicesUtil.isUserRecoverableError(errorCode))
        {
            GooglePlayServicesUtil.getErrorDialog(errorCode, this, RC_SIGN_IN, new DialogInterface.OnCancelListener()
            {
                @Override
                public void onCancel(DialogInterface dialog)
                {
                    mShouldResolve = false;
                }
            }).show();
        }
        else
        {
            String errorString = "Google Services Error";
            Toast.makeText(this, errorString, Toast.LENGTH_SHORT).show();
            mShouldResolve = false;
        }
    }

    void showSignedOutUI()
    {
        new AlertDialog.Builder(this).setTitle("Success").setMessage("You have successfully logged out").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        }).setIcon(android.R.drawable.ic_dialog_info).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " + requestCode + ":" + resultCode + ":" + data);
        switch (requestCode)
        {
            case RC_SIGN_IN:
                if (resultCode != RESULT_OK)
                    mShouldResolve = false;
                break;
            case SIGN_IN_SUCCESS:
                if (googleApiClient.isConnected())
                {
                    Plus.AccountApi.clearDefaultAccount(googleApiClient);
                    Plus.AccountApi.revokeAccessAndDisconnect(googleApiClient);
                    googleApiClient = buildGoogleApiClient();
                    googleApiClient.connect();
                    break;
                }
                showSignedOutUI();
        }
        mIsResolving = false;
        googleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        mShouldResolve = false;
        showSignedInUI();
    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

}
