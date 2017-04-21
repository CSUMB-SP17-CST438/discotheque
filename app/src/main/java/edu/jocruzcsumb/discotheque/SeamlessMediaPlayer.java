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

import static edu.jocruzcsumb.discotheque.FloorService.EVENT_GET_SONG_LIST;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_SONG_LIST_UPDATE;

/**
 * Created by carsen on 4/9/17.
 */

public class SeamlessMediaPlayer extends BroadcastReceiver implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener
{
    public static final String EVENT_SONG_STARTED = "song started";
    public static final String EVENT_SONG_STOPPED = "song stopped";
    private static final String TAG = "SeamlessMediaPlayer";
    private MediaPlayer[] m = new MediaPlayer[2];
    private Song[] s = new Song[2];
    private int current = 0;
    private int next = 1;
    private boolean lock = false;

    //TODO when player object is started, we seek to utc.now - utc start time
    private long timeStarted = 0;
    private Context context;
    private ArrayList<Song> songs = null;

    public SeamlessMediaPlayer(Context context)
    {
        this.context = context;
        this.songs = new ArrayList<Song>();

        // Recieve song events
        IntentFilter f = new IntentFilter();
        f.addAction(EVENT_SONG_LIST_UPDATE);
        LocalBroadcastManager.getInstance(context.getApplicationContext())
                             .registerReceiver(this, f);
        LocalBroadcastManager b = LocalBroadcastManager.getInstance(context.getApplicationContext());
        b.registerReceiver(this, f);

        // Request the song list
        Intent k = new Intent(EVENT_GET_SONG_LIST);
        b.sendBroadcast(k);
        //lock = true;
    }

    private void reset(int i)
    {
        m[i] = new MediaPlayer();
        m[i].setAudioStreamType(AudioManager.STREAM_MUSIC);
        try
        {
            m[i].setDataSource(s[i].getUrl());
            m[i].setLooping(false);
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

    private void checkSongs()
    {
        if (lock)
        {
            Log.d(TAG, "couldnt checkSongs because lock");
            return;
        }
        lock = true;
        Song cur = songs.get(0);
        boolean start = s[current] == null;
        long localtime = System.currentTimeMillis() / 1000;
        boolean needSeek = localtime > cur.getStartTime();

        // Check to see that there are songs in the list
        if (songs.size() <= 0)
        {
            Log.e(TAG, "Song list was empty");
            lock = false;
            return;
        }

        // Check to see that the start time is valid
        if (cur.getStartTime() == 0)
        {
            Log.e(TAG, "Start time was invalid");
            lock = false;
            return;
        }

        if (!start && cur == s[next])
        {// The service has told us that the next song is about to play.
            if(s[current].getStartTime() < localtime && cur.getStartTime() > localtime)
            {
                if (!m[current].isPlaying())
                {
                    //this is a weird case, the next song is scheduled
                    // ...but the current song is not playing
                    prepareNext();
                    swap();
                    localtime = System.currentTimeMillis() / 1000;
                    try
                    {
                        Thread.sleep(s[current].getStartTime() - localtime);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                        Log.e(TAG, "Thread.sleep InterruptedException");
                        return;
                    }
                    if(!startCurrent())
                    {
                        lock = false;
                        return;
                    }
                }
                // else: we do not interrupt the songs as they are scheduled correctly.
                lock = false;
                return;
            }
            else
            {
                Log.e(TAG, "WARNING COULD NOT SYNC");
                lock = false;
                return;
            }
        }
        else
        {
            if(start)
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
                    localtime = System.currentTimeMillis() / 1000;
                    m[current].seekTo(1000 * (int) (localtime - s[current].getStartTime()));
                    if(!startCurrent())
                    {
                        lock = false;
                        return;
                    }
                }
                else // NOT need seek
                {
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
                    if(!startCurrent())
                    {
                        lock = false;
                        return;
                    }
                }
            }
            if(songs.size() > 1 && songs.get(1) != s[next])
            {
                s[next] = songs.get(1);
                prepareNext();
            }
        }
        lock = false;
    }

    private boolean startCurrent()
    {
        try
        {
            m[current].start();
            m[current].setOnCompletionListener(this);
            m[current].setOnErrorListener(this);
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
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction()
                  .equals(EVENT_SONG_LIST_UPDATE))
        {
            songs = intent.getParcelableArrayListExtra(EVENT_SONG_LIST_UPDATE);
            Log.d(TAG, "Got Song List");
            checkSongs();

//		for(Song s:songs)
//        {
//            Log.d(TAG, "Song: " + s.getName() + " - " + s.getArtist());
//            Log.d(TAG, "Url: " + s.getUrl());
//        }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer)
    {
        Log.i(TAG, "onCompletion");

        // Swap the mediaplayers
        swap();
        // Start the new song
        startCurrent();
        // Prepare the next song
        prepareNext();

        // dispose of the player that just finished
        mediaPlayer.release();
        songs.remove(s[next]);
        //checkSongs();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1)
    {
        Log.e(TAG, "onError: (" + String.valueOf(i) + ", " + String.valueOf(i1) + ")");
        return false;
    }

    public void stop()
    {
        if (m[next].isPlaying())
        {
            m[next].stop();
        }
        if (m[current].isPlaying())
        {
            m[current].stop();
        }
    }
}
