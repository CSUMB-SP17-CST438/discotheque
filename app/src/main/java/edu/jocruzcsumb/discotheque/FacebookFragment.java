package edu.jocruzcsumb.discotheque;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import static com.facebook.FacebookSdk.getApplicationContext;

public class FacebookFragment extends Fragment
{
	private LoginButton loginButton;
	private CallbackManager callbackManager;

	private final Context context = getApplicationContext();

	private static final int TOAST_DURATION = Toast.LENGTH_SHORT;
	private static final String TAG = "Facebook API";

	private FacebookCallback<LoginResult> loginCallback = new FacebookCallback<LoginResult>()
	{
		//private FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
		@Override
		public void onCancel()
		{
			Toast.makeText(context, "Login Canceled", TOAST_DURATION).show();
			Log.d(TAG, "Login Canceled");
		}

		@Override
		public void onError(FacebookException error)
		{
			Toast.makeText(context, "Login Error", TOAST_DURATION).show();
			Log.d(TAG, "Login Error");
		}

		@Override
		public void onSuccess(LoginResult result)
		{
			Toast.makeText(context, "Login Success", TOAST_DURATION).show();
			Log.d(TAG, "Login Success");
			AccessToken token = result.getAccessToken();

			Sockets.login(LocalUser.LoginType.FACEBOOK, token.getToken());
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
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.facebook_fragment, parent, false);
		loginButton = (LoginButton) v.findViewById(R.id.loginButton);
		// If using in a fragment
		loginButton.setFragment(this);
		callbackManager = CallbackManager.Factory.create();
		// Callback registration
		loginButton.registerCallback(callbackManager, loginCallback);

		loginButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("public_profile"));
			}
		});
		return v;
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode, resultCode, data);
	}
}
