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
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static edu.jocruzcsumb.discotheque.FloorService.EVENT_MESSAGE_SEND;

public class FloorActivity extends AppCompatActivity
{

    private static final String TAG = "FloorActivity";

    private Floor floor = null;


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
                case FloorService.EVENT_FLOOR_JOINED:
                    floor = intent.getParcelableExtra(FloorService.EVENT_FLOOR_JOINED);
                    //TODO: Update the UI


                    break;
                case FloorService.EVENT_SONG_LIST_UPDATE:
                    ArrayList<Song> songs = intent.getParcelableArrayListExtra(FloorService.EVENT_SONG_LIST_UPDATE);
                    floor.setSongs(songs);
                    //TODO: Update the UI


                    break;
                case FloorService.EVENT_USER_LIST_UPDATE:
                    ArrayList<User> users = intent.getParcelableArrayListExtra(FloorService.EVENT_USER_LIST_UPDATE);
                    floor.setUsers(users);
                    //TODO: Update the UI


                    break;
                case FloorService.EVENT_MESSAGE_LIST_UPDATE:
                    ArrayList<Message> messages = intent.getParcelableArrayListExtra(FloorService.EVENT_MESSAGE_LIST_UPDATE);
                    floor.setMessages(messages);
                    //TODO: Update the UI


                    break;
                case FloorService.EVENT_MESSAGE_ADD:
                    Message m = intent.getParcelableExtra(FloorService.EVENT_MESSAGE_ADD);
                    floor.getMessages()
                         .add(m);
                    //TODO: Update the UI


                    break;
                case FloorService.EVENT_USER_ADD:
                    User u = intent.getParcelableExtra(FloorService.EVENT_USER_ADD);
                    floor.getUsers()
                         .add(u);
                    //TODO: Update the UI


                    break;
                case FloorService.EVENT_USER_REMOVE:
                    User r = intent.getParcelableExtra(FloorService.EVENT_USER_REMOVE);
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
        f.addAction(FloorService.EVENT_FLOOR_JOINED);
        f.addAction(FloorService.EVENT_SONG_LIST_UPDATE);
        f.addAction(FloorService.EVENT_USER_LIST_UPDATE);
        f.addAction(FloorService.EVENT_MESSAGE_LIST_UPDATE);
        f.addAction(FloorService.EVENT_USER_ADD);
        f.addAction(FloorService.EVENT_USER_REMOVE);
        f.addAction(FloorService.EVENT_MESSAGE_ADD);

        // Set the activity to listen for app broadcasts with the above filter
        LocalBroadcastManager.getInstance(getApplicationContext())
                             .registerReceiver(r, f);

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
     * Chat area fragment yo
     */
    public static class ChatFragment extends Fragment implements View.OnClickListener
    {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_FLOOR_ID = "section_number";
        private ChatAdapter chatAdapter;
        private RecyclerView recyleView;

        private int floorId;

        private static ArrayList<Message> messages = null;
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
                    case FloorService.EVENT_MESSAGE_LIST_UPDATE:
                    case FloorService.EVENT_FLOOR_JOINED:
                        if (intent.getAction()
                                  .equals(FloorService.EVENT_FLOOR_JOINED))
                        {
                            Floor floor = intent.getParcelableExtra(FloorService.EVENT_FLOOR_JOINED);
                            messages = floor.getMessages();
                        }
                        else
                        {
                            messages = intent.getParcelableArrayListExtra(FloorService.EVENT_MESSAGE_LIST_UPDATE);
                        }
                        //TODO: Update the UI


                        break;
                    case FloorService.EVENT_MESSAGE_ADD:
                        Message m = intent.getParcelableExtra(FloorService.EVENT_MESSAGE_ADD);
                        messages.add(m);
                        //TODO: Update the UI


