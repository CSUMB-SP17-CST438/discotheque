package edu.jocruzcsumb.discotheque;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChatRoomActivity extends AppCompatActivity implements View.OnClickListener
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_room);
		Button pickSongButton = (Button) findViewById(R.id.pick_song_button);
		pickSongButton.setOnClickListener(this);
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
}
