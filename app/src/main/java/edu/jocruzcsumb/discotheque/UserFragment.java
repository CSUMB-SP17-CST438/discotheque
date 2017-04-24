package edu.jocruzcsumb.discotheque;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import static edu.jocruzcsumb.discotheque.FloorService.EVENT_FLOOR_JOINED;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_USER_ADD;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_USER_LIST_UPDATE;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_USER_REMOVE;

/**
 * Created by Peter on 4/9/2017.
 */

public class UserFragment extends FloorFragment
{

	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String TAG = "UserFragment";
	private UserFragment.UserAdapter userAdapter;
	private RecyclerView recyclerView;
	private ImageView userPhoto;
	private TextView username;

	public static UserFragment newInstance()
	{
		UserFragment fragment = new UserFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		start(TAG);
		View rootView = inflater.inflate(R.layout.fragment_user, container, false);
		userPhoto = (ImageView) rootView.findViewById(R.id.userPhoto);
		username = (TextView) rootView.findViewById(R.id.username);
		LinearLayoutManager llm = new LinearLayoutManager(this.getActivity());
		//llm.setStackFromEnd(true); //scrolls to the bottom
		recyclerView = (RecyclerView) rootView.findViewById(R.id.rv3);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(llm);
		if (findFloor())
		{
			updateListUI(floor.getUsers());
		}
		return rootView;
	}

	@Override
	public void onUserListUpdate(ArrayList<User> users)
	{
		updateListUI(users);
	}

	@Override
	public void onUserAdded(User u)
	{
		updateListUI(floor.getUsers());
	}

	@Override
	public void onUserRemoved(User u)
	{
		updateListUI(floor.getUsers());
	}

	public void updateListUI(final ArrayList<User> users)
	{
		// check that we can access the parent activity
		final Activity a = getActivity();
		if (a == null)
		{
			Log.e(TAG, "onSongListUpdate: getActivity returned null, cancelling update");
			return;
		}

		userAdapter = new UserFragment.UserAdapter(a, users, new RecyclerViewListener()
		{
			@Override
			public void onItemClick(View v, int position)
			{
				User user = users.get(position);
				Log.d(TAG, user.toString());
				//TODO: pass user to profile activity
				Intent intent = new Intent(a, ViewProfileActivity.class);
				intent.putExtra("user", user);
				startActivity(intent);
			}

			@Override
			public void onLongItemClick(View v, int position){

			}
		});
		a.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				recyclerView.setAdapter(userAdapter);
			}
		});
	}

	@Override
	public IntentFilter getFilter()
	{
		IntentFilter f = new IntentFilter();
		f.addAction(EVENT_FLOOR_JOINED);
		f.addAction(EVENT_USER_LIST_UPDATE);
		f.addAction(EVENT_USER_ADD);
		f.addAction(EVENT_USER_REMOVE);
		return f;
	}


	public class UserAdapter extends RecyclerView.Adapter<UserFragment.UserAdapter.UserViewHolder>
	{
		Context mContext;
		ArrayList<User> users = null;
		RecyclerViewListener listener;


		UserAdapter(Context mContext, ArrayList<User> users, RecyclerViewListener listener)
		{
			this.users = users;
			this.mContext = mContext;
			this.listener = listener;
		}

		public int getItemCount()
		{
			return users.size();
		}

		@Override
		public UserFragment.UserAdapter.UserViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
		{
			View v = LayoutInflater.from(viewGroup.getContext())
					.inflate(R.layout.list_user, viewGroup, false);
			final UserFragment.UserAdapter.UserViewHolder svh = new UserFragment.UserAdapter.UserViewHolder(v);
			v.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{

					listener.onItemClick(v, svh.getAdapterPosition());

				}
			});


			return svh;
		}

		@Override
		public void onBindViewHolder(UserFragment.UserAdapter.UserViewHolder userViewHolder, int i)
		{
			//TODO set profile picture
			Picasso.with(mContext)
					.load(users.get(i)
							.getPhoto())
					.into(userViewHolder.userPhoto);

			userViewHolder.username.setText(users.get(i)
					.getFirstName() + " " + users.get(i)
					.getLastName());
		}

		@Override
		public void onAttachedToRecyclerView(RecyclerView recyclerView)
		{
			super.onAttachedToRecyclerView(recyclerView);
		}

		public class UserViewHolder extends RecyclerView.ViewHolder
		{
			CardView cv;
			TextView username;
			ImageView userPhoto;

			UserViewHolder(View itemView)
			{
				super(itemView);
				cv = (CardView) itemView.findViewById(R.id.userCardView);
				username = (TextView) itemView.findViewById(R.id.username);
				userPhoto = (ImageView) itemView.findViewById(R.id.userPhoto);


			}
		}


	}
}