package edu.jocruzcsumb.discotheque;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;

import static edu.jocruzcsumb.discotheque.FloorService.EVENT_FLOOR_JOINED;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_GET_FLOOR;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_MESSAGE_ADD;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_MESSAGE_LIST_UPDATE;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_SONG_LIST_UPDATE;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_USER_ADD;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_USER_LIST_UPDATE;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_USER_REMOVE;
import static edu.jocruzcsumb.discotheque.SeamlessMediaPlayer.EVENT_SONG_STARTED;
import static edu.jocruzcsumb.discotheque.SeamlessMediaPlayer.EVENT_SONG_STOPPED;

/**
 * Created by carsen on 4/11/17.
 */

public abstract class FloorFragment extends Fragment
{
    private static final String TAG = "FloorFragment";
    protected static Floor floor = null;

    public FloorFragment()
    {
        FloorListener listener = new FloorListener(getContext())
        {
            public void onSongStarted(Song s)
            {
                FloorFragment.this.onSongStarted(s);
            }

            public void onSongStopped(Song s)
            {
                FloorFragment.this.onSongStopped(s);
            }

            public void onFloorJoined(Floor floor)
            {
                FloorFragment.this.onFloorJoined(floor);
            }

            public void onSongListUpdate(ArrayList<Song> songs)
            {
                FloorFragment.this.onSongListUpdate(songs);
            }

            public void onUserListUpdate(ArrayList<User> users)
            {
                FloorFragment.this.onUserListUpdate(users);
            }

            public void onUserAdded(User u)
            {
                FloorFragment.this.onUserAdded(u);
            }

            public void onUserRemoved(User u)
            {
                FloorFragment.this.onUserRemoved(u);
            }

            public void onMessageListUpdate(ArrayList<Message> messages)
            {
                FloorFragment.this.onMessageListUpdate(messages);
            }

            public void onMessageAdded(Message m)
            {
                FloorFragment.this.onMessageAdded(m);
            }
        };
    }
    // EVENTS are broadcasted here
    protected void broadcast(String event)
    {
        Intent k = new Intent(event);
        broadcast(k);
    }

    private void broadcast(Intent k)
    {
        LocalBroadcastManager.getInstance(this.getContext())
                             .sendBroadcast(k);
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        floor = ((FloorActivity) getActivity()).floor;
    }

    // These are all methods that are optional to implement, they are called when events are recieved
    public void onSongStarted(Song s)
    {
    }

    public void onSongStopped(Song s)
    {
    }

    public void onFloorJoined(Floor floor)
    {
    }

    public void onSongListUpdate(ArrayList<Song> songs)
    {
    }

    public void onUserListUpdate(ArrayList<User> users)
    {
    }

    public void onUserAdded(User u)
    {
    }

    public void onUserRemoved(User u)
    {
    }

    public void onMessageListUpdate(ArrayList<Message> messages)
    {
    }

    public void onMessageAdded(Message m)
    {
    }
}
