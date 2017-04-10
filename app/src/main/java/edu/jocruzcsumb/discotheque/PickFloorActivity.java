package edu.jocruzcsumb.discotheque;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;



public class PickFloorActivity extends AppCompatActivity implements View.OnClickListener
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_room);

		//TODO: Get the list of rooms from server
		//Sockets.SocketWaiter waiter = new Sockets.SocketWaiter("get floors", "floor list");
				//This waits for the jsonArray to get back
				//jsonArray = waiter.getArray(obj);




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
				Intent k = new Intent(PickFloorActivity.this, FloorActivity.class);

				k.putExtra(Floor.TAG, new Long(0));

				startActivity(k);

				break;
		}
	}
}
