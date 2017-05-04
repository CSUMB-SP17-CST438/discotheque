package edu.jocruzcsumb.discotheque;

import java.util.ArrayList;
import java.util.Arrays;

import static edu.jocruzcsumb.discotheque.CompileOptions.BIG_LOGS;
import static edu.jocruzcsumb.discotheque.CompileOptions.DEBUG_MODE;
import static edu.jocruzcsumb.discotheque.CompileOptions.SHOW_LOG_ERRORS;
import static edu.jocruzcsumb.discotheque.CompileOptions.SHOW_LOG_INFO;
import static edu.jocruzcsumb.discotheque.CompileOptions.SHOW_LOG_VERBOSE;
import static edu.jocruzcsumb.discotheque.CompileOptions.SHOW_LOG_WARNINGS;
import static junit.framework.Assert.fail;

/**
 * Created by carsen on 4/27/17.
 */

public class Log
{
	public enum Level
	{
		Verbose,
		Debug,
		Info,
		Warning,
		Error,
		WTF
	}
	private static final String TAG = "Logger";

	// Debug messages should only be used when you are trying to test something
	// DO NOT commit Log.d Messages to the main repo.
	public static void d(String tag, String text)
	{
		if(!DEBUG_MODE)
		{
			wtf(TAG, "Call to Log.d, but DEBUG_MODE is false");
		}
		if(BIG_LOGS)
		{
			String[][] box = new String[][]{
					new String[]{"DEBUG",tag},
					new String[]{"MSG",text},
			};
			text = getBox(box);
			tag = TAG;
		}
		android.util.Log.d(tag, text);
	}

	// Generic information about what the app is doing
	// It's good practice to use this at the top of method calls
	public static void i(String tag, String text)
	{
		if (!SHOW_LOG_INFO)
		{
			return;
		}
		if(BIG_LOGS)
		{
			String[][] box = new String[][]{
					new String[]{"INFO",tag},
					new String[]{"MSG",text},
			};
			text = getBox(box);
			tag = TAG;
		}
		android.util.Log.i(tag, text);
	}

	// Same as i, but for if you have even MORE info to log
	public static void v(String tag, String text)
	{
		if (!SHOW_LOG_VERBOSE)
		{
			return;
		}
		if(BIG_LOGS)
		{
			String[][] box = new String[][]{
					new String[]{"VERBOSE",tag},
					new String[]{"MSG",text},
			};
			text = getBox(box);
			tag = TAG;
		}
		android.util.Log.v(tag, text);
	}

	// Warnings can be unexpected behavior that DON'T break functionality
	public static void w(String tag, String text)
	{
		if (!SHOW_LOG_WARNINGS)
		{
			return;
		}
		if(BIG_LOGS)
		{
			String[][] box = new String[][]{
					new String[]{"WARNING",tag},
					new String[]{"MSG",text},
			};
			text = getBox(box);
			tag = TAG;
		}
		android.util.Log.w(tag, text);
	}

	// Errors can be unexpected behavior that COULD break functionality
	public static void e(String tag, String text)
	{
		if (!SHOW_LOG_ERRORS)
		{
			return;
		}
		if(BIG_LOGS)
		{
			String[][] box = new String[][]{
					new String[]{"ERROR",tag},
					new String[]{"MSG",text},
			};
			text = getBox(box);
			tag = TAG;
		}
		android.util.Log.e(tag, text);
	}

	// What A Terrible Failure messages ( I prefer "What the fuck")
	// This logs a failure that should NEVER happen.
	// THIS WILL CAUSE AN ANR, THE APP WILL STOP
	public static void wtf(String tag, String text)
	{
		if(BIG_LOGS)
		{
			String[][] box = new String[][]{
					new String[]{"WTF",tag},
					new String[]{"MSG",text},
			};
			text = getBox(box);
			tag = TAG;
		}
		android.util.Log.wtf(tag, text);
		fail();
	}

	public static void l(Level logLevel, String tag, String text)
	{
		switch (logLevel)
		{
			case Verbose:
				v(tag, text);
				return;
			case Debug:
				d(tag, text);
				return;
			case Info:
				i(tag, text);
				return;
			case Warning:
				w(tag, text);
				return;
			case Error:
				e(tag, text);
				return;
			case WTF:
				wtf(tag, text);
				return;
			default:
				Log.wtf(TAG, "Unknown log level");
		}
	}

	// Takes a 2D array of strings and makes it into a fancy unicode box
	public static String getBox(String[][] box)
	{
		// First we have to find the size of each item in the box.
		// This is some advanced shit
		int[] rowHeights = new int[box.length], colWidths = new int[box[0].length];
		Arrays.fill(rowHeights, 0);
		Arrays.fill(colWidths, 0);
		int r = 0, c, w, h;
		String[][][] lines = new String[box.length][box[0].length][];
		for(String[] row:box)
		{
			c = 0;
			for(String item:row)
			{
				lines[r][c] = item.split("\n");
				h = lines[r][c].length;
				if(h > rowHeights[r])
				{
					rowHeights[r] = h;
				}
				for(String line:lines[r][c])
				{
					w = line.length();
					if(w > colWidths[c])
					{
						colWidths[c] = w;
					}
				}
				c++;
			}
			r++;
		}

		// Now we can actually make a real string
		StringBuilder bob = new StringBuilder();
		String[] row;
		for(r = 0; r < box.length; r++) //The rows of the box
		{
			row = box[r];
			// Start with the row separator
			boolean first = r == 0;
			bob.append(first?"\n\n╔":"╟");
			for(int i = 0; i < row.length; i++)
			{
				if(i != 0)
				{
					bob.append(first?"╤":"┼");
				}
				for(h = 0; h < colWidths[i]; h++)
				{
					bob.append(first?"═":"─");
				}

			}
			bob.append(first?"╗\n":"╢\n");

			// Print by line
			for(int line = 0; line < rowHeights[r]; line++)
			{
				bob.append("║");
				for(c = 0; c < row.length; c++) // The cols of the box
				{
					if(c != 0)
					{
						bob.append("│");
					}
					if(lines[r][c].length <= line)
					{
						// An empty line for alignment
						for(int ch = 0; ch < colWidths[c]; ch++)
						{
							bob.append(" ");
						}
					}
					else
					{
						bob.append(lines[r][c][line]);
						// Add padding after the text for alignment
						for(int ch = lines[r][c][line].length(); ch < colWidths[c]; ch++)
						{
							bob.append(" ");
						}
					}
				}
				bob.append("║\n");
			}
		}
		bob.append("╚");
		for(int i = 0; i < box[0].length; i++)
		{
			if(i != 0)
			{
				bob.append("╧");
			}
			for(h = 0; h < colWidths[i]; h++)
			{
				bob.append("═");
			}

		}
		bob.append("╝\n");
		return bob.toString();
	}

}
