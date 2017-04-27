package edu.jocruzcsumb.discotheque;

import static edu.jocruzcsumb.discotheque.CompileOptions.*;

/**
 * Created by carsen on 4/27/17.
 */

public class Log
{
	public static void v(String tag, String text)
	{
		if(LOG_VERBOSE) android.util.Log.i(tag, text);
	}

	public static void d(String tag, String text)
	{
		if(LOG_DEBUG) android.util.Log.i(tag, text);
	}

	public static void i(String tag, String text)
	{
		if(LOG_INFO) android.util.Log.i(tag, text);
	}

	public static void w(String tag, String text)
	{
		if(LOG_WARNINGS) android.util.Log.i(tag, text);
	}

	public static void e(String tag, String text)
	{
		if(LOG_ERRORS) android.util.Log.i(tag, text);
	}

	public static void wtf(String tag, String text)
	{
		android.util.Log.wtf(tag, text);
	}
}
