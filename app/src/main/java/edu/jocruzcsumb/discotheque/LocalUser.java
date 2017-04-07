package edu.jocruzcsumb.discotheque;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import static junit.framework.Assert.fail;

// For a user who has logged in on this local device
public class LocalUser extends User
{
	//Singleton CurrentUser
	private static LocalUser currentUser = null;
	private static SharedPreferences preferences = null;


	private static final String GOOGLE_KEY = "google";
	private static final String FACEBOOK_KEY = "fb";
	private static final String SOUNDCLOUD_KEY = "soundcloud";

	private static final String AUTH_TYPE_KEY = "auth_type";
	private static final String AUTH_TOKEN_KEY = "auth_token";

    //Should only be called after server vefifies the user
	private static void setCurrentUser(LocalUser user)
	{
		if(user.loginType == null || user.token == null)
			fail("setCurrentUser requires a user with a LoginType and token");
		String x = null;
		switch (user.getLoginType())
		{
			case GOOGLE:
				x = GOOGLE_KEY;
				break;
			case FACEBOOK:
				x = FACEBOOK_KEY;
				break;
			case SOUNDCLOUD:
				x = SOUNDCLOUD_KEY;
				break;
		}
		if(x == null)
			fail("setCurrentUser requires a user with a LoginType and token");
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(AUTH_TYPE_KEY, x);
		editor.putString(AUTH_TOKEN_KEY, user.token);
        editor.apply();
        editor.commit();
		currentUser = user;
	}

	private static void signIn(Activity context)
	{
		Intent k = new Intent(context, JoinRoomActivity.class);
		k.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(k);
		context.finish();
	}

	private static LoginType parseLoginType(String l)
    {
        switch (l)
        {
            case GOOGLE_KEY: return LoginType.GOOGLE;
            case FACEBOOK_KEY: return LoginType.FACEBOOK;
            case SOUNDCLOUD_KEY: return LoginType.SOUNDCLOUD;
            default: return null;
        }
    }

    //Checks for leftover auth to log in to discotheque server
    public static boolean silentlogin(Activity context)
	{
		Log.d("Dtk Server", "Silent Login");
        initPrefs(context);
		String t = preferences.getString(AUTH_TYPE_KEY, null);
		if(t == null) return false;
        LoginType type = parseLoginType(t);
        String token = preferences.getString(AUTH_TOKEN_KEY, null);
       if( !(type == null || token == null) && socketLogin(type, token))
	   {
		   signIn(context);
		   return true;
	   }
	   return false;
    }

    //Logs in to discotheque server
    public static boolean login(Activity context, LoginType loginType, String token)
    {
        initPrefs(context);
        if(socketLogin(loginType, token))
		{
			signIn(context);
			return true;
		}
		return false;
    }

    //Logs out of discotheque server and allows user to choose new login infos at MainActivity
	public static void logout(Activity context)
	{
		//TODO: should probably clear entire activity stack
		Intent k = new Intent(context, MainActivity.class);
		k.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(k);
		context.finish();
	}

	public static LocalUser getCurrentUser()
	{
		if(currentUser == null)
			fail("You may not call getCuntUser if no user has been logged in");
		return currentUser;
	}

	private static void initPrefs(Context context)
    {
        preferences = context.getSharedPreferences(context.getString(R.string.shared_prefs_file), Context.MODE_PRIVATE);
    }

	public enum LoginType
	{
		GOOGLE,
		FACEBOOK,
		SOUNDCLOUD
	}

    // This is a long operation
    // Returns true only if setCurrentUser was called
    private static boolean socketLogin(LoginType loginType, String token)
    {
        //We will emit 'login' and wait for 'login status'
        Sockets.SocketWaiter loginWaiter = new Sockets.SocketWaiter("login","login status");
        JSONObject obj = new JSONObject();
        try
        {
            obj.put(LocalUser.getTokenJSONKey(loginType), token);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            return false;
        }
        // This line of code will send the login message
        // and wait until it recieves login status back
        obj = loginWaiter.getObj(obj);

        if(obj == null){
            Log.d("DTK-Server Login", "login returned null (timeout or other error)");
            return false;
        } else
        {
            int a = 0;
            String
                    username = null,
                    firstname = null,
                    lastname = null,
                    email = null,
                    photo = null,
                    bio = null;
            try
            {
                a = obj.getInt("authorized");
                if(a == 1)
                {
                    //TODO: get user info from JSON
                    //				username = obj.getString("username");
                    //				firstname = obj.getString("firstname");
                    //				lastname = obj.getString("lastname");
                    //				email = obj.getString("email");
                    //				photo = obj.getString("photo");
                    //				bio = obj.getString("bio");

                    LocalUser u = new LocalUser(loginType, token, username, firstname, lastname, email, photo, bio);
                    LocalUser.setCurrentUser(u);
                    return true;
                }
                else
                {
                    return false;
                }
            }
            catch(JSONException e)
            {
                e.printStackTrace();
                return false;
            }
        }
    }

	public static final String GOOGLE_TOKEN_KEY = "google_t";
	public static final String FACEBOOK_TOKEN_KEY = "fb_t";
	public static final String SOUNDCLOUD_TOKEN_KEY = "soundcloud_t";

	public static String getTokenJSONKey(LoginType loginType)
	{
		switch(loginType)
		{
			case GOOGLE:
				return GOOGLE_TOKEN_KEY;
			case FACEBOOK:
				return FACEBOOK_TOKEN_KEY;
			case SOUNDCLOUD:
				return SOUNDCLOUD_TOKEN_KEY;
			default:
				return null;
		}
	}

	private LoginType loginType = null;
	private String token = null;
	public LocalUser(LoginType loginType, String token, String userName, String firstName, String lastName, String email, String photo, String bio)
	{
		super(userName, firstName, lastName, email, photo, bio);
		this.loginType = loginType;
		this.token = token;
	}
	public void setLoginType(LoginType loginType)
	{
		this.loginType=loginType;
	}
	public LoginType getLoginType()
	{
		return this.loginType;
	}
}
