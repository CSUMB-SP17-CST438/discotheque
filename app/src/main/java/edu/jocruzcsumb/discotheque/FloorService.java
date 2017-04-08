package edu.jocruzcsumb.discotheque;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import io.socket.emitter.Emitter;

/**
 * IntentService that handles all events inside chat room
 */
public class FloorService extends IntentService
{
    public static final String TAG = "FloorService";

    // When the Song List is updated for the Floor
    public static final String EVENT_SONG_LIST_UPDATE = "song list";

    // When the Message List is updated for the Floor
    public static final String EVENT_USER_LIST_UPDATE = "user list";//TODO

    // When the Message List is updated for the Floor
    public static final String EVENT_MESSAGE_LIST_UPDATE = "message list";//TODO

    // When the UI requests the most recent Song List
    public static final String EVENT_GET_SONG_LIST = "get song list";

    // When the UI requests the most recent Message List
    public static final String EVENT_GET_MESSAGE_LIST = "get message list";

    // When the UI requests the most recent User List
    public static final String EVENT_GET_USER_LIST = "get user list";

    // When a member leaves the Floor that LocalUser is in
    public static final String EVENT_USER_REMOVE = "remove member";

    // When a member joins the Floor that LocalUser is in
    public static final String EVENT_USER_ADD = "add member";

    // When there is a new message from the server to add to the Floor
    public static final String EVENT_MESSAGE_ADD = "add message";

    //When the current LocalUser sends a message
    public static final String EVENT_MESSAGE_SEND = "send message";

    // When the user requests to join the floor
    public static final String EVENT_JOIN_FLOOR = "join floor";

    // When the user requests to leave the floor
    public static final String EVENT_LEAVE_FLOOR = "leave floor";

    // When the server acknowledges that the client has joined the floor
    // This event also contains the entire floor object according to Ryan
    public static final String EVENT_FLOOR_JOINED = ""; //TODO

    // The action that is sent to start the FloorService
    private static final String ACTION_JOIN_FLOOR = "edu.jocruzcsumb.discotheque.action.JOINROOM";

    // Probably will not use this.
    private static final String EXTRA_FLOOR = "edu.jocruzcsumb.discotheque.extra.ROOM";

    private static final Floor floor = null;

    public FloorService()
    {
        super("FloorService");
    }

    /**
     * Starts this service by joining a room.
     *
     * @see IntentService
     */
    public static void joinRoom(Context context, String room)
    {
        Intent intent = new Intent(context, FloorService.class);
        intent.setAction(ACTION_JOIN_FLOOR);
        intent.putExtra(EXTRA_FLOOR, room);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        if (intent != null)
        {
            final String action = intent.getAction();
            if (ACTION_JOIN_FLOOR.equals(action))
            {
                final String param1 = intent.getStringExtra(EXTRA_FLOOR);
                handleActionJoinRoom(param1);
            }
        }
    }

    private void broadcast(Intent k)
    {
        LocalBroadcastManager.getInstance(getApplicationContext())
                             .sendBroadcast(k);
    }

    private void broadcast(String event, Serializable params)
    {
        Intent k = new Intent(event);
        k.putExtra(event, params);
        broadcast(k);
    }

