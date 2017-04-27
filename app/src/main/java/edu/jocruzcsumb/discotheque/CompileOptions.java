package edu.jocruzcsumb.discotheque;

/**
 * Created by carsen on 4/27/17.
 */

public class CompileOptions
{
	// Server setting
	private static final boolean DEBUG_MODE = true;
	private static final int SELECTED_TEST_SERVER = 2;
	// Append to this list if you want to run a different server :D
	private static final String[] SERVERS =
	{
		"http://carsen.ml:8080",
		"http://devev-jcrzry.c9users.io:8080",
		"http://testing-jcrzry.c9users.io:8080",
	};
	private static final String LIVE_SERVER = "https://disco-theque.herokuapp.com";
	public static final String SERVER_URL = DEBUG_MODE ? SERVERS[SELECTED_TEST_SERVER] : LIVE_SERVER;

	// For logging
	public static final boolean LOG_VERBOSE = false;
	public static final boolean LOG_DEBUG = false;
	public static final boolean LOG_INFO = false;
	public static final boolean LOG_WARNINGS = true;
	public static final boolean LOG_ERRORS = true;

	// For socket timeout
	public static final long SOCKET_TIMEOUT = 8L;

}
