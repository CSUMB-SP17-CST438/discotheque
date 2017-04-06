package edu.jocruzcsumb.discotheque;

import android.app.Fragment;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.app.FragmentManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static junit.framework.Assert.fail;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks
{

    //google sign in vars
    private GoogleApiClient googleApiClient;
    private static final int REQ_CODE = 9001;
    private SignInButton SignIn;

	private static final String TAG = "MainActivity";
	private static final String GOOGLE_AUTH_TAG = "Google auth";

    //setting listeners

    ArrayList<Button> buttons = new ArrayList<>();
    JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //facebook login fragment code
        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null)
        {
            fragment = new FacebookFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }

        //Google sign in set up
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN.DEFAULT_SIGN_IN)
				.requestEmail()
				.requestServerAuthCode("411633551801-iivlfqvn0mpo3iarr71dn25b15lslr5r.apps.googleusercontent.com")
				.build();

        googleApiClient = new GoogleApiClient.Builder(this)
				.enableAutoManage(this, this)
				.addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
				.build();


		//google sign in button
		SignIn = (SignInButton)findViewById( R.id.google_login_btn);
		SignIn.setOnClickListener(this);


        // Insert a button ID into this array to give it a click listener and add it to the buttons ArrayList
        int[] ids = new int[]{R.id.guest_login_btn};

        for(int id:ids){
            Button b = (Button) findViewById(id);
            buttons.add(b);
            b.setOnClickListener(this);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.guest_login_btn:
                //go to activity
                Intent guestLogin = new Intent(MainActivity.this, JoinRoomActivity.class);
                startActivity(guestLogin);

                break;
            case R.id.google_login_btn:
                googleSignIn();
                break;


        }
    }
    private void googleSignIn(){
        Log.d(GOOGLE_AUTH_TAG, "googleSignIn");
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,REQ_CODE);
    }
    private void googleSignOut(){
		Log.d(GOOGLE_AUTH_TAG, "googleSignOut");
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
				Log.d(GOOGLE_AUTH_TAG, "googleSignOut onResult: " + status.getStatusMessage());
            }
        });
    }

    //Below are for google sign in/out
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult){
		Log.d(GOOGLE_AUTH_TAG, "onConnectionFailed");
    }

    private void HandleResult(GoogleSignInResult result)
	{
		Log.d(GOOGLE_AUTH_TAG, "onHandleResult");
		if(result.isSuccess())
		{

			Log.d(GOOGLE_AUTH_TAG, "result.isSuccess");
            GoogleSignInAccount account = result.getSignInAccount();
            String name = account.getDisplayName();
            String email = account.getEmail();
            String img_url = account.getPhotoUrl().toString();
            Sockets.login(LocalUser.LoginType.GOOGLE, account.getIdToken());
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
        super.onActivityResult(requestCode,resultCode,data);
		Log.d(TAG, "onActivityResult");
        if(requestCode == REQ_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
			Log.d(TAG, String.valueOf(result.getStatus().getStatusCode()));
			if(result == null)
			{
				Log.d(TAG, "result was null you cunt ass bitch");
			}
			else
			{
				if(result.getSignInAccount() == null)
				{
					fail("result.getSignInAccount was null");
				}
				Log.d(TAG, "got result");
				Log.d(TAG, result.getSignInAccount().getDisplayName());
				HandleResult(result);
			}
        }

    }


	@Override
	public void onConnected(@Nullable Bundle bundle)
	{

	}

	@Override
	public void onConnectionSuspended(int i)
	{

	}
}