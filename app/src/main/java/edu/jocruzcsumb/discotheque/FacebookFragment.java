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

import java.util.ArrayList;
import java.util.List;

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

			if(LocalUser.login(context, LocalUser.LoginType.FACEBOOK, token.getToken()))
            {
                Intent k = new Intent(FacebookFragment.this.getActivity(), ChatRoomActivity.class);
                startActivity(k);
            }
            else Toast.makeText(context, R.string.dtk_server_login_error, Toast.LENGTH_LONG).show();
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
		LoginManager.getInstance().registerCallback(callbackManager, loginCallback);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.facebook_fragment, parent, false);
		loginButton = (LoginButton) v.findViewById(R.id.loginButton);
		List<String> permissions = new ArrayList<String>();
		permissions.add("public_profile");
		permissions.add("email");
		loginButton.setReadPermissions(permissions);
		loginButton.setFragment(this);
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
