package edu.jocruzcsumb.discotheque;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import static edu.jocruzcsumb.discotheque.FloorService.EVENT_FLOOR_JOINED;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_GET_SONG_LIST;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_SONG_LIST_UPDATE;

/**
 * Created by carsen on 4/9/17.
 */

public class SeamlessMediaPlayer extends BroadcastReceiver
{
	private static final String TAG = "SeamlessMediaPlayer";
	private MediaPlayer[] m = new MediaPlayer[2];
	private Song[] s = new Song[2];

    public static final String EVENT_SONG_STARTED = "song started";
    public static final String EVENT_SONG_STOPPED = "song stopped";

	private int current = 0;
	private int next = 1;
	private boolean lock = false;

	//TODO when player object is started, we seek to utc.now - utc start time
	private long timeStarted = 0;
	private MediaPlayer.OnCompletionListener[] l = new MediaPlayer.OnCompletionListener[]
			{
					new MediaPlayer.OnCompletionListener()
					{
						@Override
						public void onCompletion(MediaPlayer mp)
						{
							mp.release();
							if(!lock)
							{
								lock = true;
								Log.d(TAG, "onCompletion: start");
								m[1].start();
								Intent k = new Intent(EVENT_SONG_STARTED);
								k.putExtra(EVENT_SONG_STARTED, s[1]);
								LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(k);
								reset(0);
								swap();
								lock = false;
							}
						}
					},
					new MediaPlayer.OnCompletionListener()
					{
						@Override
						public void onCompletion(MediaPlayer mp)
						{
							mp.release();
							if(!lock)
							{
								lock = true;
								Log.d(TAG, "onCompletion: start");
								m[0].start();
								Intent k = new Intent(EVENT_SONG_STARTED);
								k.putExtra(EVENT_SONG_STARTED, s[0]);
								LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(k);
								reset(1);
								swap();
								lock = false;
							}
						}
					}
			};
	private Context context;
	private ArrayList<Song> songs = null;

	public SeamlessMediaPlayer(Context context)
	{
		this.context = context;
		this.songs = new ArrayList<Song>();


		//Set up the media players
		for(int i = 0; i < 2; i++)
		{
			m[i] = new MediaPlayer();
			m[i].setOnCompletionListener(l[i]);
            s[i]=null;
		}

		// Recieve song events
		IntentFilter f = new IntentFilter();
		f.addAction(EVENT_SONG_LIST_UPDATE);
		f.addAction(EVENT_FLOOR_JOINED);
		LocalBroadcastManager.getInstance(context.getApplicationContext())
				.registerReceiver(this, f);
		LocalBroadcastManager b = LocalBroadcastManager.getInstance(context.getApplicationContext());
		b.registerReceiver(this, f);

		// Request the song list
		Intent k = new Intent(EVENT_GET_SONG_LIST);
		b.sendBroadcast(k);
	}

	private void reset(int i)
	{
		//m[i].release();
		m[i].reset();
		m[i].setAudioStreamType(AudioManager.STREAM_MUSIC);
		try
		{
			m[i].setDataSource(s[i].getUrl());
			m[i].prepare();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			Log.w(TAG, "Could not prepare song");
		}
		m[i].setOnCompletionListener(l[i]);
	}

	private void swap()
	{
		int t = current;
		current = next;
		next = t;
	}

	private void checkSongs()
	{
		if(lock)
		{
			Log.d(TAG, "couldnt checkSongs because lock");
			return;
		}
		if(songs.size() > 0 && s[current] != songs.get(0))
		{
			lock = true;
			s[current] = songs.get(0);
			// The currently playing song has been invalidated, stop and restart player[current]
			if(m[current].isPlaying())m[current].stop();
			reset(current);
			// TODO: Seek to start time
			// m[current].seekTo();
			Log.d(TAG, "checkSongs: start");
			m[current].start();
			Intent k = new Intent(EVENT_SONG_STARTED);
			k.putExtra(EVENT_SONG_STARTED, s[current]);
			LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(k);

			Log.d(TAG, "started playback");
			lock = false;
		}
		if(songs.size() > 1 && s[next] != songs.get(1))
		{
			lock=true;
			s[next] = songs.get(1);
			// The next song has been invalidated, reset the player[next]
			reset(next);
			lock=false;
		}
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		// Upate the song list
		if(intent.getAction().equals(EVENT_FLOOR_JOINED))
		{
            Log.d(TAG, "Floor Joined");
			Floor f = intent.getParcelableExtra(EVENT_FLOOR_JOINED);
			songs = f.getSongs();
		}
		else if(intent.getAction().equals(EVENT_FLOOR_JOINED))
		{
			songs = intent.getParcelableArrayListExtra(EVENT_SONG_LIST_UPDATE);
		}
		Log.d(TAG, "Got Song List");
//		for(Song s:songs)
//        {
//            Log.d(TAG, "Song: " + s.getName() + " - " + s.getArtist());
//            Log.d(TAG, "Url: " + s.getUrl());
//        }
		checkSongs();
	}
}