                        break;
                }
            }
        };
        private EditText chatField;

        public ChatFragment()
        {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ChatFragment newInstance(int floorId)
        {
            ChatFragment fragment = new ChatFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_FLOOR_ID, floorId);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            // This tells the activity what LocalBroadcast Events to listen for
            IntentFilter f = new IntentFilter();
            f.addAction(FloorService.EVENT_MESSAGE_LIST_UPDATE);
            f.addAction(FloorService.EVENT_MESSAGE_ADD);
            f.addAction(FloorService.EVENT_FLOOR_JOINED);

            // Set the activity to listen for app broadcasts with the above filter
            LocalBroadcastManager.getInstance(this.getContext())
                                 .registerReceiver(r, f);

            // Get the floorid
            floorId = getArguments().getInt(ARG_FLOOR_ID);

            View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
            Button send_chat_button = (Button) rootView.findViewById(R.id.send_button);
            chatField = (EditText) rootView.findViewById(R.id.chat_edit_text);
            send_chat_button.setOnClickListener(this);
            recyleView = (RecyclerView) rootView.findViewById(R.id.rv2);
            recyleView.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(this.getActivity());
            llm.setStackFromEnd(true); //scrolls to the bottom
            recyleView.setLayoutManager(llm);


            return rootView;
        }

        @Override
        public void onClick(View v)
        {
            String text = chatField.getText().toString();
            Message m = new Message(0,LocalUser.getCurrentUser(), text, floorId, 0);
            Intent k = new Intent(EVENT_MESSAGE_SEND);
            k.putExtra(EVENT_MESSAGE_SEND, m);
            LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).sendBroadcast(k);

            //TODO: Create an object that shows a little loading things, and hide it when the message sent successfully
            chatField.setText("");
        }

        public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder>
        {
            Context mContext;
            ArrayList<Message> messages = null;

            ChatAdapter(Context mContext, ArrayList<Message> messages)
            {
                this.messages = messages;
                this.mContext = mContext;
            }

            public int getItemCount()
            {
                return messages.size();
            }

            @Override
            public ChatViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
            {
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_chat_list, viewGroup, false);
                ChatViewHolder svh = new ChatViewHolder(v);
                return svh;
            }

            @Override
            public void onBindViewHolder(ChatViewHolder chatViewHolder, int i)
            {
                chatViewHolder.name.setText(messages.get(i).getAuthor().getFirstName() + " " + messages.get(i).getAuthor().getLastName());
                chatViewHolder.message.setText(messages.get(i).getText());
                // chatViewHolder.time.setText(userChatList.get(i).getPub_time());
                //if (!userChatList.get(i).getPhoto().equals("null"))
                // {
                //Picasso.with(mContext).load(songList.getSong(i).getArtworkUrl()).into(chatViewHolder.image);
                //}
                //else
                //{
                chatViewHolder.image.setImageResource(R.drawable.ic_launcher);
                //}
            }

            @Override
            public void onAttachedToRecyclerView(RecyclerView recyclerView)
            {
                super.onAttachedToRecyclerView(recyclerView);
            }

            public class ChatViewHolder extends RecyclerView.ViewHolder
            {
                CardView cv;
                TextView message;
                TextView name;
                TextView time;
                ImageView image;

                ChatViewHolder(View itemView)
                {
                    super(itemView);
                    cv = (CardView) itemView.findViewById(R.id.chatCardView);
                    name = (TextView) itemView.findViewById(R.id.sender);
                    message = (TextView) itemView.findViewById(R.id.chat);
                    image = (ImageView) itemView.findViewById(R.id.chatPhoto);
                    time = (TextView) itemView.findViewById(R.id.pubTime);
                }
            }
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
            switch (position)
            {
                case 0:
                    return ChatFragment.newInstance(floor.getId());
                case 1:
                    return null;
                case 2:
                    return null;
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
                    return "Chat";
                case 1:
                    return "Songs";
                case 2:
                    return "Other?";
            }
            return null;
        }
    }
}
