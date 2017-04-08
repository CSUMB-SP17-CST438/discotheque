package edu.jocruzcsumb.discotheque;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;

import java.util.ArrayList;

public class FloorActivity extends AppCompatActivity
{

    private static final String TAG = "FloorActivity";
    // EVENTS are recieved here.
    BroadcastReceiver r = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.d(TAG, "onRecieve");
            Log.d(TAG, "intent.getAction() = " + intent.getAction());
            switch (intent.getAction())
            {
                case FloorService.EVENT_SONG_LIST_UPDATE:
                    ArrayList<Song> songs = intent.getParcelableArrayListExtra(FloorService.EVENT_SONG_LIST_UPDATE);

                    break;
                case FloorService.EVENT_USER_LIST_UPDATE:
                    ArrayList<User> users = intent.getParcelableArrayListExtra(FloorService.EVENT_USER_LIST_UPDATE);

                    break;
                case FloorService.EVENT_MESSAGE_LIST_UPDATE:
                    ArrayList<Message> messages = intent.getParcelableArrayListExtra(FloorService.EVENT_MESSAGE_LIST_UPDATE);

                    break;
                case FloorService.EVENT_MESSAGE_ADD:
                    Message m = intent.getParcelableExtra(FloorService.EVENT_MESSAGE_ADD);

                    break;
                case FloorService.EVENT_USER_ADD:
                    User u = intent.getParcelableExtra(FloorService.EVENT_USER_ADD);

                    break;
                case FloorService.EVENT_USER_REMOVE:
                    User r = intent.getParcelableExtra(FloorService.EVENT_USER_REMOVE);

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
        f.addAction(FloorService.EVENT_SONG_LIST_UPDATE);
        f.addAction(FloorService.EVENT_USER_LIST_UPDATE);
        f.addAction(FloorService.EVENT_MESSAGE_LIST_UPDATE);
        f.addAction(FloorService.EVENT_USER_ADD);
        f.addAction(FloorService.EVENT_USER_REMOVE);
        f.addAction(FloorService.EVENT_MESSAGE_ADD);

        // Set the activity to listen for app broadcasts with the above filter
        LocalBroadcastManager.getInstance(getApplicationContext())
                             .registerReceiver(r, f);

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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show();
            }
        });

    }

    // EVENTS are broadcasted here
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment
    {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment()
        {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber)
        {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_floor, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
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
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount()
        {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            switch (position)
            {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }
}
