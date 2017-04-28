package edu.jocruzcsumb.discotheque;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static edu.jocruzcsumb.discotheque.FloorService.EVENT_FLOOR_JOINED;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_SONG_LIST_UPDATE;


public class SongFragment extends FloorFragment
{
	private static final String TAG = "SongFragment";
	private RecyclerView recyclerView;
	private SongAdapter songAdapter;

	// Used by activity to create an instance
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
		start(TAG);
		View rootView = inflater.inflate(R.layout.fragment_song, container, false);
		recyclerView = (RecyclerView) rootView.findViewById(R.id.rv);
		recyclerView.setHasFixedSize(true);
		LinearLayoutManager llm = new LinearLayoutManager(this.getActivity());
		recyclerView.setLayoutManager(llm);
		if (findFloor())
		{
			updateListUI(floor.getSongs());
		}
		return rootView;
	}

	@Override
	public void onSongListUpdate(ArrayList<Song> songs)
	{
		updateListUI(songs);
	}

	public void updateListUI(final ArrayList<Song> songs)
	{
		// check that we can access the parent activity
		Activity a = getActivity();
		if (a == null)
		{
			Log.e(TAG, "onSongListUpdate: getActivity returned null, cancelling update");
			return;
		}

		// create the song list
		songAdapter = new SongAdapter(a, songs, new RecyclerViewListener()
		{
			@Override
			public void onItemClick(View v, int position)
			{
				Song song = songs.get(position);
				Log.d(TAG, "song was regular clicked");
				//TODO: Broadcast song picked.
			}

			@Override
			public void onLongItemClick(View v, int position)
			{
				Log.d(TAG, "song was long clicked");
				final Song song = songs.get(position);
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setMessage(R.string.confirm_leave_app)
					   .setCancelable(false)
					   .setPositiveButton(R.string.action_yes, new DialogInterface.OnClickListener()
					   {
						   public void onClick(DialogInterface dialog, int id)
						   {
							   Uri uri = Uri.parse(song.getTrack_permalink()); // missing 'http://' will cause crashed
							   Intent intent = new Intent(Intent.ACTION_VIEW, uri);
							   startActivity(intent);

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
		});

		// update the activity with the new list
		a.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				recyclerView.setAdapter(songAdapter);
			}
		});
	}

	@Override
	public void onSongStarted(Song s)
	{
	}

	@Override
	public void onSongStopped(Song s)
	{
	}

	@Override
	public IntentFilter getFilter()
	{
		IntentFilter f = new IntentFilter();
		f.addAction(EVENT_FLOOR_JOINED);
		f.addAction(EVENT_SONG_LIST_UPDATE);
		return f;
	}

	public class SongAdapter extends RecyclerView.Adapter<SongFragment.SongAdapter.SongViewHolder>
	{
		Context mContext;
		ArrayList<Song> songs = null;
		RecyclerViewListener listener;


		SongAdapter(Context mContext, ArrayList<Song> songs, RecyclerViewListener listener)
		{
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
			View v = LayoutInflater.from(viewGroup.getContext())
								   .inflate(R.layout.list_song, viewGroup, false);
			final SongAdapter.SongViewHolder svh = new SongAdapter.SongViewHolder(v);
			v.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					listener.onItemClick(v, svh.getAdapterPosition());
				}
			});

			v.setOnLongClickListener(new View.OnLongClickListener()
			{
				@Override
				public boolean onLongClick(View v)
				{
					listener.onLongItemClick(v, svh.getAdapterPosition());
					return true;
				}
			});

			return svh;

		}

		@Override
		public void onBindViewHolder(SongFragment.SongAdapter.SongViewHolder songViewHolder, int i)
		{
			songViewHolder.songName.setText(songs.get(i)
												 .getName());
			songViewHolder.name.setText(songs.get(i)
											 .getArtist());
			if (!songs.get(i)
					  .getArtworkUrl()
					  .equals("null"))
			{
				Picasso.with(mContext)
					   .load(songs.get(i)
								  .getArtworkUrl())
					   .into(songViewHolder.image);

			}
			else
			{
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

