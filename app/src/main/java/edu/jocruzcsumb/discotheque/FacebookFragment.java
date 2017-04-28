package edu.jocruzcsumb.discotheque;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class FacebookFragment extends Fragment
{
	private static final int TOAST_DURATION = Toast.LENGTH_SHORT;
	private static final String TAG = "Facebook API";
	private final Context context = getApplicationContext();
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
			MainActivity a = (MainActivity) getActivity();
			a.showLoader(false);
		}

		@Override
		public void onError(FacebookException error)
		{
			Toast.makeText(context, "Login Error", TOAST_DURATION)
				 .show();
			Log.d(TAG, "Login Error");
			MainActivity a = (MainActivity) getActivity();
			a.showLoader(false);
		}

		@Override
		public void onSuccess(LoginResult result)
		{
			Toast.makeText(context, "Login Success", TOAST_DURATION)
				 .show();
			Log.d(TAG, "Login Success");
			AccessToken token = result.getAccessToken();

			if (LocalUser.socketLogin(LocalUser.LoginType.FACEBOOK, token.getToken()))
			{
				Log.i(TAG, "Successful facebook login");
				Activity a = FacebookFragment.this.getActivity();
				Intent k = new Intent(a, PickFloorActivity.class);
				k.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(k);
				a.finish();
			}
			else
			{
				Log.e(TAG, "Facebook user signed in but we could not log them into Discotek");
				Toast.makeText(FacebookFragment.this.getActivity(), R.string.error_no_connection_dtk, Toast.LENGTH_LONG)
					 .show();
				LoginManager.getInstance()
							.logOut();
				MainActivity a = (MainActivity) getActivity();
				a.showLoader(false);
			}
		}

	};

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
				MainActivity a = (MainActivity) getActivity();
				a.showLoader(true);
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
