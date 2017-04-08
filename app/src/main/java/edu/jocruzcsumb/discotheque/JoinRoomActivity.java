package edu.jocruzcsumb.discotheque;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;

import java.util.concurrent.CountDownLatch;

public class JoinRoomActivity extends AppCompatActivity implements View.OnClickListener
{
	private ListView listView;
	private Floor floorRoom;
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
