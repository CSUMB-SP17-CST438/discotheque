package edu.jocruzcsumb.discotheque;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import io.socket.emitter.Emitter;

/**
 * IntentService that handles all events inside chat floor
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
	public static final String EVENT_FLOOR_JOINED = "floor joined"; //TODO

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
			Log.d(TAG, "onRecieve");
			Log.d(TAG, "intent.getAction() = " + intent.getAction());
			switch(intent.getAction())
			{
				case FloorService.EVENT_GET_SONG_LIST:
					Log.d(TAG, EVENT_GET_SONG_LIST);
					broadcast(EVENT_SONG_LIST_UPDATE, floor.getSongs());
					break;
				case FloorService.EVENT_GET_USER_LIST:
					Log.d(TAG, EVENT_GET_USER_LIST);
					broadcast(EVENT_USER_LIST_UPDATE, floor.getUsers());
					break;
				case FloorService.EVENT_GET_MESSAGE_LIST:
					Log.d(TAG, EVENT_GET_USER_LIST);
					broadcast(EVENT_MESSAGE_LIST_UPDATE, floor.getMessages());
					break;
				case FloorService.EVENT_MESSAGE_SEND:
					Log.d(TAG, EVENT_MESSAGE_SEND);
					//TODO
					break;
				case FloorService.EVENT_LEAVE_FLOOR:
					Log.d(TAG, EVENT_LEAVE_FLOOR);
					Sockets.getSocket()
							.emit(EVENT_LEAVE_FLOOR);
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
		Intent intent = new Intent(context, FloorService.class);
		intent.setAction(ACTION_JOIN_FLOOR);
		intent.putExtra(Floor.JSON_ID_TAG, floorId);
		context.startService(intent);
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		if(intent != null)
		{
			final String action = intent.getAction();
			if(ACTION_JOIN_FLOOR.equals(action))
			{
				final int floorId = intent.getIntExtra(Floor.JSON_ID_TAG, 0);
				if(floorId != 0)
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

		// Set the activity to listen for app broadcasts with the above filter
		LocalBroadcastManager.getInstance(getApplicationContext())
				.registerReceiver(r, f);

		// List Events
		Sockets.getSocket()
				.on(EVENT_SONG_LIST_UPDATE, new Emitter.Listener()
				{
					@Override
					public void call(Object... args)
					{
						Log.d(TAG, EVENT_SONG_LIST_UPDATE);

						JSONArray a = (JSONArray) args[0];
						ArrayList<Song> l = null;

						if(a == null)
						{
							Log.w(TAG, EVENT_SONG_LIST_UPDATE + ": json was null");
							return;
						}

						// Parse JSON
						try
						{
							l = Song.parse(a);
						}
						catch(JSONException e)
						{
							e.printStackTrace();
							Log.w(TAG, EVENT_SONG_LIST_UPDATE + ": failed to parse json");
							return;
						}

						if(l == null)
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

						JSONArray a = (JSONArray) args[0];
						ArrayList<User> l = null;

						if(a == null)
						{
							Log.w(TAG, EVENT_USER_LIST_UPDATE + ": json was null");
							return;
						}

						// Parse JSON
						try
						{
							l = User.parse(a);
						}
						catch(JSONException e)
						{
							e.printStackTrace();
							Log.w(TAG, EVENT_USER_LIST_UPDATE + ": failed to parse json");
							return;
						}

						if(l == null)
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

						JSONArray a = (JSONArray) args[0];
						ArrayList<Message> l = null;

						if(a == null)
						{
							Log.w(TAG, EVENT_MESSAGE_LIST_UPDATE + ": json was null");
							return;
						}

						// Parse JSON
						try
						{
							l = Message.parse(a);
						}
						catch(JSONException e)
						{
							e.printStackTrace();
							Log.w(TAG, EVENT_MESSAGE_LIST_UPDATE + ": failed to parse json");
							return;
						}

						if(l == null)
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
						catch(JSONException e)
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
						catch(JSONException e)
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
						catch(JSONException e)
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


		// Finally, ask the server to join the floor and retreive the floor object.
		Sockets.SocketWaiter waiter = new Sockets.SocketWaiter(EVENT_JOIN_FLOOR, EVENT_FLOOR_JOINED);

		JSONObject obj = new JSONObject();
		try
		{
			obj.put(Floor.JSON_ID_TAG, floorId);
			obj.put(LocalUser.JSON_ID_TAG, LocalUser.getCurrentUser().getId());
			obj = waiter.getObj(obj);
			if(obj == null)
			{
				Log.w(TAG, EVENT_FLOOR_JOINED + "Returned null");
				return;
			}
			floor = Floor.parseAdvanced(obj);
		}
		catch(JSONException e)
		{
			e.printStackTrace();
			Log.w(TAG, EVENT_FLOOR_JOINED + ": Unable to parse floor from json");
			return;
		}

		if(floor == null)
		{
			Log.w(TAG, "Floor was null");
			return;
		}

        //Set up the mediaplayer
        Log.d(TAG, "Starting seamless player");
        SeamlessMediaPlayer seamlessMediaPlayer = new SeamlessMediaPlayer(this);

		broadcast(EVENT_FLOOR_JOINED, floor);


		try
		{
			floorLatch.await();
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
			Log.w(TAG, "The floorLatch was interruped, leaving the floor");
		}
	}
}
