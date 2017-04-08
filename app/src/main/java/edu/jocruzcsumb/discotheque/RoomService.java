package edu.jocruzcsumb.discotheque;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.emitter.Emitter;

/**
 * IntentService that handles all events inside chat room
 */
public class RoomService extends IntentService
{
    public static final String TAG = "RoomService";
    public static final String EVENT_SONG_LIST_UPDATE = "song list";
    public static final String EVENT_USER_LIST_UPDATE = "user list";//TODO
    public static final String EVENT_MESSAGE_LIST_UPDATE = "message list";//TODO

    public static final String EVENT_GET_SONG_LIST = "get song list";
    public static final String EVENT_GET_MESSAGE_LIST = "get message list";
    public static final String EVENT_GET_USER_LIST = "get user list";

    public static final String EVENT_USER_REMOVE = "remove member";
    public static final String EVENT_USER_ADD = "add member";

    public static final String EVENT_NEW_MESSAGE = "add message";

    public static final String EVENT_JOIN_ROOM = "join floor";
    public static final String EVENT_ROOM_JOINED = ""; //TODO

    private static final String ACTION_JOIN_ROOM = "edu.jocruzcsumb.discotheque.action.JOINROOM";
    private static final String EXTRA_ROOM = "edu.jocruzcsumb.discotheque.extra.ROOM";

    private static final Floor floor = null;

    public RoomService()
    {
        super("RoomService");
    }

    /**
     * Starts this service by joining a room.
     *
     * @see IntentService
     */
    public static void joinRoom(Context context, String room)
    {
        Intent intent = new Intent(context, RoomService.class);
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

    /**
     * Handle action Join room in the provided background thread with the provided
     * parameters.
     */
    private void handleActionJoinRoom(String room)
    {
        //TODO get the current floor from server

        Sockets.SocketWaiter waiter = new Sockets.SocketWaiter(EVENT_JOIN_ROOM, EVENT_ROOM_JOINED);


        Sockets.getSocket()
               .on(EVENT_SONG_LIST_UPDATE, new Emitter.Listener()
               {
                   @Override
                   public void call(Object... args)
                   {
                       Log.d(TAG, EVENT_SONG_LIST_UPDATE);
                       floor.setSongs(parseSongList((JSONArray) args[0]));
                   }
               });

        Sockets.getSocket()
               .on(EVENT_USER_LIST_UPDATE, new Emitter.Listener()
               {
                   @Override
                   public void call(Object... args)
                   {
                       Log.d(TAG, EVENT_USER_LIST_UPDATE);
                       floor.setUsers(parseUserList((JSONArray) args[0]));
                   }
               });

        Sockets.getSocket()
               .on(EVENT_MESSAGE_LIST_UPDATE, new Emitter.Listener()
               {
                   @Override
                   public void call(Object... args)
                   {
                       Log.d(TAG, EVENT_MESSAGE_LIST_UPDATE);
                       floor.setMessages(parseMessages((JSONArray) args[0]));
                   }
               });
        Sockets.getSocket()
               .emit(EVENT_GET_SONG_LIST);
        Sockets.getSocket()
               .on(EVENT_USER_ADD, new Emitter.Listener()
               {
                   @Override
                   public void call(Object... args)
                   {
                       Log.d(TAG, EVENT_USER_ADD);
                       //TODO
                   }
               });
        Sockets.getSocket()
               .on(EVENT_USER_REMOVE, new Emitter.Listener()
               {
                   @Override
                   public void call(Object... args)
                   {
                       Log.d(TAG, EVENT_USER_REMOVE);
                       //TODO
                   }
               });
        Sockets.getSocket()
               .on(EVENT_NEW_MESSAGE, new Emitter.Listener()
               {
                   @Override
                   public void call(Object... args)
                   {
                       Log.d(TAG, EVENT_NEW_MESSAGE);
                       try
                       {
                           floor.getMessages().add(Message.parse((JSONObject) args[0]));
                       }
                       catch (JSONException e)
                       {
                           e.printStackTrace();
                       }
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
            }
        }
        return null;
    }
}
