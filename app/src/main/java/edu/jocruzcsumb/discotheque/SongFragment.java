package edu.jocruzcsumb.discotheque;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;


import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static edu.jocruzcsumb.discotheque.FloorService.EVENT_GET_FLOOR;


public class SongFragment extends Fragment
{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String TAG = "SongFragment";
    private static final String ARG_FLOOR_ID = "section_number";
    //private ChatFragment.ChatAdapter chatAdapter;
    private RecyclerView recyclerView;
    private SongAdapter songAdapter;
    private int floorId;

    private static ArrayList<Song> songs = null;
    // EVENTS are recieved here.
    BroadcastReceiver r = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.d(TAG, "onRecieve");
            Log.d(TAG, "intent.getAction() = " + intent.getAction());

            if (intent.getAction()
                    .equals(FloorService.EVENT_FLOOR_JOINED))
            {
                Floor floor = intent.getParcelableExtra(FloorService.EVENT_FLOOR_JOINED);
                floorId = floor.getId();
                songs = floor.getSongs();
            }
            else
            {
                songs = intent.getParcelableArrayListExtra(FloorService.EVENT_SONG_LIST_UPDATE);
            }
            //TODO: Update the UI

            songAdapter = new SongAdapter(getActivity(), songs, new CustomItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Song song = songs.get(position);
                    //TODO: Broadcast song picked.
                }
            });
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    recyclerView.setAdapter(songAdapter);

                }
            });
        }
    };

    public SongFragment()
    {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SongFragment newInstance()
    {
        SongFragment fragment = new SongFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // This tells the activity what LocalBroadcast Events to listen for
        IntentFilter f = new IntentFilter();
        f.addAction(FloorService.EVENT_SONG_LIST_UPDATE);

        // Set the activity to listen for app broadcasts with the above filter
        LocalBroadcastManager m = LocalBroadcastManager.getInstance(this.getContext());
        m.registerReceiver(r, f);
        m.sendBroadcast(new Intent(EVENT_GET_FLOOR));

        View rootView = inflater.inflate(R.layout.fragment_song, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(llm);
        return rootView;
    }

    public class SongAdapter extends RecyclerView.Adapter<SongFragment.SongAdapter.SongViewHolder>
    {
        Context mContext;
        ArrayList<Song> songs = null;
        CustomItemClickListener listener;


        SongAdapter(Context mContext, ArrayList<Song> songs,CustomItemClickListener listener) {
            this.songs = songs;
            this.mContext = mContext;
            this.listener = listener;
        }

        public int getItemCount()
        {
            return songs.size();
        }

        @Override
        public SongFragment.SongAdapter.SongViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
        {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_song_list_view, viewGroup, false);
            final SongAdapter.SongViewHolder svh = new SongAdapter.SongViewHolder(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v, svh.getAdapterPosition());
                }
            });

            return svh;

        }

        @Override
        public void onBindViewHolder(SongFragment.SongAdapter.SongViewHolder songViewHolder, int i)
        {
            songViewHolder.songName.setText(songs.get(i).getName());
            songViewHolder.name.setText(songs.get(i).getArtist());
            if (!songs.get(i).getArtworkUrl().equals("null")) {
                Picasso.with(mContext).load(songs.get(i).getArtworkUrl()).into(songViewHolder.image);

            }
            else{
                songViewHolder.image.setImageResource(R.drawable.ic_launcher);
            }

        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView)
        {
            super.onAttachedToRecyclerView(recyclerView);
        }

        public class SongViewHolder extends RecyclerView.ViewHolder
        {
            CardView cv;
            TextView songName;
            TextView name;
            ImageView image;

            SongViewHolder(View itemView)
            {
                super(itemView);
                cv = (CardView) itemView.findViewById(R.id.cv);
                songName = (TextView) itemView.findViewById(R.id.song);
                name = (TextView) itemView.findViewById(R.id.artist);
                image = (ImageView) itemView.findViewById(R.id.photo);
            }


        }
    }





}

