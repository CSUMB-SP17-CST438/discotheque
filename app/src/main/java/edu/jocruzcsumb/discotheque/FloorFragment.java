package edu.jocruzcsumb.discotheque;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;

import static junit.framework.Assert.fail;

/**
 * Created by carsen on 4/11/17.
 */

public abstract class FloorFragment extends Fragment
{
	private static final String TAG = "FloorFragment";
	protected static Floor floor = null;
	private FloorListener listener;

	public void hideKeyboard()
	{
		Activity a = getActivity();
		if (a == null)
		{
			Log.d(TAG, "getActivity returned null");
			return;
		}
		View view = a.getCurrentFocus();
		if (view != null)
		{
			InputMethodManager imm = (InputMethodManager) a.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	public void start(String tag)
	{
		listener = new FloorListener(getFilter(), getContext(), tag)
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
		hideKeyboard();
	}

	protected IntentFilter getFilter()
	{
		return FloorListener.getDefaultFilter();
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		listener.stop();

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

	protected boolean findFloor()
	{
		return findFloor(getActivity());
	}

	protected boolean findFloor(Activity a)
	{
		return findFloor((FloorActivity) a);
	}

	protected boolean findFloor(FloorActivity a)
	{
		if (a == null)
		{
			Log.e(TAG, "Cant find floor, FloorActivity was null");
			fail();
		}
		floor = a.floor;
		if (floor == null)
		{
			Log.i(TAG, "Floor was null");
			return false;
		}
		return true;
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
