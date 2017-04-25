package edu.jocruzcsumb.discotheque;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONArray;

import java.util.ArrayList;

import static junit.framework.Assert.fail;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks
{

	private static final int REQ_CODE = 9001;
	private static final String TAG = "MainActivity";
	private static final String GOOGLE_AUTH_TAG = "Google auth";
	ArrayList<Button> buttons = new ArrayList<>();
	JSONArray jsonArray;

	//setting listeners
	//google sign in vars
	private GoogleApiClient googleApiClient;
	private Button SignIn;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (!Sockets.getSocket()
					.connected())
		{
			Toast.makeText(this, R.string.error_no_connection_dtk, Toast.LENGTH_LONG)
				 .show();
			Log.w(TAG, getString(R.string.error_no_connection_dtk));
			finish();
		}
		setContentView(R.layout.activity_main);
		findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
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

		googleApiClient = getGoogleApiClient();


		if (!getIntent().getBooleanExtra("signout", false))
		{
			//Thread for dtk silent sign in
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					showLoader(true);
					if (LocalUser.silentLogin(MainActivity.this, googleApiClient))
					{
						Intent k = new Intent(MainActivity.this, PickFloorActivity.class);
						k.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(k);
						finish();
					}
					Log.w(TAG, "silent sign in failed");
					showLoader(false);
				}
			}).start();
//			// Attempt silent sign in
//			OptionalPendingResult<GoogleSignInResult> pendingResult =
//					Auth.GoogleSignInApi.silentSignIn(googleApiClient);
//			if (pendingResult.isDone())
//			{
//				Log.d(TAG, "pendingResult.isDone() = true");
//				GoogleSignInResult r = pendingResult.get();
//				if (r.isSuccess())
//				{
//					// There's an immediate result available.
//					MainActivity.this.handleResult(pendingResult.get());
//				}
//				else
//				{
//					t.start();
//				}
//			}
//			else
//			{
//				// There's no immediate result ready
//
//				// We may want to add a progress indicator right here
//				pendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>()
//				{
//					@Override
//					public void onResult(@NonNull GoogleSignInResult result)
//					{
//						Log.d(TAG, "Google Silent login onResult");
//						if (result.isSuccess())
//						{
//							Log.d(TAG, "result.isSuccess()");
//							MainActivity.this.handleResult(result);
//						}
//						else
//						{
//							t.start();
//						}
//					}
//				});
//			}

		}
		else
		{
			LoginManager.getInstance()
						.logOut();
			googleSignOut();
			switch (LocalUser.getCurrentUser()
							 .getLoginType())
			{
				case GOOGLE:
					break;
				case FACEBOOK:
					break;
				case SOUNDCLOUD:
			}
		}

		//Insert a button ID into this array to give it a click listener and add it to the buttons ArrayList
		int[] ids = new int[]{R.id.google_login_btn};
		// Foreach button above
		for (int id : ids)
		{
			Button b = (Button) findViewById(id);
			buttons.add(b);
			b.setOnClickListener(this);
		}
		showLoader(false);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
