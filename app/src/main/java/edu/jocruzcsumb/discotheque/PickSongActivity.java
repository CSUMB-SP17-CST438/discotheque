package edu.jocruzcsumb.discotheque;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class PickSongActivity extends AppCompatActivity {

	private SongList songList = new SongList();
	private JSONArray jsonArray = null;
	private RecyclerView mRecyleView;
	private RVAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_song);

        //used to change to the current song
        TextView roomTitle = (TextView) findViewById(R.id.lister_rooms_title);
//        TextView CurrentSong = (TextView) findViewById(R.id.curr_song_name_textview);
//        TextView currentArtist = (TextView) findViewById(R.id.curr_artist_textiew);
//        final ListView songListView = (ListView) findViewById(R.id.song_listview);
		mRecyleView = (RecyclerView) findViewById(R.id.rv);
		mRecyleView.setHasFixedSize(true);
		LinearLayoutManager llm = new LinearLayoutManager(this);
		mRecyleView.setLayoutManager(llm);

		new Thread(new Runnable()
        {
            @Override
            public void run()
            {
				Log.d("Discotheque","starting socket thread");
				Sockets.getSocket().emit("join room", "pls");
                Sockets.SocketWaiter waiter = new Sockets.SocketWaiter("get songs", "song list");
				JSONObject obj = new JSONObject();
				try
				{
					obj.put("genre", "punk");
				}
				catch(JSONException e)
				{
					e.printStackTrace();
				}
				//This waits for the jsonArray to get back
                jsonArray = waiter.getArray(obj);

				if (jsonArray != null)
				{
					try
					{
						Log.d("json array", jsonArray.toString());
						int arrayLength = jsonArray.length();
						for (int i = 0; i < arrayLength; i++)
						{
							JSONObject object = jsonArray.getJSONObject(i);
							Song song = new Song();
							song.setSongName(object.getString("title"));
							song.setArtist(object.getString("creator_user"));
							song.setSong_uri(object.getString("stream_url"));
							song.setPhoto_link(object.getString("artwork"));
							Log.d("Discotheque","song: "+song.getSongName());
							songList.addSong(song);
						}
					}
					catch(JSONException e)
					{
						e.printStackTrace();
					}
					//final ListView listView = songListView;
					//songList = socket.getSongList("punk");


					//button reference to widgets
//					String[] data = new String[songList.size()];
//					for(int i = 0; i < songList.size(); i++)
//					{
//						Song song = songList.getSong(i);
//						data[i] = song.getArtist();
//
//					}

					//final ArrayAdapter<String> adapter = new ArrayAdapter<String>(PickSongActivity.this, android.R.layout.simple_list_item_1, data);
					songList.sortList();
					mAdapter = new RVAdapter(PickSongActivity.this, songList, new CustomItemClickListener() {
						@Override
						public void onItemClick(View v, int position) {
							JSONObject obj = new JSONObject();
							JSONObject tempObject = new JSONObject();
							//Toast.makeText(PickSongActivity.this, position, Toast.LENGTH_SHORT).show();
							Song song = songList.getSong(position);
							try
							{
								for(int i = 0; i < jsonArray.length(); i++){
									tempObject = jsonArray.getJSONObject(i);
									if(tempObject.getString("title").equals(song.getSongName())){
										break;
									}
								}
								obj.put("song", tempObject);

							}
							catch(JSONException e)
							{
								e.printStackTrace();
							}
							Sockets.getSocket().emit("song picked",obj);
							finish();

						}
					});
					runOnUiThread(new Runnable() {
						@Override
						public void run() {

							//listView.setAdapter(adapter);
							mRecyleView.setAdapter(mAdapter);

						}
					});

					//listView.setOnItemClickListener(new ListClickHandler());
//					listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
//					{
//
//						@Override
//						public void onItemClick(AdapterView<?> parent, View view, int pos, long id)
//						{
//							Toast.makeText(PickSongActivity.this, pos + " " + id, Toast.LENGTH_SHORT).show();
//							JSONObject obj = new JSONObject();
//							try
//							{
//								obj.put("song", jsonArray.get(pos));
//							}
//							catch(JSONException e)
//							{
//								e.printStackTrace();
//							}
//							Sockets.getSocket().emit("song picked",obj);
//							finish();
//						}
//
//					});

				}
				else
				{
					Log.d("Discotheque", "JSON was null");
				}
            }
        }).start();
    }
}
