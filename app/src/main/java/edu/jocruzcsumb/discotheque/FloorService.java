package edu.jocruzcsumb.discotheque;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import io.socket.emitter.Emitter;

/**
 * IntentService that handles all events inside chat floor
 */
public class FloorService extends IntentService
{
    public static final String TAG = "FloorService";

    // Requests the Floor object
    public static final String EVENT_GET_FLOOR = "get floor";

    // When the Song List is updated for the Floor
    public static final String EVENT_SONG_LIST_UPDATE = "song list";

    // When the Message List is updated for the Floor
    public static final String EVENT_USER_LIST_UPDATE = "member list update";

    // When the Message List is updated for the Floor
    public static final String EVENT_MESSAGE_LIST_UPDATE = "message list";

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
    public static final String EVENT_MESSAGE_SEND = "new message";

    // When the user requests to join the floor
    public static final String EVENT_JOIN_FLOOR = "join floor";

    // When the user requests to leave the floor
    public static final String EVENT_LEAVE_FLOOR = "leave floor";

    // When the server acknowledges that the client has joined the floor
    // This event also contains the entire floor object according to Ryan
    public static final String EVENT_FLOOR_JOINED = "floor joined";

    // The action that is sent to start the FloorService
    private static final String ACTION_JOIN_FLOOR = "edu.jocruzcsumb.discotheque.action.JOINFLOOR";

    // Probably will not use this.
    private static final String EXTRA_FLOOR = "edu.jocruzcsumb.discotheque.extra.FLOOR";

    private Floor floor = null;
    private CountDownLatch floorLatch = null;

