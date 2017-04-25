package edu.jocruzcsumb.discotheque;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

import static edu.jocruzcsumb.discotheque.FloorService.EVENT_FLOOR_JOINED;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_LEAVE_FLOOR;
import static edu.jocruzcsumb.discotheque.SeamlessMediaPlayer.EVENT_SONG_STARTED;
import static edu.jocruzcsumb.discotheque.SeamlessMediaPlayer.EVENT_SONG_STOPPED;

public class FloorActivity extends AppCompatActivity
{

	private static final String TAG = "FloorActivity";
	private static final String CURRENT_TAB_TAG = "current_tab";
	// this means we update 20 times a second
	// it looks pretty smoothe like this
	private static final int SONG_UPDATE_INTERVAL = 50;
	public Floor floor = null;
	private FloorListener listener;
	private ImageView albumCoverView;
	private TextView songInfoView;
    private ImageView backgroundView = null;
	// For the tabs
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	private Song currentSong;
	private SeekBar songBar;
	private Timer songTimer;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_floor);
		setTitle(" ");
		// This tells the activity what LocalBroadcast Events to listen for
		IntentFilter f = new IntentFilter();
		f.addAction(EVENT_FLOOR_JOINED);
//        f.addAction(EVENT_SONG_LIST_UPDATE);
//        f.addAction(EVENT_USER_LIST_UPDATE);
//        f.addAction(EVENT_MESSAGE_LIST_UPDATE);
//        f.addAction(EVENT_USER_ADD);
//        f.addAction(EVENT_USER_REMOVE);
//        f.addAction(EVENT_MESSAGE_ADD);
		f.addAction(EVENT_SONG_STARTED);
		f.addAction(EVENT_SONG_STOPPED);

		listener = new FloorListener(f, this, TAG)
		{
			@Override
			public void onFloorJoined(Floor floor)
			{
				FloorActivity.this.floor = floor;
				FloorActivity.this.runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						findViewById(R.id.loadingPanel).setVisibility(View.GONE);
						setTitle(FloorActivity.this.floor.getName());

						//setting background by according to genre
						backgroundView.setImageResource(genreTypes(FloorActivity.this.floor.getGenre())); //use this to let users background image later
					}
				});

			}

			public void onSongStarted(Song x)
			{
				final Song s = x;
				Log.d(TAG, EVENT_SONG_STARTED + ": " + s.getName() + " - " + s.getArtist());
				FloorActivity.this.runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						setCurrentSong(s);
					}
				});

			}

			public void onSongStopped(Song s)
			{
			}
		};

		// Start the floor service
		Intent i = getIntent();
		int floorId = i.getIntExtra(Floor.TAG, 0);
		if (floorId == 0)
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

		songBar = (SeekBar) findViewById(R.id.song_progress_bar);
		songBar.setEnabled(false);

		int t = 1;
		if (savedInstanceState != null)
		{
			t = savedInstanceState.getInt(CURRENT_TAB_TAG, 1);
		}

		mViewPager.setCurrentItem(t, false);

		TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
		tabLayout.setupWithViewPager(mViewPager);

        albumCoverView = (ImageView) findViewById(R.id.song_artwork);
        songInfoView = (TextView) findViewById(R.id.song_title_text);
        backgroundView = (ImageView) findViewById(R.id.floor_background_picture);


        //setting background by according to genre
        //backgroundView.setImageResource(genreTypes(floor.getGenre())); //use this to let users background image




    }

    private int genreTypes(String genre){
        switch(genre){
            case "reggae":
                return R.drawable.red_cardcover_temp;
            case "soft rock":
                return R.drawable.teal_cardcover_temp;
            default:
                return R.drawable.yellow_cardcover_temp;

        }
    }

	private void setCurrentSong(Song s)
	{
		currentSong = s;
		songInfoView.setText((s.getName() + " - " + s.getArtist()));
		Picasso.with(FloorActivity.this)
			   .load(s.getArtworkUrl())
			   .into(albumCoverView);
		//TODO progressbar
		songBar.setMax(0);
		songBar.setMax(s.getDuration());
		songBar.setProgress((int) (System.currentTimeMillis() - (currentSong.getStartTime() * 1000)));
		if (songTimer != null)
		{
			songTimer.cancel();
		}
		songTimer = new Timer();


		songTimer.scheduleAtFixedRate(new TimerTask()
		{
			@Override
			public void run()
			{
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
				{
					songBar.setProgress(songBar.getProgress() + SONG_UPDATE_INTERVAL, true);
				}
				else
				{
					songBar.setProgress(songBar.getProgress() + SONG_UPDATE_INTERVAL);
				}
			}
		}, 0, SONG_UPDATE_INTERVAL);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState)
	{
		floor = (Floor) savedInstanceState.getParcelable(Floor.TAG);
		if (floor != null)
		{
			findViewById(R.id.loadingPanel).setVisibility(View.GONE);
		}
		setCurrentSong((Song) savedInstanceState.getParcelable(Song.TAG));
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState)
	{
		if (floor != null)
		{
			savedInstanceState.putParcelable(Floor.TAG, floor);
		}
		if (currentSong != null)
		{
			savedInstanceState.putParcelable(Song.TAG, currentSong);
		}
		if (floor != null)
		{
			savedInstanceState.putInt(CURRENT_TAB_TAG, mViewPager.getCurrentItem());
		}
	}

	@Override
	public void onBackPressed()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.confirm_leave_floor)
			   .setCancelable(false)
			   .setPositiveButton(R.string.action_yes, new DialogInterface.OnClickListener()
			   {
				   public void onClick(DialogInterface dialog, int id)
				   {
					   broadcast(EVENT_LEAVE_FLOOR);
					   FloorActivity.this.finish();
				   }
			   })
			   .setNegativeButton(R.string.action_no, new DialogInterface.OnClickListener()
			   {
				   public void onClick(DialogInterface dialog, int id)
				   {
					   dialog.cancel();
				   }
			   });
		AlertDialog alert = builder.create();
		alert.show();
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

		switch (id)
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

	@Override
	protected void onStop()
	{
		super.onStop();
		listener.stop();
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
			switch (position)
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
			switch (position)
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
