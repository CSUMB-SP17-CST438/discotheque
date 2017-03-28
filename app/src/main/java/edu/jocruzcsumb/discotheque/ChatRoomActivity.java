package edu.jocruzcsumb.discotheque;

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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ChatRoomActivity extends AppCompatActivity {

	private SongList songList = new SongList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_activity);

        //used to change to the current song
        TextView roomTitle = (TextView) findViewById(R.id.lister_rooms_title);
        TextView CurrentSong = (TextView) findViewById(R.id.curr_song_name_textview);
        TextView currentArtist = (TextView) findViewById(R.id.curr_artist_textiew);
        final ListView songListView = (ListView) findViewById(R.id.song_listview);
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
                JSONArray jsonArray = waiter.getArray(obj);

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
							Log.d("Discotheque","song: "+song.getSongName());
							songList.addSong(song);
						}
					}
					catch(JSONException e)
					{
						e.printStackTrace();
					}
					final ListView listView = songListView;
					//songList = socket.getSongList("punk");
					if(songList.size() == 0)
					{
						Toast.makeText(ChatRoomActivity.this, "list is zero", Toast.LENGTH_SHORT).show();
					}


					//button reference to widgets
					String[] data = new String[songList.size()];
					for(int i = 0; i < songList.size(); i++)
					{
						Song song = songList.getSong(i);
						data[i] = song.getArtist();

					}

					final ArrayAdapter<String> adapter = new ArrayAdapter<String>(ChatRoomActivity.this, android.R.layout.simple_list_item_1, data);

					runOnUiThread(new Runnable() {
						@Override
						public void run() {

							listView.setAdapter(adapter);

						}
					});

					//listView.setOnItemClickListener(new ListClickHandler());
					listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
					{

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int pos, long id)
						{
							Toast.makeText(ChatRoomActivity.this, pos + " " + id, Toast.LENGTH_SHORT).show();
							Song song = songList.getSong(pos);
							String url = song.getSong_uri();
							Log.d("Discotheque", "Streaming song - url: " + url);
							MediaPlayer mediaPlayer = new MediaPlayer();
							mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
							try
							{
								URL u = new URL(url);
								URLConnection conn = u.openConnection();
								InputStream is = conn.getInputStream();
								Log.d("Discotheque", is.toString());

								mediaPlayer.setDataSource(url + "?client_id=\"8c1cf28d0d2834808a2eda6645da717b\"");
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

					});
				}
				else
				{
					Log.d("Discotheque", "JSON was null");
				}
            }
        }).start();
    }
}
