package edu.jocruzcsumb.discotheque;

import android.app.Fragment;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {

    //google sign in vars
    private GoogleApiClient googleApiClient;
    private static final int REQ_CODE = 9001;
    private SignInButton SignIn;



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

        if (fragment == null) {
            fragment = new FB_Frag();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }

        //Google sign in set up
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions).build();


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
        Log.d("Discotheque Google Auth", "Sign in attempt");
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,REQ_CODE);
    }
    private void googleSignOut(){
		Log.d("Discotheque Google Auth", "Sign out attempt");
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
				Log.d("Discotheque Google Auth", "Signed out");
            }
        });
    }

    //Below are for google sign in/out
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult){

    }


    private void HandleResult(GoogleSignInResult result){
        if(result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            String name = account.getDisplayName();
            String email = account.getEmail();
            String img_url = account.getPhotoUrl().toString();
			Log.d("Discotheque Google Auth", "Logged in as: " + name);
            Sockets.SocketWaiter waiter = new Sockets.SocketWaiter("login", "login status");

            JSONObject obj = new JSONObject();
            String t = result.getSignInAccount().getIdToken();


			try
			{
				obj.put("google_t", t);
			}
			catch(JSONException e)
			{
				e.printStackTrace();
				return;
			}

			obj = waiter.getObj(obj);

			if(obj == null)
			{
				return;
			}
			else
            {

            }


        }
        else {
            //error messages
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == REQ_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            HandleResult(result);

        }

    }
    //end of google sign in/out code


}