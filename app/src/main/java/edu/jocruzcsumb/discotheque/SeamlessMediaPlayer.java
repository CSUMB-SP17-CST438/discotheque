package edu.jocruzcsumb.discotheque;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
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

    private int current = 0;
    private int next = 1;

    //TODO when player object is started, we seek to utc.now - utc start time
    private long timeStarted = 0;

    private void reset(int i)
    {
        m[i].release();
        m[i].reset();
        m[i].setAudioStreamType(AudioManager.STREAM_MUSIC);
        try
        {
            m[i].setDataSource(s[i].getUrl());
            m[i].prepare();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.w(TAG, "Could not prepare song");
        }
    }
    private void swap()
    {
        int t = current;
        current = next;
        next = t;
    }

    private MediaPlayer.OnCompletionListener[] l = new MediaPlayer.OnCompletionListener[]
    {
        new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                m[1].start();
                reset(0);
                swap();

            }
        },
        new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                m[0].start();
                reset(1);
                swap();
            }
        }
    };
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
        LocalBroadcastManager.getInstance(context.getApplicationContext())
                             .registerReceiver(this, f);
        LocalBroadcastManager b = LocalBroadcastManager.getInstance(context.getApplicationContext());
        b.registerReceiver(this, f);

        //Set up the media players
        for (int i = 0; i < 2; i++)
        {
            m[i] = new MediaPlayer();
            m[i].setOnCompletionListener(l[i]);
        }

        // Request the song list
        Intent k = new Intent(EVENT_GET_SONG_LIST);
        b.sendBroadcast(k);
    }

    private void checkSongs()
    {
        if(s[current] != songs.get(0))
        {
            s[current] = songs.get(0);
            // The currently playing song has been invalidated, stop and restart player[current]
            m[current].stop();
            reset(current);
            // TODO: Seek to start time
            // m[current].seekTo();
            m[current].start();

        }
        if(s[next] != songs.get(1))
        {
            s[next] = songs.get(1);
            // The next song has been invalidated, reset the player[next]
            reset(next);
        }

    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d(TAG, "onRecieve");
        Log.d(TAG, "intent.getAction() = " + intent.getAction());

        // Upate the song list
        if(intent.getAction().equals(EVENT_FLOOR_JOINED))
        {
            Floor f = intent.getParcelableExtra(EVENT_FLOOR_JOINED);
            songs = f.getSongs();
        }
        else if(intent.getAction().equals(EVENT_FLOOR_JOINED))
        {
            songs = intent.getParcelableArrayListExtra(EVENT_SONG_LIST_UPDATE);
        }
        checkSongs();
    }
}
