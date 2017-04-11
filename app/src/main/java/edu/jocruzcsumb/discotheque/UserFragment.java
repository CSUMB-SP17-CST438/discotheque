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

import static edu.jocruzcsumb.discotheque.FloorService.EVENT_FLOOR_JOINED;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_GET_FLOOR;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_GET_USER_LIST;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_USER_LIST_UPDATE;

/**
 * Created by Peter on 4/9/2017.
 */

public class UserFragment  extends Fragment implements View.OnClickListener {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String TAG = "UserFragment";
    private static ArrayList<User> users = null;
    // EVENTS are recieved here.
    BroadcastReceiver r = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onRecieve");
            Log.d(TAG, "intent.getAction() = " + intent.getAction());
            if(intent.getAction().equals(EVENT_FLOOR_JOINED))
            {
                    //get all users
                    Floor floor = intent.getParcelableExtra(FloorService.EVENT_FLOOR_JOINED);
                    floorId = floor.getId();
                    users = floor.getUsers();
            }
            else if(intent.getAction().equals(EVENT_USER_LIST_UPDATE))
            {
                users = intent.getParcelableArrayListExtra(EVENT_USER_LIST_UPDATE);
            }

            //TODO PETER UPDATE THE UI WITH THE NEW users ARRAYLIST
            userAdapter = new UserFragment.UserAdapter(getActivity(), users);
            Log.d(TAG, users.toString());
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    recyclerView.setAdapter(userAdapter);
                }
            });


        }

    };
    private UserFragment.UserAdapter userAdapter;
    private RecyclerView recyclerView;
    private int floorId;
    private ImageView userPhoto;
    private TextView username;

    public UserFragment() {

    }

    public static UserFragment newInstance() {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // This tells the activity what LocalBroadcast Events to listen for
        IntentFilter f = new IntentFilter();
        f.addAction(EVENT_GET_USER_LIST);

        LocalBroadcastManager m = LocalBroadcastManager.getInstance(this.getContext());
        m.registerReceiver(r, f);
        m.sendBroadcast(new Intent(EVENT_GET_FLOOR));

        View rootView = inflater.inflate(R.layout.fragment_user, container, false);
        userPhoto = (ImageView) rootView.findViewById(R.id.userPhoto);
        username = (TextView) rootView.findViewById(R.id.username);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv3);
        recyclerView.setHasFixedSize(true);
        return rootView;


    }



    @Override
    public void onClick(View v) {
        //TODO: show profile'

    }

    public class UserAdapter extends RecyclerView.Adapter<UserFragment.UserAdapter.UserViewHolder> {
        Context mContext;
        ArrayList<User> users = null;

        UserAdapter(Context mContext, ArrayList<User> users) {
            this.users = users;
            this.mContext = mContext;
        }

        public int getItemCount() {
            return users.size();
        }

        @Override
        public UserFragment.UserAdapter.UserViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_user, viewGroup, false);
            UserFragment.UserAdapter.UserViewHolder svh = new UserFragment.UserAdapter.UserViewHolder(v);
            return svh;
        }


        @Override
        public void onBindViewHolder(UserFragment.UserAdapter.UserViewHolder userViewHolder, int i) {
            //userViewHolder.userPhoto.setText(users.get(i).getPhoto());
            //TODO set profile picture
            //maybe setImageResource?
            userViewHolder.username.setText(users.get(i).getFirstName() + " " + users.get(i).getLastName());
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        public class UserViewHolder extends RecyclerView.ViewHolder {
            CardView cv;
            TextView username;
            ImageView userPhoto;

            UserViewHolder(View itemView) {
                super(itemView);
                cv = (CardView) itemView.findViewById(R.id.userCardView);
                username = (TextView) itemView.findViewById(R.id.username);
                userPhoto = (ImageView) itemView.findViewById(R.id.userPhoto);


            }
        }


    }
}