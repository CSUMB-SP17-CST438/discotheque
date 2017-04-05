package edu.jocruzcsumb.discotheque;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.facebook.login.LoginManager;
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

    //facebook sign in
    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;


    //setting listeners

    ArrayList<Button> buttons = new ArrayList<>();
    JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Google sign in set up
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions).build();

        //facebook sign in
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        info = (TextView)findViewById(R.id.info);
        loginButton = (LoginButton)findViewById(R.id.login_button);

        // Insert a button ID into this array to give it a click listener and add it to the buttons ArrayList
        int[] ids = new int[]{R.id.guest_login_btn};
        //google sign in
        SignIn = (SignInButton)findViewById( R.id.google_login_btn);
        SignIn.setOnClickListener(this);

        for(int id:ids){
            Button b = (Button) findViewById(id);
            buttons.add(b);
            b.setOnClickListener(this);
        }

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                info.setText(
                        "User ID: "
                                + loginResult.getAccessToken().getUserId()
                                + "\n" +
                                "Auth Token: "
                                + loginResult.getAccessToken().getToken()
                );
            }

            @Override
            public void onCancel() {

                info.setText("Login canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Login failed.");
            }
        });
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
                signIn();
                break;


        }
    }
    private void signIn(){
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,REQ_CODE);
    }
    private void signOut(){
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Toast.makeText(MainActivity.this, "Logging out", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(MainActivity.this, "Google Login Result: " + name, Toast.LENGTH_SHORT).show();
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
