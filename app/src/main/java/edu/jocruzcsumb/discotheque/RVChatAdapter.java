package edu.jocruzcsumb.discotheque;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;


/**
 * Created by Admin on 3/31/2017.
 */

public class RVChatAdapter extends RecyclerView.Adapter<RVChatAdapter.ChatViewHolder> {

    ArrayList<UserChatMessage> userChatList = new ArrayList<UserChatMessage>();
    Context mContext;

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView message;
        TextView name;
        TextView time;
        ImageView image;

        ChatViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.chatCardView);
            name = (TextView) itemView.findViewById(R.id.sender);
            message = (TextView) itemView.findViewById(R.id.chat);
            image = (ImageView) itemView.findViewById(R.id.chatPhoto);
            time = (TextView) itemView.findViewById(R.id.pubTime);
        }
    }

    RVChatAdapter(Context mContext, ArrayList<UserChatMessage> userChatList) {
        this.userChatList = userChatList;
        this.mContext = mContext;
    }

    public int getItemCount() {
        return userChatList.size();
    }


    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_chat_list, viewGroup, false);
        ChatViewHolder svh = new ChatViewHolder(v);

        return svh;


    }

    @Override
    public void onBindViewHolder(ChatViewHolder chatViewHolder, int i) {
        chatViewHolder.name.setText(userChatList.get(i).getFirstName() + " " + userChatList.get(i).getLastName());
        chatViewHolder.message.setText(userChatList.get(i).getChatMessage());
       // chatViewHolder.time.setText(userChatList.get(i).getPub_time());
//        if (!userChatList.get(i).getPhoto().equals("null")) {
//            Picasso.with(mContext).load(songList.getSong(i).getArtworkUrl()).into(chatViewHolder.image);
//
//        }
//        else{
            chatViewHolder.image.setImageResource(R.drawable.ic_launcher);
        //}
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

    }
}