    BroadcastReceiver r = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.v(TAG, "onRecieve: " + intent.getAction());
            switch (intent.getAction())
            {
                case EVENT_GET_FLOOR:
                    if (floor != null)
                    {
                        broadcast(EVENT_FLOOR_JOINED, floor);
                        broadcast(EVENT_MESSAGE_LIST_UPDATE, floor.getMessages());
                        broadcast(EVENT_USER_LIST_UPDATE, floor.getUsers());
                        broadcast(EVENT_SONG_LIST_UPDATE, floor.getSongs());
                    }
                    else
                    {
                        Log.w(TAG, EVENT_GET_FLOOR + ": Floor was null");
                    }
                    break;
                case EVENT_GET_SONG_LIST:
                    if (floor != null)
                    {
                        broadcast(EVENT_SONG_LIST_UPDATE, floor.getSongs());
                    }
                    else
                    {
                        Log.w(TAG, EVENT_GET_SONG_LIST + ": Floor was null");
                    }
                    break;
                case EVENT_GET_USER_LIST:
                    if (floor != null)
                    {
                        broadcast(EVENT_USER_LIST_UPDATE, floor.getUsers());
                    }
                    else
                    {
                        Log.w(TAG, EVENT_GET_USER_LIST + ": Floor was null");
                    }
                    break;
                case EVENT_GET_MESSAGE_LIST:
                    if (floor != null)
                    {
                        broadcast(EVENT_MESSAGE_LIST_UPDATE, floor.getMessages());
                    }
                    else
                    {
                        Log.w(TAG, EVENT_GET_MESSAGE_LIST + ": Floor was null");
                    }
                    break;
                case EVENT_MESSAGE_SEND:
                    JSONObject jsonObject = new JSONObject();
                    try
                    {
                        jsonObject.put("floor", floor.getId()); //floor_id
                        jsonObject.put("from", LocalUser.getCurrentUser()
                                                        .getId()); //member_id
                        jsonObject.put("message", ((Message) intent.getParcelableExtra(EVENT_MESSAGE_SEND)).getText());
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    Sockets.getSocket()
                           .emit(EVENT_MESSAGE_SEND, jsonObject);
                    break;
                case EVENT_LEAVE_FLOOR:
                    Log.v(TAG, EVENT_LEAVE_FLOOR);
                    floorLatch.countDown();
                    break;
            }
        }
    };

    public FloorService()
    {
        super("FloorService");
    }

    /**
     * Starts this service by joining a floor.
     *
     * @see IntentService
     */
    public static void joinFloor(Context context, int floorId)
    {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (FloorService.class.getName().equals(service.service.getClassName()))
            {
                Log.i(TAG, "Avoided starting a new service, the service is already running");
                return;
            }
        }
        Intent intent = new Intent(context, FloorService.class);
        intent.setAction(ACTION_JOIN_FLOOR);
        intent.putExtra(Floor.JSON_ID_TAG, floorId);
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
                final int floorId = intent.getIntExtra(Floor.JSON_ID_TAG, 0);
                if (floorId != 0)
                {
                    handleActionJoinFloor(floorId);
                }
                else
                {
                    Log.w(TAG, "Could not join room because floorId was 0");
                }
            }
        }
    }

    // EVENTS are broadcasted here
    private void broadcast(String event, Parcelable params)
    {
        Intent k = new Intent(event);
        k.putExtra(event, params);
        broadcast(k);
    }

    private void broadcast(String event, ArrayList<? extends Parcelable> params)
    {
        Intent k = new Intent(event);
        k.putParcelableArrayListExtra(event, params);
        broadcast(k);
    }

    private void broadcast(Intent k)
    {
        LocalBroadcastManager.getInstance(getApplicationContext())
                             .sendBroadcast(k);
    }

    /**
     * Handle action Join floor in the provided background thread with the provided
     * parameters.
     */
    private void handleActionJoinFloor(int floorId)
    {
        //This holds the service open until the room is left
        floorLatch = new CountDownLatch(1);

        // This tells the service what LocalBroadcast Events to listen for
        IntentFilter f = new IntentFilter();
        f.addAction(EVENT_GET_SONG_LIST);
        f.addAction(EVENT_GET_USER_LIST);
        f.addAction(EVENT_GET_MESSAGE_LIST);
        f.addAction(EVENT_LEAVE_FLOOR);
        f.addAction(EVENT_MESSAGE_SEND);
        f.addAction(EVENT_GET_FLOOR);

        // Set the activity to listen for app broadcasts with the above filter
        LocalBroadcastManager.getInstance(getApplicationContext())
                             .registerReceiver(r, f);


        // Finally, ask the server to join the floor and retreive the floor object.
        Sockets.SocketWaiter waiter = new Sockets.SocketWaiter(EVENT_JOIN_FLOOR, EVENT_FLOOR_JOINED);

        JSONObject obj = new JSONObject();
        try
        {
            obj.put(Floor.JSON_ID_TAG, floorId);
            obj.put(LocalUser.JSON_ID_TAG, LocalUser.getCurrentUser()
                                                    .getId());
            obj = waiter.getObj(obj);
            if (obj == null)
            {
                Log.w(TAG, EVENT_FLOOR_JOINED + "Returned null");
                return;
            }
            floor = Floor.parseAdvanced(obj);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            Log.w(TAG, EVENT_FLOOR_JOINED + ": Unable to parse floor from json");
            return;
        }

        if (floor == null)
        {
            Log.w(TAG, "Floor was null");
            return;
        }

        //Set up the mediaplayer
        Log.d(TAG, "Starting seamless player");
        SeamlessMediaPlayer seamlessMediaPlayer = new SeamlessMediaPlayer(this);

        Log.v(TAG, EVENT_FLOOR_JOINED);
        broadcast(EVENT_FLOOR_JOINED, floor);
        broadcast(EVENT_MESSAGE_LIST_UPDATE, floor.getMessages());
        broadcast(EVENT_USER_LIST_UPDATE, floor.getUsers());
        broadcast(EVENT_SONG_LIST_UPDATE, floor.getSongs());

        registerSocketEvents();
        try
        {
            floorLatch.await();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            Log.w(TAG, "The floorLatch was interruped, leaving the floor");
        }
        seamlessMediaPlayer.stop();
        unregisterSocketEvents();
        obj = new JSONObject();
        try
        {
            obj.put("floor_id", floor.getId());
            obj.put("member_id", LocalUser.getCurrentUser()
                                          .getId());
            Sockets.getSocket()
                   .emit(EVENT_LEAVE_FLOOR, obj);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            Log.e(TAG, "could not create json to leave floor");
        }
    }

    private void unregisterSocketEvents()
    {
        for (String e : new String[]
                {
                        EVENT_FLOOR_JOINED,
                        EVENT_SONG_LIST_UPDATE,
                        EVENT_MESSAGE_LIST_UPDATE,
                        EVENT_USER_LIST_UPDATE,
                        EVENT_MESSAGE_ADD,
                        EVENT_USER_ADD,
                        EVENT_USER_REMOVE,
                })
        {
            Sockets.getSocket()
                   .off(e);
        }
    }

    private void registerSocketEvents()
    {
        // List Events
        Sockets.getSocket()
               .on(EVENT_SONG_LIST_UPDATE, new Emitter.Listener()
               {
                   @Override
                   public void call(Object... args)
                   {
                       Log.d(TAG, EVENT_SONG_LIST_UPDATE);

                       JSONObject o = (JSONObject) args[0];
                       ArrayList<Song> l = null;

                       if (o == null)
                       {
                           Log.w(TAG, EVENT_SONG_LIST_UPDATE + ": json was null");
                           return;
                       }

                       // Parse JSON
                       try
                       {
                           JSONArray a = o.getJSONArray("songlist");
                           l = Song.parse(a);
                       }
                       catch (JSONException e)
                       {
                           e.printStackTrace();
                           Log.w(TAG, EVENT_SONG_LIST_UPDATE + ": failed to parse json");
                           return;
                       }

                       if (l == null)
                       {
                           Log.w(TAG, EVENT_SONG_LIST_UPDATE + ": arraylist was null");
                           return;
                       }

                       // Set songs
                       FloorService.this.floor.setSongs(l);

                       // Broadcast the event (so that FloorActivity can update)
                       broadcast(EVENT_SONG_LIST_UPDATE, l);
                   }
               });
        Sockets.getSocket()
               .on(EVENT_USER_LIST_UPDATE, new Emitter.Listener()
               {
                   @Override
                   public void call(Object... args)
                   {
                       Log.d(TAG, EVENT_USER_LIST_UPDATE);

                       JSONObject o = (JSONObject) args[0];
                       ArrayList<User> l = null;

                       if (o == null)
                       {
                           Log.w(TAG, EVENT_USER_LIST_UPDATE + ": json was null");
                           return;
                       }

                       // Parse JSON
                       try
                       {
                           Log.d(TAG, o.toString());
                           JSONArray a = o.getJSONArray("floor members");
                           l = User.parse(a);
                       }
                       catch (JSONException e)
                       {
                           e.printStackTrace();
                           Log.w(TAG, EVENT_USER_LIST_UPDATE + ": failed to parse json");
                           return;
                       }

                       if (l == null)
                       {
                           Log.w(TAG, EVENT_USER_LIST_UPDATE + ": arraylist was null");
                           return;
                       }

                       // Set users
                       FloorService.this.floor.setUsers(l);

                       // Broadcast the event (so that FloorActivity can update)
                       broadcast(EVENT_USER_LIST_UPDATE, l);
                   }
               });
        Sockets.getSocket()
               .on(EVENT_MESSAGE_LIST_UPDATE, new Emitter.Listener()
               {
                   @Override
                   public void call(Object... args)
                   {
                       Log.d(TAG, EVENT_MESSAGE_LIST_UPDATE);

                       JSONObject o = (JSONObject) args[0];
                       ArrayList<Message> l = null;

                       if (o == null)
                       {
                           Log.w(TAG, EVENT_MESSAGE_LIST_UPDATE + ": json was null");
                           return;
                       }

                       // Parse JSON
                       try
                       {
                           JSONArray a = o.getJSONArray("floor_messages");
                           l = Message.parse(a);
                       }
                       catch (JSONException e)
                       {
                           e.printStackTrace();
                           Log.w(TAG, EVENT_MESSAGE_LIST_UPDATE + ": failed to parse json");
                           return;
                       }

                       if (l == null)
                       {
                           Log.w(TAG, EVENT_MESSAGE_LIST_UPDATE + ": arraylist was null");
                           return;
                       }

                       // Set Messages
                       FloorService.this.floor.setMessages(l);

                       // Broadcast the event (so that FloorActivity can update)
                       broadcast(EVENT_MESSAGE_LIST_UPDATE, l);
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
                       FloorService.this.floor.getUsers()
                                              .add(u);

                       // Broadcast the event (so that FloorActivity can update)
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
                       FloorService.this.floor.getUsers()
                                              .remove(u);

                       // Broadcast the event (so that FloorActivity can update)
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
                       FloorService.this.floor.getMessages()
                                              .add(m);

                       // Broadcast the event (so that FloorActivity can update)
                       broadcast(EVENT_MESSAGE_ADD, m);
                   }
               });
    }
}
