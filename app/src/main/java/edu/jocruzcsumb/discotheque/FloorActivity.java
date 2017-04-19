package edu.jocruzcsumb.discotheque;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static edu.jocruzcsumb.discotheque.FloorService.EVENT_FLOOR_JOINED;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_GET_FLOOR;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_LEAVE_FLOOR;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_MESSAGE_ADD;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_MESSAGE_LIST_UPDATE;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_SONG_LIST_UPDATE;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_USER_ADD;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_USER_LIST_UPDATE;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_USER_REMOVE;
import static edu.jocruzcsumb.discotheque.SeamlessMediaPlayer.EVENT_SONG_STARTED;
import static edu.jocruzcsumb.discotheque.SeamlessMediaPlayer.EVENT_SONG_STOPPED;

public class FloorActivity extends AppCompatActivity
{

	private static final String TAG = "FloorActivity";

	public Floor floor = null;
    private ImageView albumCoverView;
    private TextView songInfoView;


	// EVENTS are recieved here.
    // IDK whatever dumbass decided to make BroadcastReciever a class and not an interface,
    // but fuck them
	BroadcastReceiver r = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
            Log.d(TAG, "onRecieve: " + intent.getAction());
			switch(intent.getAction())
			{
                case EVENT_SONG_STARTED:
                    final Song s = intent.getParcelableExtra(EVENT_SONG_STARTED);
                    Log.d(TAG, EVENT_SONG_STARTED + ": " + s.getName() + " - " + s.getArtist());
                    //TODO: Update the UI
                    FloorActivity.this.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            songInfoView.setText((s.getName() + " - " + s.getArtist()));
                        }
                    });

                    break;
                case EVENT_SONG_STOPPED:
                    Song x = intent.getParcelableExtra(EVENT_SONG_STOPPED);

                    //TODO: Update the UI


                    break;
                case EVENT_FLOOR_JOINED:
                    floor = intent.getParcelableExtra(EVENT_FLOOR_JOINED);
                    //TODO: Update the UI


                    break;
				case EVENT_SONG_LIST_UPDATE:
					ArrayList<Song> songs = intent.getParcelableArrayListExtra(EVENT_SONG_LIST_UPDATE);
					if(floor != null) floor.setSongs(songs);
					//TODO: Update the UI


					break;
				case EVENT_USER_LIST_UPDATE:
					ArrayList<User> users = intent.getParcelableArrayListExtra(EVENT_USER_LIST_UPDATE);
                    if(floor != null) floor.setUsers(users);
					//TODO: Update the UI


					break;
				case EVENT_MESSAGE_LIST_UPDATE:
					ArrayList<Message> messages = intent.getParcelableArrayListExtra(EVENT_MESSAGE_LIST_UPDATE);
                    if(floor != null) floor.setMessages(messages);
					//TODO: Update the UI


					break;
				case EVENT_MESSAGE_ADD:
					Message m = intent.getParcelableExtra(EVENT_MESSAGE_ADD);
                    if(floor != null)
                        floor.getMessages()
							.add(m);
					//TODO: Update the UI


					break;
				case EVENT_USER_ADD:
					User u = intent.getParcelableExtra(EVENT_USER_ADD);
                    if(floor != null)
                        floor.getUsers()
							.add(u);
					//TODO: Update the UI


					break;
				case EVENT_USER_REMOVE:
					User r = intent.getParcelableExtra(EVENT_USER_REMOVE);
                    if(floor != null)
                        floor.getUsers()
							.remove(r);
					//TODO: Update the UI


					break;
			}
		}
	};
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link FragmentPagerAdapter} derivative, which will keep every
	 * loaded fragment in memory. If this becomes too memory intensive, it
	 * may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	private SectionsPagerAdapter mSectionsPagerAdapter;
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	private ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_floor);

		// This tells the activity what LocalBroadcast Events to listen for
		IntentFilter f = new IntentFilter();
		f.addAction(EVENT_FLOOR_JOINED);
		f.addAction(EVENT_SONG_LIST_UPDATE);
		f.addAction(EVENT_USER_LIST_UPDATE);
		f.addAction(EVENT_MESSAGE_LIST_UPDATE);
		f.addAction(EVENT_USER_ADD);
		f.addAction(EVENT_USER_REMOVE);
		f.addAction(EVENT_MESSAGE_ADD);
		f.addAction(SeamlessMediaPlayer.EVENT_SONG_STARTED);

		// Set the activity to listen for app broadcasts with the above filter
		LocalBroadcastManager.getInstance(getApplicationContext())
				.registerReceiver(r, f);
        broadcast(EVENT_GET_FLOOR);

		// Start the floor service
		Intent i = getIntent();
		int floorId = i.getIntExtra(Floor.TAG, 0);
		if(floorId == 0)
		{
			Log.w(TAG, "No floor was passed to this activity, aborting...");
			finish();
		}
		else
		{
			FloorService.joinFloor(this, floorId);
		}

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.container);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
		tabLayout.setupWithViewPager(mViewPager);

        albumCoverView = (ImageView) findViewById(R.id.song_artwork);
        songInfoView = (TextView) findViewById(R.id.song_title_text);

	}

    @Override
    public void onBackPressed()
    {
        broadcast(EVENT_LEAVE_FLOOR);
        super.onBackPressed();
    }

	// EVENTS are broadcasted here
    private void broadcast(String event)
    {
        Intent k = new Intent(event);
        broadcast(k);
    }

	private void broadcast(String event, Parcelable params)
	{
		Intent k = new Intent(event);
		k.putExtra(event, params);
		broadcast(k);
	}

	private void broadcast(Intent k)
	{
		LocalBroadcastManager.getInstance(getApplicationContext())
				.sendBroadcast(k);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_floor, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

        switch(id)
        {
            case R.id.action_settings:
                break;
            case R.id.action_leave_floor:
                broadcast(EVENT_LEAVE_FLOOR);
                finish();
                break;
        }

		return super.onOptionsItemSelected(item);
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter
	{

		public SectionsPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int position)
		{
			switch(position)
			{
				case 0:
					return ChatFragment.newInstance();
				case 1:
					return SongFragment.newInstance();
				case 2:
					return UserFragment.newInstance();
			}
			return null;
		}

		@Override
		public int getCount()
		{
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			switch(position)
			{
				case 0:
					return getString(R.string.chat_fragment_title);
				case 1:
					return getString(R.string.song_fragment_title);
				case 2:
					return getString(R.string.user_fragment_title);
			}
			return null;
		}
	}
}
