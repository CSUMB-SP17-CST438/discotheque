package edu.jocruzcsumb.discotheque;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

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
				Log.d(TAG, "Got Song List");
				checkSongs();

				//		for(Song s:songs)
				//        {
				//            Log.d(TAG, "Song: " + s.getName() + " - " + s.getArtist());
				//            Log.d(TAG, "Url: " + s.getUrl());
				//        }
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
		Log.d(TAG, "CHECK SONGS");
		if (lock)
		{
			Log.w(TAG, "couldnt checkSongs because lock");
			return;
		}

		// Check to see that there are songs in the list
		if (songs.size() <= 0)
		{
			Log.e(TAG, "Song list was empty");
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
			Log.e(TAG, "Start time was invalid");
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

			Log.d(TAG, "CURRENT TIME: " + String.valueOf(localtime));
			Log.d(TAG, "SONG START TIME: " + String.valueOf(s[current].getStartTime()));

			needSeek = localtime > cur.getStartTime();
			if (m[current] != null && m[current].isPlaying())
			{
				m[current].stop();
			}
			if (needSeek)
			{
				Log.d(TAG, "seek to song");
				m[current].seekTo(1000 * (int) ((System.currentTimeMillis() / 1000) - s[current].getStartTime()));
			}
			else // NOT need seek
			{
				Log.d(TAG, "wait for song to start");
				//FIRST try to wait for the time the song is supposed to start
				try
				{
					localtime = System.currentTimeMillis() / 1000;
					Thread.sleep(s[current].getStartTime() - localtime);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
					Log.e(TAG, "Thread.sleep InterruptedException");
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
			Log.d(TAG, "NOT START");
			if (cur == s[next])
			{
				Log.d(TAG, "SCHEDULE NEXT SONG");
				// time to schedule the next song
				swap();
				s[current] = cur;
				if(m[current] == null || m[current].isPlaying())
				{
					// We are interrupting the current song
					Log.d(TAG, "INTERRUPT CURRENT SONG");
					prepareCurrent();
				}
				localtime = System.currentTimeMillis() / 1000;
				if(cur.getStartTime() > localtime)
				{
					// The song will start soon, wait
					Log.d(TAG, "WAIT FOR NEXT SONG");
					localtime = System.currentTimeMillis() / 1000;
					try
					{
						Thread.sleep(s[current].getStartTime() - localtime);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
						Log.e(TAG, "Thread.sleep InterruptedException");
						lock = false;
						return;
					}
				}
				else
				{
					// The song has already started, we need to seek
					Log.d(TAG, "SEEK TO NEXT SONG");
					m[current].seekTo(1000 * (int) ((System.currentTimeMillis() / 1000) - s[current].getStartTime()));
				}
				if (!startCurrent())
				{
					Log.e(TAG, "startCurrent returned false");
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
				Log.d(TAG, "WUT");
				printSong(current);
				printSong(next);
				Log.d(TAG, "CUR IS " + cur.getName());
			}
		}
		lock = false;
		return;



////////////////////////////////////////////////////////

//		if (!start && cur == s[next])
//		{// The service has told us that the next song is about to play.
//			swap();
//			if (cur.getStartTime() > localtime)
//			{
//				if (!m[current].isPlaying())
//				{
//					//this is a weird case, the next song is scheduled
//					// ...but the current song is not playing
//					prepareCurrent();
//					localtime = System.currentTimeMillis() / 1000;
//					try
//					{
//						Thread.sleep(s[current].getStartTime() - localtime);
//					}
//					catch (InterruptedException e)
//					{
//						e.printStackTrace();
//						Log.e(TAG, "Thread.sleep InterruptedException");
//						return;
//					}
//					if (!startCurrent())
//					{
//						Log.e(TAG, "startCurrent returned false");
//						lock = false;
//						return;
//					}
//				}
//				// else: we do not interrupt the songs as they are scheduled correctly.
//				lock = false;
//				return;
//			}
//			else
//			{
//				Log.e(TAG, "WARNING COULD NOT SYNC");
//				lock = false;
//				return;
//			}
//		}
//		else
//		{
//			if (start)
//			{
//				// There is no song currently playing
//				// Here we start playing the very first song
//				s[current] = songs.get(0);
//				prepareCurrent();
//				localtime = System.currentTimeMillis() / 1000;
//
//				Log.d(TAG, "CURRENT TIME: " + String.valueOf(localtime));
//				Log.d(TAG, "SONG START TIME: " + String.valueOf(s[current].getStartTime()));
//
//				needSeek = localtime > cur.getStartTime();
//				if (m[current] != null && m[current].isPlaying())
//				{
//					m[current].stop();
//				}
//				if (needSeek)
//				{
//					Log.d(TAG, "seek to song");
//					m[current].seekTo(1000 * (int) ((System.currentTimeMillis() / 1000) - s[current].getStartTime()));
//					if (!startCurrent())
//					{
//						lock = false;
//						return;
//					}
//				}
//				else // NOT need seek
//				{
//					Log.d(TAG, "wait for song to start");
//					//FIRST try to wait for the time the song is supposed to start
//					try
//					{
//						localtime = System.currentTimeMillis() / 1000;
//						Thread.sleep(s[current].getStartTime() - localtime);
//					}
//					catch (InterruptedException e)
//					{
//						e.printStackTrace();
//						Log.e(TAG, "Thread.sleep InterruptedException");
//						lock = false;
//						return;
//					}
//					//We waited, now start the song
//					if (!startCurrent())
//					{
//						lock = false;
//						return;
//					}
//				}
//			}
//			if ((m[next] == null || !m[next].isPlaying()) && songs.size() > 1 && songs.get(1) != s[next])
//			{
//				s[next] = songs.get(1);
//				prepareNext();
//			}
//		}
	}

	private void printSong(int i)
    {
        //print current song
        Log.i(TAG, "Title: " + s[i].getName());
        Log.i(TAG, "Artist: " + s[i].getArtist());
        Log.i(TAG, "Start time: " + s[i].getStartTime());
        Log.i(TAG, "URL: " + s[i].getUrl());
    }

	private boolean startCurrent()
	{
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
			Log.e(TAG, "Could not start current song");
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
		Log.e(TAG, "onError: (" + String.valueOf(i) + ", " + String.valueOf(i1) + ")");
		return false;
	}

	public void stop()
	{
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
