package edu.jocruzcsumb.discotheque;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
    private final BroadcastReceiver r = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.d(TAG, "onRecieve: " + intent.getAction());

            if (intent.getAction()
                      .equals(EVENT_FLOOR_JOINED))
            {
                floor = intent.getParcelableExtra(EVENT_FLOOR_JOINED);
                onFloorJoined(floor);
            }
            else if (floor != null)
            {
                switch (intent.getAction())
                {
                    case EVENT_SONG_STARTED:
                        Song s = intent.getParcelableExtra(EVENT_SONG_STARTED);
                        onSongStarted(s);
                        break;
                    case EVENT_SONG_STOPPED:
                        Song x = intent.getParcelableExtra(EVENT_SONG_STOPPED);
                        onSongStopped(x);
                        break;
                    case EVENT_SONG_LIST_UPDATE:
                        ArrayList<Song> songs = intent.getParcelableArrayListExtra(EVENT_SONG_LIST_UPDATE);
                        floor.setSongs(songs);
                        onSongListUpdate(songs);
                        break;
                    case EVENT_USER_LIST_UPDATE:
                        ArrayList<User> users = intent.getParcelableArrayListExtra(EVENT_USER_LIST_UPDATE);
                        floor.setUsers(users);
                        onUserListUpdate(users);
                        break;
                    case EVENT_MESSAGE_LIST_UPDATE:
                        ArrayList<Message> messages = intent.getParcelableArrayListExtra(EVENT_MESSAGE_LIST_UPDATE);
                        floor.setMessages(messages);
                        onMessageListUpdate(messages);
                        break;
                    case EVENT_MESSAGE_ADD:
                        Message m = intent.getParcelableExtra(EVENT_MESSAGE_ADD);
                        floor.getMessages()
                             .add(m);
                        onMessageAdded(m);
                        break;
                    case EVENT_USER_ADD:
                        User u = intent.getParcelableExtra(EVENT_USER_ADD);
                        floor.getUsers()
                             .add(u);
                        onUserAdded(u);
                        break;
                    case EVENT_USER_REMOVE:
                        User r = intent.getParcelableExtra(EVENT_USER_REMOVE);
                        floor.getUsers()
                             .remove(r);
                        onUserRemoved(r);
                        break;
                }
            }
            else
            {
                broadcast(EVENT_GET_FLOOR);
            }
        }
    };

    public FloorFragment()
    {
        // This tells the Fragment what LocalBroadcast Events to listen for
        IntentFilter f = new IntentFilter();
        f.addAction(EVENT_FLOOR_JOINED);
        f.addAction(EVENT_SONG_LIST_UPDATE);
        f.addAction(EVENT_USER_LIST_UPDATE);
        f.addAction(EVENT_MESSAGE_LIST_UPDATE);
        f.addAction(EVENT_USER_ADD);
        f.addAction(EVENT_USER_REMOVE);
        f.addAction(EVENT_MESSAGE_ADD);
        f.addAction(EVENT_SONG_STARTED);
        f.addAction(EVENT_SONG_STOPPED);

        // Set the Fragment to listen for app broadcasts with the above filter
        LocalBroadcastManager.getInstance(this.getContext())
                             .registerReceiver(r, f);
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
