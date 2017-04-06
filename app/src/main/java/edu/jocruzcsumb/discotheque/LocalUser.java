package edu.jocruzcsumb.discotheque;

import static junit.framework.Assert.fail;

// For a user who has logged in on this local device
public class LocalUser extends User
{
	//Singleton CurrentUser
	public static LocalUser currentUser = null;

	public static void setCurrentUser(LocalUser user)
	{
		if(user.loginType == null || user.token == null)
			fail("setCurrentUser requires a user with a LoginType and token");
		currentUser = user;
	}

	public static LocalUser getCurrentUser()
	{
		if(currentUser == null)
			fail("You may not call getCuntUser if no user has been logged in");
		return currentUser;
	}


	public enum LoginType
	{
		GOOGLE,
		FACEBOOK,
		SOUNDCLOUD
	}

	public static final String GOOGLE_TOKEN_KEY = "google_t";
	public static final String FACEBOOK_TOKEN_KEY = "FB_t";
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
