package edu.jocruzcsumb.discotheque;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import io.socket.emitter.Emitter;

public class ChatRoomActivity extends AppCompatActivity implements View.OnClickListener, Emitter.Listener {
    MediaPlayer mediaPlayer;
    private EditText chatField;
    private UserChatMessage userChatMessage;
    private ArrayList<UserChatMessage> chatList = new ArrayList<UserChatMessage>();
    private RecyclerView mRecyleView;
    private RVChatAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        Button pickSongButton = (Button) findViewById(R.id.pick_song_button);
        Button send_chat_button = (Button) findViewById(R.id.send_button);
        chatField = (EditText) findViewById(R.id.chat_edit_text);
        pickSongButton.setOnClickListener(this);
        send_chat_button.setOnClickListener(this);
        mRecyleView = (RecyclerView) findViewById(R.id.rv2);
        mRecyleView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true); //scrolls to the bottom
        mRecyleView.setLayoutManager(llm);
        Sockets.getSocket().on("song to play", this);
        Sockets.getSocket().on("message added", this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) mediaPlayer.stop();
        Toast.makeText(this, "mediaplayer was destroyed.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.pick_song_button:
                //go to activity
                Intent goToRoom = new Intent(ChatRoomActivity.this, PickSongActivity.class);
                startActivity(goToRoom);

                break;

            case R.id.send_button:
                chat();

                break;
        }
    }

    public void chat() {
        String chatMessage = chatField.getText().toString();
        chatField.setText("");
        Log.d("chatMessage", chatMessage);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("floor", 1); //floor_id
            jsonObject.put("from", 3); //member_id
            jsonObject.put("message", chatMessage);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Sockets.getSocket().emit("new message", jsonObject);

    }

    @Override
    public void call(Object... args) {
        final JSONObject jsonSong = (JSONObject) args[0];
        if (jsonSong.has("floor_messages")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d("Discotheque", "new song to play: " + jsonSong.toString());
                    try {
                        JSONArray jsonArray = jsonSong.getJSONArray("floor_messages");
                        Log.d("JsonArray: ", jsonArray.toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject chat = jsonArray.getJSONObject(i);
                            JSONObject member = chat.getJSONObject("member");
                            userChatMessage = new UserChatMessage();
                            userChatMessage.setFirstName(member.getString("member_FName"));
                            userChatMessage.setLastName(member.getString("member_LName"));
                            userChatMessage.setPhoto(member.getString("member_img_url"));
                            userChatMessage.setChatMessage(chat.getString("text"));
                            userChatMessage.setPub_time(chat.getString("pubTime"));

                            chatList.add(userChatMessage);
                        }
                    } catch (JSONException e) {
                        e.getStackTrace();
                    }

                    mAdapter = new RVChatAdapter(ChatRoomActivity.this, chatList);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mRecyleView.setAdapter(mAdapter);
                        }
                    });
                }


            }).start();
        } else {
            String url = null;
            String startTime = null;
            try {
                url = jsonSong.get("stream_url").toString();
                startTime = jsonSong.get("start_time").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("Discotheque", "Streaming song - url: " + url);
            if (mediaPlayer != null) mediaPlayer.stop();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                Log.d("Discotheque", "song url chosen: " + url);
                mediaPlayer.setDataSource(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                mediaPlayer.prepare(); // might take long! (for buffering, etc)
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();
            double duration = mediaPlayer.getDuration();
            Log.d("duration is ", String.valueOf(duration));
        }
    }
}
