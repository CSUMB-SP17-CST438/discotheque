package edu.jocruzcsumb.discotheque;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;

import static edu.jocruzcsumb.discotheque.FloorService.EVENT_FLOOR_JOINED;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_SONG_LIST_UPDATE;

/**
 * Created by carsen on 4/9/17.
 */

public class SeamlessMediaPlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener
{
	public static final String EVENT_SONG_STARTED = "song started";
	public static final String EVENT_SONG_STOPPED = "song stopped";
	private static final String TAG = "SeamlessMediaPlayer";
	private final FloorListener l;
	private MediaPlayer[] m = new MediaPlayer[2];
	private Song[] s = new Song[2];
	private int current = 0;
	private int next = 1;
	private boolean lock = false;
	private Context context;
	private ArrayList<Song> songs = null;

	public SeamlessMediaPlayer(Context context)
	{
		this.context = context;
		this.songs = new ArrayList<Song>();

		// Recieve song events
		IntentFilter f = new IntentFilter();
		f.addAction(EVENT_SONG_LIST_UPDATE);
		f.addAction(EVENT_FLOOR_JOINED);
		l = new FloorListener(f, context, TAG)
		{
			@Override
			public void onSongListUpdate(ArrayList<Song> songs)
			{
				SeamlessMediaPlayer.this.songs = songs;
				checkSongs();
			}
		};
	}

	private void swap()
	{
		int t = current;
		current = next;
		next = t;
	}

	private void checkSongs()
	{
		Log.i(TAG, "checkSongs");
		if (lock)
		{
			Log.w(TAG, "checkSongs: lock was enabled");
			return;
		}

		// Check to see that there are songs in the list
		if (songs.size() <= 0)
		{
			Log.wtf(TAG, "Song list was empty");
			lock = false;
			return;
		}

		Song cur = songs.get(0);
		boolean start = s[current] == null;
		long localtime = System.currentTimeMillis() / 1000;
		boolean needSeek = localtime > cur.getStartTime();

		// Check to see that the start time is valid
		if (cur.getStartTime() == 0)
		{
			Log.wtf(TAG, "Start time was invalid");
			lock = false;
			return;
		}

		if (start)
		{
			// There is no song currently playing
			// Here we start playing the very first song
			s[current] = songs.get(0);
			prepareCurrent();
			localtime = System.currentTimeMillis() / 1000;

//			Log.i(TAG, "CURRENT TIME: " + String.valueOf(localtime));
//			Log.i(TAG, "SONG START TIME: " + String.valueOf(s[current].getStartTime()));

			needSeek = localtime > cur.getStartTime();
			if (m[current] != null && m[current].isPlaying())
			{
				m[current].stop();
			}
			if (needSeek)
			{
				Log.i(TAG, "seek to song");
				m[current].seekTo(1000 * (int) ((System.currentTimeMillis() / 1000) - s[current].getStartTime()));
			}
			else // NOT need seek
			{
				Log.i(TAG, "wait for song to start");
				//FIRST try to wait for the time the song is supposed to start
				try
				{
					localtime = System.currentTimeMillis() / 1000;
					Thread.sleep(s[current].getStartTime() - localtime);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
					Log.wtf(TAG, "Thread.sleep InterruptedException");
					lock = false;
					return;
				}
				//We waited, now start the song
			}
			if (!startCurrent())
			{
				lock = false;
				return;
			}
			s[next] = songs.get(1);
			prepareNext();
		}
		else //not start
		{
			if (cur.equals(s[next]))
			{
				Log.i(TAG, "Schedule next song");
				// time to schedule the next song
				swap();
				s[current] = cur;
				if (m[current] == null || m[current].isPlaying())
				{
					// We are interrupting the current song
					Log.i(TAG, "Interrupt current song");
					prepareCurrent();
				}
				localtime = System.currentTimeMillis() / 1000;
				if (cur.getStartTime() > localtime)
				{
					// The song will start soon, wait
					Log.i(TAG, "Wait for next song to start");
					localtime = System.currentTimeMillis() / 1000;
					try
					{
						Thread.sleep(s[current].getStartTime() - localtime);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
						Log.wtf(TAG, "Thread.sleep InterruptedException");
						lock = false;
						return;
					}
				}
				else
				{
					// The song has already started, we need to seek
					Log.w(TAG, "Fell way behind, seek to next song");
					m[current].seekTo(1000 * (int) ((System.currentTimeMillis() / 1000) - s[current].getStartTime()));
				}
				if (!startCurrent())
				{
					Log.wtf(TAG, "startCurrent returned false");
					lock = false;
					return;
				}
				s[next] = songs.get(1);
				prepareNext();
				lock = false;
				return;
			}
			else
			{
				Log.i(TAG, "Current:");
				printSong(current);
				Log.i(TAG, "Next:");
				printSong(next);
				Log.i(TAG, "Server's current:");
				printSong(cur);
				lock = false;
				return;
			}
		}
		lock = false;
		return;
	}

	private void printSong(int i)
	{
		printSong(s[i]);
	}

	private void printSong(Song s)
	{
		Log.i(TAG, "Title: " + s.getName());
		Log.i(TAG, "Artist: " + s.getArtist());
		Log.i(TAG, "Start time: " + s.getStartTime());
		Log.i(TAG, "URL: " + s.getUrl());
	}

	private boolean startCurrent()
	{
		Log.i(TAG, "startCurrent");
		printSong(current);
		try
		{
			m[current].start();
			m[current].setOnCompletionListener(this);
			m[current].setOnErrorListener(this);
			Intent k = new Intent(EVENT_SONG_STARTED);
			k.putExtra(EVENT_SONG_STARTED, s[current]);
			LocalBroadcastManager.getInstance(context)
								 .sendBroadcast(k);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.w(TAG, "Could not start current song");
			return false;
		}
		return true;
	}

	private boolean prepareCurrent()
	{
		return prep(current);
	}

	private boolean prepareNext()
	{
		return prep(next);
	}

	private boolean prep(int i)
	{
		Log.i(TAG, "prep");
		printSong(i);
		try
		{
			m[i] = new MediaPlayer();
			m[i].setAudioStreamType(AudioManager.STREAM_MUSIC);
			m[i].setDataSource(s[i].getUrl());
			m[i].setLooping(false);
			m[i].prepare();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.e(TAG, "Could not prepare song");
			return false;
		}
		return true;
	}


	@Override
	public void onCompletion(MediaPlayer mediaPlayer)
	{
		Log.i(TAG, "onCompletion");

		// Swap the mediaplayers
//		swap();
//		// Start the new song
//		startCurrent();
//		// Prepare the next song
//		prepareNext();
//
//		// dispose of the player that just finished
//		mediaPlayer.release();
//		songs.remove(s[next]);
//		//checkSongs();
	}

	@Override
	public boolean onError(MediaPlayer mediaPlayer, int i, int i1)
	{
		Log.e(TAG, "onError: " + String.valueOf(i) + ", " + String.valueOf(i1));
		return false;
	}

	public void stop()
	{
		Log.i(TAG, "stop");
		if (m[next] != null && m[next].isPlaying())
		{
			m[next].stop();
		}
		if (m[next] != null && m[current].isPlaying())
		{
			m[current].stop();
		}
		l.stop();
	}
}