//			case R.id.guest_login_btn:
//				//go to activity
//				Intent guestLogin = new Intent(MainActivity.this, PickFloorActivity.class);
//				startActivity(guestLogin);
//				break;
			case R.id.google_login_btn:
				showLoader(true);
				googleSignIn();
				break;
		}
	}

	private void googleSignIn()
	{
		Log.d(GOOGLE_AUTH_TAG, "googleSignIn");
		Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
		startActivityForResult(intent, REQ_CODE);
	}

	private void googleSignOut()
	{
		Log.d(GOOGLE_AUTH_TAG, "googleSignOut");
		if (!googleApiClient.isConnected())
		{
			googleApiClient.connect();
			googleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks()
			{
				@Override
				public void onConnected(@Nullable Bundle bundle)
				{
					if (googleApiClient.isConnected())
					{
						Auth.GoogleSignInApi.signOut(googleApiClient)
											.setResultCallback(new ResultCallback<Status>()
											{
												@Override
												public void onResult(@NonNull Status status)
												{
													if (status.isSuccess())
													{
														Log.i(GOOGLE_AUTH_TAG, "User Logged out");
													}
												}
											});
					}
					else
					{
						Log.w(GOOGLE_AUTH_TAG, "signout failed, trying again");
						googleSignOut();
					}
				}

				@Override
				public void onConnectionSuspended(int i)
				{

				}
			});
		}
		else
		{
			doGoogleSignOut();
		}
	}

	private void doGoogleSignOut()
	{
		Auth.GoogleSignInApi.signOut(googleApiClient)
							.setResultCallback(new ResultCallback<Status>()
							{
								@Override
								public void onResult(@NonNull Status status)
								{
									Log.d(GOOGLE_AUTH_TAG, "googleSignOut onResult: " + status.getStatusMessage());
								}
							});
	}

	private GoogleApiClient getGoogleApiClient()
	{

		// Google API setup
		GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN.DEFAULT_SIGN_IN)
				.requestEmail()
				.requestIdToken("411633551801-iivlfqvn0mpo3iarr71dn25b15lslr5r.apps.googleusercontent.com")
				.requestServerAuthCode("411633551801-iivlfqvn0mpo3iarr71dn25b15lslr5r.apps.googleusercontent.com")
				.build();

		// Get the API Client obj
		GoogleApiClient g = new GoogleApiClient.Builder(this)
				.enableAutoManage(this, this)
				.addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
				.build();
		return g;
	}

	//Below are for google sign in/out
	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
	{
		Log.d(GOOGLE_AUTH_TAG, "onConnectionFailed");
	}

	public void handleResult(GoogleSignInResult r)
	{
		Log.d(GOOGLE_AUTH_TAG, "onHandleResult");
		final GoogleSignInResult result = r;
		if (result.isSuccess())
		{
			new Thread(new Runnable()
			{
				public void run()
				{
					Log.d(GOOGLE_AUTH_TAG, "result.isSuccess");

					GoogleSignInAccount account = result.getSignInAccount();
					Log.d(GOOGLE_AUTH_TAG, "Account: " + account.getDisplayName());
					Log.d(GOOGLE_AUTH_TAG, "IdToken: " + account.getIdToken());
					Log.d(GOOGLE_AUTH_TAG, "Id: " + account.getId());
					Log.d(GOOGLE_AUTH_TAG, "GrantedScopes: " + account.getGrantedScopes()
																	  .toString());

					String name = account.getDisplayName();
					String email = account.getEmail();
					String img_url = account.getPhotoUrl()
											.toString();
					if (LocalUser.socketLogin(LocalUser.LoginType.GOOGLE, account.getIdToken()))
					{
						MainActivity.this.runOnUiThread(new Runnable()
						{
							public void run()
							{
								Intent k = new Intent(MainActivity.this, PickFloorActivity.class);
								k.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(k);
								finish();
							}
						});
					}
					else
					{

						Log.e(GOOGLE_AUTH_TAG, "User signed in but we could not log them into Discotek");
						Toast.makeText(MainActivity.this, R.string.error_no_connection_dtk, Toast.LENGTH_LONG)
							 .show();
						googleSignOut();
						showLoader(false);
					}
				}
			}).start();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "onActivityResult");
		if (requestCode == REQ_CODE)
		{
			GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
			Log.d(TAG, String.valueOf(result.getStatus()
											.getStatusCode()));
			if (result == null)
			{
				Log.e(TAG, "result was null you cunt ass bitch");
			}
			else
			{
				if (result.getSignInAccount() == null)
				{
					fail("result.getSignInAccount was null");
				}
				Log.d(TAG, "got result");
				Log.d(TAG, result.getSignInAccount()
								 .getDisplayName());
				handleResult(result);
			}
		}

	}

	public void showLoader(final boolean show)
	{
		MainActivity.this.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				findViewById(R.id.loadingPanel).setVisibility(show ? View.VISIBLE : View.GONE);
			}
		});
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