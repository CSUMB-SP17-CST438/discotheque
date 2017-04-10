package edu.jocruzcsumb.discotheque;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Peter on 4/9/2017.
 */

public class UserFragment  extends Fragment implements View.OnClickListener {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String TAG = "UserFragment";
    private static final String ARG_FLOOR_ID = "section_number";
    private static ArrayList<User> users = null;
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
                case FloorService.EVENT_GET_USER_LIST:
                //get all users
                    Floor floor = intent.getParcelableExtra(FloorService.EVENT_FLOOR_JOINED);
                    users = floor.getUsers();
                    break;
            }
            else{
                users = intent.getParcelableArrayListExtra(FloorService.EVENT_USER_LIST_UPDATE);
            }
        }
        }
    };
    private UserFragment.UserAdapter userAdapter;
    private RecyclerView recyclerView;
    private int floorId;
    private ImageView userPhoto;
    private TextView username;

    public UserFragment(){

    }
    public static UserFragment newInstance(int floorId){
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_FLOOR_ID, floorId);
        fragment.setArguments(args);
        return fragment;

    }
    @Overide
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        // This tells the activity what LocalBroadcast Events to listen for
        IntentFilter f = new IntentFilter();
        f.addAction(FloorService.EVENT_GET_USER_LIST);

        LocalBroadcastManager.getInstance(this.getContext())
                .registerReceiver(r, f);

        // Get the floorid
        floorId = getArguments().getInt(ARG_FLOOR_ID);

        View rootView = inflater.inflate(R.layout.fragment_user, container, false);
        userPhoto = (ImageView) rootView.findViewById(R.id.userPhoto);
        username = (TextView) rootView.findViewById(R.id.username);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv3);
        recyclerView.setHasFixedSize(true);
        return rootView;


    }
    @Override
    public void onClick(View v)
    {
        //TODO: show profile'

   }
    public class UserAdapter extends RecyclerView.Adapter<UserFragment.UserAdapter.UserViewHolder>
    {
        Context mContext;
        ArrayList<User> users = null;

        UserAdapter(Context mContext, ArrayList<User> users)
        {
            this.users = users;
            this.mContext = mContext;
        }
        public int getItemCount()
        {
            return users.size();
        }

        @Override
        public UserFragment.UserAdapter.UserViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
        {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_user_list, viewGroup, false);
            UserFragment.UserAdapter.UserViewHolder svh = new UserFragment.UserAdapter.UserViewHolder(v);
            return svh;
        }


        @Override
        public void onBindViewHolder(UserFragment.UserAdapter.UserViewHolder userViewHolder, int i)
        {
            userViewHolder.userPhoto.setText(users.get(i).getPhoto())
            userViewHolder.username.setText(users.get(i).getFirstName() + " " + users.get(i).getLastName());
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
                cv = (CardView) itemView.findViewById(R.id.chatCardView);
                username = (TextView) itemView.findViewById(R.id.username);
                userPhoto = (ImageView) itemView.findViewById(R.id.userPhoto);


            }
        }


    }