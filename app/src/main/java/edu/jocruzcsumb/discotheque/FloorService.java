package edu.jocruzcsumb.discotheque;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.emitter.Emitter;

/**
 * IntentService that handles all events inside chat room
 */
public class FloorService extends IntentService
{
    public static final String TAG = "FloorService";
    public static final String EVENT_SONG_LIST_UPDATE = "song list";
    public static final String EVENT_USER_LIST_UPDATE = "user list";//TODO
    public static final String EVENT_MESSAGE_LIST_UPDATE = "message list";//TODO

    public static final String EVENT_GET_SONG_LIST = "get song list";
    public static final String EVENT_GET_MESSAGE_LIST = "get message list";
    public static final String EVENT_GET_USER_LIST = "get user list";

    public static final String EVENT_USER_REMOVE = "remove member";
    public static final String EVENT_USER_ADD = "add member";

    public static final String EVENT_MESSAGE_ADD = "add message";

    public static final String EVENT_JOIN_ROOM = "join floor";
    public static final String EVENT_ROOM_JOINED = ""; //TODO

    private static final String ACTION_JOIN_ROOM = "edu.jocruzcsumb.discotheque.action.JOINROOM";
    private static final String EXTRA_ROOM = "edu.jocruzcsumb.discotheque.extra.ROOM";

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
        intent.setAction(ACTION_JOIN_ROOM);
        intent.putExtra(EXTRA_ROOM, room);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        if (intent != null)
        {
            final String action = intent.getAction();
            if (ACTION_JOIN_ROOM.equals(action))
            {
                final String param1 = intent.getStringExtra(EXTRA_ROOM);
                handleActionJoinRoom(param1);
            }
        }
    }

    private void broadcast(Intent k)
    {
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(k);
    }

    /**
     * Handle action Join room in the provided background thread with the provided
     * parameters.
     */
    private void handleActionJoinRoom(String room)
    {
        //TODO get the current floor from server
        Sockets.SocketWaiter waiter = new Sockets.SocketWaiter(EVENT_JOIN_ROOM, EVENT_ROOM_JOINED);

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
                       Intent k = new Intent(EVENT_SONG_LIST_UPDATE);
                       k.putExtra(EVENT_SONG_LIST_UPDATE, floor.getSongs());
                       broadcast(k);
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
                       Intent k = new Intent(EVENT_USER_LIST_UPDATE);
                       k.putExtra(EVENT_USER_LIST_UPDATE, floor.getUsers());
                       broadcast(k);
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
                       Intent k = new Intent(EVENT_MESSAGE_LIST_UPDATE);
                       k.putExtra(EVENT_MESSAGE_LIST_UPDATE, floor.getMessages());
                       broadcast(k);
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
                       catch(JSONException e)
                       {
                           e.printStackTrace();
                           Log.w(TAG, EVENT_USER_ADD + ": failed to parse json");
                           return;
                       }

                       // Add the user to the floor object
                       floor.getUsers().add(u);

                       // Broadcast the event (so that RoomActivity can update)
                       Intent k = new Intent(EVENT_USER_ADD);
                       k.putExtra(EVENT_USER_ADD, u);
                       broadcast(k);
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
                       catch(JSONException e)
                       {
                           e.printStackTrace();
                           Log.w(TAG, EVENT_USER_REMOVE + ": failed to parse json");
                           return;
                       }

                       // Remove the user from the floor object
                       floor.getUsers().remove(u);

                       // Broadcast the event (so that RoomActivity can update)
                       Intent k = new Intent(EVENT_USER_REMOVE);
                       k.putExtra(EVENT_USER_REMOVE, u);
                       broadcast(k);
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
                       catch(JSONException e)
                       {
                           e.printStackTrace();
                           Log.w(TAG, EVENT_MESSAGE_ADD + ": failed to parse json");
                           return;
                       }

                       // Add the user to the floor object
                       floor.getMessages().add(m);

                       // Broadcast the event (so that RoomActivity can update)
                       Intent k = new Intent(EVENT_MESSAGE_ADD);
                       k.putExtra(EVENT_MESSAGE_ADD, m);
                       broadcast(k);
                   }
               });
    }

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
