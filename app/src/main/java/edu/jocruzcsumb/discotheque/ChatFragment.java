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
    private ChatFragment.ChatAdapter chatAdapter;
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