    /**
     * Handle action Join room in the provided background thread with the provided
     * parameters.
     */
    private void handleActionJoinRoom(String room)
    {

        // This tells the service what LocalBroadcast Events to listen for
        IntentFilter f = new IntentFilter();
        f.addAction(FloorService.EVENT_GET_SONG_LIST);
        f.addAction(FloorService.EVENT_GET_USER_LIST);
        f.addAction(FloorService.EVENT_GET_MESSAGE_LIST);
        f.addAction(FloorService.EVENT_LEAVE_FLOOR);
        f.addAction(FloorService.EVENT_MESSAGE_SEND);

        // Set the activity to listen for app broadcasts with the above filter
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(r, f);

        //TODO get the current floor from server
        Sockets.SocketWaiter waiter = new Sockets.SocketWaiter(EVENT_JOIN_FLOOR, EVENT_FLOOR_JOINED);

        // List Events
        Sockets.getSocket()
               .on(EVENT_SONG_LIST_UPDATE, new Emitter.Listener()
               {
                   @Override
                   public void call(Object... args)
                   {
                       Log.d(TAG, EVENT_SONG_LIST_UPDATE);

                       //Update the floor object with the new list
                       floor.setSongs(parseSongList((JSONArray) args[0]));

                       // Broadcast the event (so that RoomActivity can update)
                       broadcast(EVENT_SONG_LIST_UPDATE, floor.getSongs());
                   }
               });
        Sockets.getSocket()
               .on(EVENT_USER_LIST_UPDATE, new Emitter.Listener()
               {
                   @Override
                   public void call(Object... args)
                   {
                       Log.d(TAG, EVENT_USER_LIST_UPDATE);

                       //Update the floor object with the new list
                       floor.setUsers(parseUserList((JSONArray) args[0]));

                       // Broadcast the event (so that RoomActivity can update)
                       broadcast(EVENT_USER_LIST_UPDATE, floor.getUsers());
                   }
               });
        Sockets.getSocket()
               .on(EVENT_MESSAGE_LIST_UPDATE, new Emitter.Listener()
               {
                   @Override
                   public void call(Object... args)
                   {
                       Log.d(TAG, EVENT_MESSAGE_LIST_UPDATE);

                       //Update the floor object with the new list
                       floor.setMessages(parseMessages((JSONArray) args[0]));

                       // Broadcast the event (so that RoomActivity can update)
                       broadcast(EVENT_MESSAGE_LIST_UPDATE, floor.getMessages());
                   }
               });

        // Add Events
        Sockets.getSocket()
               .on(EVENT_USER_ADD, new Emitter.Listener()
               {
                   @Override
                   public void call(Object... args)
                   {
                       Log.d(TAG, EVENT_USER_ADD);

                       User u = null;
                       // Get the user object
                       try
                       {
                           u = User.parse((JSONObject) args[0]);
                       }
                       catch (JSONException e)
                       {
                           e.printStackTrace();
                           Log.w(TAG, EVENT_USER_ADD + ": failed to parse json");
                           return;
                       }

                       // Add the user to the floor object
                       floor.getUsers()
                            .add(u);

                       // Broadcast the event (so that RoomActivity can update)
                       broadcast(EVENT_USER_ADD, u);
                   }
               });
        Sockets.getSocket()
               .on(EVENT_USER_REMOVE, new Emitter.Listener()
               {
                   @Override
                   public void call(Object... args)
                   {
                       Log.d(TAG, EVENT_USER_REMOVE);

                       User u = null;
                       // Get the user object
                       try
                       {
                           u = User.parse((JSONObject) args[0]);
                       }
                       catch (JSONException e)
                       {
                           e.printStackTrace();
                           Log.w(TAG, EVENT_USER_REMOVE + ": failed to parse json");
                           return;
                       }

                       // Remove the user from the floor object
                       floor.getUsers()
                            .remove(u);

                       // Broadcast the event (so that RoomActivity can update)
                       broadcast(EVENT_USER_REMOVE, u);
                   }
               });
        Sockets.getSocket()
               .on(EVENT_MESSAGE_ADD, new Emitter.Listener()
               {
                   @Override
                   public void call(Object... args)
                   {
                       Log.d(TAG, EVENT_MESSAGE_ADD);

                       Message m = null;
                       // Get the user object
                       try
                       {
                           m = Message.parse((JSONObject) args[0]);
                       }
                       catch (JSONException e)
                       {
                           e.printStackTrace();
                           Log.w(TAG, EVENT_MESSAGE_ADD + ": failed to parse json");
                           return;
                       }

                       // Add the user to the floor object
                       floor.getMessages()
                            .add(m);

                       // Broadcast the event (so that RoomActivity can update)
                       broadcast(EVENT_MESSAGE_ADD, m);
                   }
               });
    }

    BroadcastReceiver r = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.d(TAG, "onRecieve");
            Log.d(TAG, "intent.getAction() = " + intent.getAction());
            switch (intent.getAction())
            {
                case FloorService.EVENT_GET_SONG_LIST:
                    Log.d(TAG, EVENT_GET_SONG_LIST);

                    break;
                case FloorService.EVENT_GET_USER_LIST:
                    Log.d(TAG, EVENT_GET_USER_LIST);

                    break;
                case FloorService.EVENT_MESSAGE_SEND:
                    Log.d(TAG, EVENT_MESSAGE_SEND);

                    break;
                case FloorService.EVENT_LEAVE_FLOOR:
                    Log.d(TAG, EVENT_LEAVE_FLOOR);

                    break;
            }
        }
    };
    private ArrayList<Song> parseSongList(JSONArray a)
    {
        if (a != null)
        {
            try
            {
                Log.d(TAG, a.toString());
                int arrayLength = a.length();
                ArrayList<Song> songList = new ArrayList<Song>();
                for (int i = 0; i < arrayLength; i++)
                {
                    songList.add(Song.parse(a.getJSONObject(i)));
                }
                return songList;
            }
            catch (JSONException e)
            {
                e.printStackTrace();
                Log.w(TAG, "Song List" + ": failed to parse json");
            }
        }
        return null;
    }

    private ArrayList<Message> parseMessages(JSONArray a)
    {
        if (a != null)
        {
            try
            {
                Log.d(TAG, a.toString());
                int arrayLength = a.length();
                ArrayList<Message> messageList = new ArrayList<Message>();
                for (int i = 0; i < arrayLength; i++)
                {
                    messageList.add(Message.parse(a.getJSONObject(i)));
                }
                return messageList;
            }
            catch (JSONException e)
            {
                e.printStackTrace();
                Log.w(TAG, "Message List" + ": failed to parse json");
            }
        }
        return null;
    }

    private ArrayList<User> parseUserList(JSONArray a)
    {
        if (a != null)
        {
            try
            {
                Log.d(TAG, a.toString());
                int arrayLength = a.length();
                ArrayList<User> userList = new ArrayList<User>();
                for (int i = 0; i < arrayLength; i++)
                {
                    userList.add(User.parse(a.getJSONObject(i)));
                }
                return userList;
            }
            catch (JSONException e)
            {
                e.printStackTrace();
                Log.w(TAG, "User List" + ": failed to parse json");
            }
        }
        return null;
    }
}
