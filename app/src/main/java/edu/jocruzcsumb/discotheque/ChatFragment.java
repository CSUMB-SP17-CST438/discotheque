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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static edu.jocruzcsumb.discotheque.FloorService.EVENT_MESSAGE_SEND;

/**
 * Chat area fragment yo
 */
public class ChatFragment extends Fragment implements View.OnClickListener
{
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String TAG = "ChatFragment";
	private static final String ARG_FLOOR_ID = "section_number";
	private static ArrayList<Message> messages = null;
	// EVENTS are recieved here.
	BroadcastReceiver r = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Log.d(TAG, "onRecieve");
			Log.d(TAG, "intent.getAction() = " + intent.getAction());
			switch(intent.getAction())
			{
				case FloorService.EVENT_MESSAGE_LIST_UPDATE:




						messages = intent.getParcelableArrayListExtra(FloorService.EVENT_MESSAGE_LIST_UPDATE);
						if(messages.size() > 0)floorId = messages.get(0).getFloor();
					//TODO: Update the UI

					addChatTOUI();


					break;
				case FloorService.EVENT_MESSAGE_ADD:
					Message m = intent.getParcelableExtra(FloorService.EVENT_MESSAGE_ADD);
					messages.add(m);
					//TODO: Update the UI
					addChatTOUI();


					break;
			}
		}
	};
	private ChatFragment.ChatAdapter chatAdapter;
	private RecyclerView recyclerView;
	private int floorId;
	private EditText chatField;



	public ChatFragment()
	{
	}

	/**
	 * Returns a new instance of this fragment for the given section
	 * number.
	 */
	public static ChatFragment newInstance()
	{
		ChatFragment fragment = new ChatFragment();
		Bundle args = new Bundle();
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

		// Set the activity to listen for app broadcasts with the above filter
		LocalBroadcastManager.getInstance(this.getContext())
				.registerReceiver(r, f);

		View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
		Button send_chat_button = (Button) rootView.findViewById(R.id.send_button);
		chatField = (EditText) rootView.findViewById(R.id.chat_edit_text);
		send_chat_button.setOnClickListener(this);
		recyclerView = (RecyclerView) rootView.findViewById(R.id.rv2);
		recyclerView.setHasFixedSize(true);
		LinearLayoutManager llm = new LinearLayoutManager(this.getActivity());
		llm.setStackFromEnd(true); //scrolls to the bottom
		recyclerView.setLayoutManager(llm);


		return rootView;
	}

	private void addChatTOUI()
	{
		Log.d(TAG, "addChatTOUI");
		chatAdapter = new ChatAdapter(getActivity(), messages);
		Log.d(TAG, messages.toString());
		getActivity().runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				recyclerView.setAdapter(chatAdapter);
			}
		});

	}

	@Override
	public void onClick(View v)
	{
		String text = chatField.getText().toString();
		Message m = new Message(0, LocalUser.getCurrentUser(), text, floorId, 0);
		Intent k = new Intent(EVENT_MESSAGE_SEND);
		k.putExtra(EVENT_MESSAGE_SEND, m);
		LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).sendBroadcast(k);

		//TODO: Create an object that shows a little loading things, and hide it when the message sent successfully
		chatField.setText("");
	}

	public class ChatAdapter extends RecyclerView.Adapter<ChatFragment.ChatAdapter.ChatViewHolder>
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
		public ChatFragment.ChatAdapter.ChatViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
		{
			View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_chat_list, viewGroup, false);
			ChatFragment.ChatAdapter.ChatViewHolder svh = new ChatFragment.ChatAdapter.ChatViewHolder(v);
			return svh;
		}

		@Override
		public void onBindViewHolder(ChatFragment.ChatAdapter.ChatViewHolder chatViewHolder, int i)
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
