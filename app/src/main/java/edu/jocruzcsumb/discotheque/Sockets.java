package edu.jocruzcsumb.discotheque;

/**
 * Created by jcrzr on 3/25/2017.
 */

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import io.socket.client.Socket;
//import edu.jocruzcsumb.discotheque.Socket;
import io.socket.emitter.Emitter;
import io.socket.client.IO;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * Created by Admin on 3/25/2017.
 */

public class Sockets {


    private Socket socket;
    private JSONArray jsonArray;
    private SongList songList = new SongList();

    public Sockets(){

            try {
                //"https://disco-theque.herokuapp.com"
                socket = IO.socket("http://10.11.154.239");
                socket.connect();

            } catch (URISyntaxException e) {
                throw new RuntimeException(e);

            }

    }


    public SongList getSongList(String genre) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("genre", "punk");
            socket.emit("join room", "pls");
            socket.emit("get songs", obj);
            Thread.sleep(1000);
            final CountDownLatch socketLatch = new CountDownLatch(1);


            socket.on("song list", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    jsonArray = (JSONArray) args[0];
                    //JSONObject obj = (JSONObject)args[0];


                    Log.d("returned data*****:", jsonArray.toString());
                    socketLatch.countDown();
                }
            });
            socketLatch.await(8L, TimeUnit.SECONDS);
            //Log.d("json array.....after", jsonArray.toString());
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

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return songList;


    }




}
