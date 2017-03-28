package edu.jocruzcsumb.discotheque;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class JoinRoomActivity extends AppCompatActivity implements View.OnClickListener
{
	private ListView listView;
	private Room floorRoom;
	private Sockets socket = new Sockets();
	private SongList songList = new SongList();
	private JSONArray jsonArray;
	private CountDownLatch socketLatch;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_room);

		Button room = (Button) findViewById(R.id.TEMP_go_to_room);
		room.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{

			case R.id.TEMP_go_to_room:
				//go to activity
				Intent goToRoom = new Intent(JoinRoomActivity.this, ChatRoomActivity.class);
				startActivity(goToRoom);

				break;
		}
	}
}
