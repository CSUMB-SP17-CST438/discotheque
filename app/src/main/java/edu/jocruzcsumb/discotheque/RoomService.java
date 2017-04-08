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
    public static final String EVENT_GET_SONG_LIST = "get song list";
    public static final String EVENT_USER_REMOVE = "member left";
    public static final String EVENT_USER_ADD = "member joined";
    public static final String EVENT_NEW_MESSAGE = "message added";

    public static final String MESSAGE_FROM_TAG = "member";
    public static final String MESSAGE_FLOOR_TAG = "floor";
    public static final String MESSAGE_TEXT_TAG = "text";
    public static final String MESSAGE_PUB_TIME_TAG = "pubTime";
    public static final String MESSAGE_ID_TAG = "mess_id";

    public static final String USER_USERNAME_TAG = "username";
    public static final String USER_CREATED_FLOORS_TAG = "created_floors";
    public static final String USER_FNAME_TAG = "member_FName";
    public static final String USER_LNAME_TAG = "member_LName";
    public static final String USER_IMG_URL_TAG = "member_img_url";

    public static final String SONG_NAME_TAG = "title";
    public static final String SONG_ARTIST_TAG = "creator_user";
    public static final String SONG_STREAM_URL_TAG = "stream_url";
    public static final String SONG_ARTWORK_TAG = "artwork";

    private static final String ACTION_JOIN_ROOM = "edu.jocruzcsumb.discotheque.action.JOINROOM";

    private static final String EXTRA_ROOM = "edu.jocruzcsumb.discotheque.extra.ROOM";

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

    private static ArrayList<Song> songs = null;
    private static ArrayList<Message> messages = null;

    /**
     * Handle action Join room in the provided background thread with the provided
     * parameters.
     */
    private void handleActionJoinRoom(String room)
    {
        Sockets.getSocket()
               .on(EVENT_SONG_LIST_UPDATE, new Emitter.Listener()
               {
                   @Override
                   public void call(Object... args)
                   {
                       Log.d(TAG, EVENT_SONG_LIST_UPDATE);
                       songs = parseSongList((JSONArray) args[0]);
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
                           messages.add(Message.parse((JSONObject) args[0]));
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
}
