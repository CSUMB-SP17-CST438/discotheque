package edu.jocruzcsumb.discotheque;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.socket.emitter.Emitter;

public class ChatRoomActivity extends AppCompatActivity implements View.OnClickListener, Emitter.Listener
{
	MediaPlayer mediaPlayer;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_room);
		Button pickSongButton = (Button) findViewById(R.id.pick_song_button);
		pickSongButton.setOnClickListener(this);

		Sockets.getSocket().on("song to play", this);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if(mediaPlayer != null) mediaPlayer.stop();
	}

	@Override
	public void onClick(View view)
	{
		switch(view.getId())
		{

			case R.id.pick_song_button:
				//go to activity
				Intent goToRoom = new Intent(ChatRoomActivity.this, PickSongActivity.class);
				startActivity(goToRoom);

				break;
		}
	}

	@Override
	public void call(Object... args)
	{
		JSONObject jsonSong = (JSONObject) args[0];
		Log.d("Discotheque","new song to play: "+jsonSong.toString());

		String url = null;
		String startTime = null;
		try
		{
			url = jsonSong.get("stream_url").toString();
			startTime = jsonSong.get("start_time").toString();
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
		Log.d("Discotheque", "Streaming song - url: " + url);
		if(mediaPlayer != null) mediaPlayer.stop();
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try
		{
			Log.d("Discotheque","song url chosen: "+url);
			mediaPlayer.setDataSource(url);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		try
		{
			mediaPlayer.prepare(); // might take long! (for buffering, etc)
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		mediaPlayer.start();
	}
}
