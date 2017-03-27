package edu.jocruzcsumb.discotheque;

import android.content.Intent;
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

import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class JoinRoomActivity extends AppCompatActivity implements View.OnClickListener, Emitter.Listener {
    private ListView listView;
    private Room floorRoom;
    private Sockets socket = new Sockets();
    private SongList songList = new SongList();
    private JSONArray jsonArray;
    private CountDownLatch socketLatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_join_room);


        try {
            //"https://disco-theque.herokuapp.com"
            Socket socket = IO.socket("http://10.11.154.239");
            socket.connect();
            JSONObject obj = new JSONObject();
            obj.put("genre", "punk");
            socket.emit("join room", "pls");
            socket.emit("get songs", obj);
            socketLatch = new CountDownLatch(1);

            jsonArray = null;
            socket.on("song list", this);
            socketLatch.await(8L, TimeUnit.SECONDS);
            Log.d("json array == null", (jsonArray == null)?"true": "false");
            if (jsonArray != null) {
                Log.d("json array.....after", jsonArray.toString());
                int arrayLength = jsonArray.length();
                for (int i = 0; i < arrayLength; i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    Song song = new Song();
                    song.setSongName(object.getString("title"));
                    song.setArtist(object.getString("creator_user"));
                    song.setSong_uri(object.getString("stream_url"));
                    songList.addSong(song);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();

        }  catch (URISyntaxException e) {
            throw new RuntimeException(e);

        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }




            listView = (ListView) findViewById(R.id.room_listview);
            //songList = socket.getSongList("punk");
            if(songList.size() == 0){
                Toast.makeText(this, "list is zero", Toast.LENGTH_SHORT).show();
            }



            //button reference to widgets
            String [] data = new String[songList.size()];
            for(int i = 0; i < songList.size(); i++){
                Song song = songList.getSong(i);
                data[i] = song.getArtist();

            }
            Button room = (Button) findViewById(R.id.TEMP_go_to_room);

            //setting listeners
            room.setOnClickListener(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, data);
        listView.setAdapter(adapter);
        //listView.setOnItemClickListener(new ListClickHandler());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                Toast.makeText(JoinRoomActivity.this, pos + " " + id, Toast.LENGTH_SHORT).show();
            }

        });
    }




    @Override
    public void call(Object... args) {
        jsonArray = (JSONArray) args[0];
        //JSONObject obj = (JSONObject)args[0];


        Log.d("returned data*****:", jsonArray.toString());
        socketLatch.countDown();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.TEMP_go_to_room:
                //go to activity
                Intent goToRoom = new Intent(JoinRoomActivity.this, ChatRoomActivity.class);
                startActivity(goToRoom);

                break;
        }
    }
}
