package edu.jocruzcsumb.discotheque;


import android.content.Context;
import android.widget.Toast;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.Sharer;

import java.util.Arrays;

import static com.facebook.FacebookSdk.getApplicationContext;

public class FacebookFragment extends Fragment {
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    Context context = getApplicationContext();
    int duration = Toast.LENGTH_SHORT;

    private FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onCancel() {
            Toast toast = Toast.makeText(context, "Login Canceled", duration);
            toast.show();
        }

        @Override
        public void onError(FacebookException error) {
            Toast toast2 = Toast.makeText(context,"Login Error",duration);
            toast2.show();
        }

        @Override
        public void onSuccess(Sharer.Result result) {
            Toast toast3 = Toast.makeText(context,"Login Success",duration);
            toast3.show();
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.facebook_fragment, parent, false);
        loginButton = (LoginButton) v.findViewById(R.id.loginButton);
        // If using in a fragment
        loginButton.setFragment(this);
        callbackManager = CallbackManager.Factory.create();
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast toast = Toast.makeText(context, "Logged In", duration);
                toast.show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast toast2 = Toast.makeText(context,"Login Error",duration);
                toast2.show();
            }

            @Override
            public void onCancel() {
                Toast toast = Toast.makeText(context, "Login Canceled", duration);
                toast.show();
            }

            });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("public_profile"));
            }
        });
        return v;
    }
}
