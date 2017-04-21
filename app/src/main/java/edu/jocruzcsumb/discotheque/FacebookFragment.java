package edu.jocruzcsumb.discotheque;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class FacebookFragment extends Fragment
{
    private static final int TOAST_DURATION = Toast.LENGTH_SHORT;
    private static final String TAG = "Facebook API";
    private final Context context = getApplicationContext();
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private FacebookCallback<LoginResult> loginCallback = new FacebookCallback<LoginResult>()
    {
        //private FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onCancel()
        {
            Toast.makeText(context, "Login Canceled", TOAST_DURATION)
                 .show();
            Log.d(TAG, "Login Canceled");
        }

        @Override
        public void onError(FacebookException error)
        {
            Toast.makeText(context, "Login Error", TOAST_DURATION)
                 .show();
            Log.d(TAG, "Login Error");
        }

        @Override
        public void onSuccess(LoginResult result)
        {
            Toast.makeText(context, "Login Success", TOAST_DURATION)
                 .show();
            Log.d(TAG, "Login Success");
            AccessToken token = result.getAccessToken();

            if (!LocalUser.login(FacebookFragment.this.getActivity(), LocalUser.LoginType.FACEBOOK, token.getToken()))
            {
                Toast.makeText(context, R.string.dtk_server_login_error, Toast.LENGTH_LONG)
                     .show();
            }
        }

    };

//    private FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
//        @Override
//        public void onCancel() {
//            Toast.makeText(context, "Share Canceled", TOAST_DURATION).show();
//            Log.d(TAG, "Share Canceled");
//        }
//
//        @Override
//        public void onError(FacebookException error) {
//            Toast.makeText(context,"Share Error",TOAST_DURATION).show();
//            Log.d(TAG, "Share Error");
//        }
//
//        @Override
//        public void onSuccess(Sharer.Result result) {
//            Toast.makeText(context,"Share Success",TOAST_DURATION).show();
//            Log.d(TAG, "Share Success");
//        }
//
//    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Callback registration
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance()
                    .registerCallback(callbackManager, loginCallback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_facebook, parent, false);
        final List<String> permissions = new ArrayList<String>();
        permissions.add("public_profile");
        permissions.add("email");
        Button facebookButton = (Button) v.findViewById(R.id.button_facebook);
        facebookButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                LoginManager.getInstance()
                            .logInWithReadPermissions(FacebookFragment.this, permissions);
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
