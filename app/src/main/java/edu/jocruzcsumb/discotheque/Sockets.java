package edu.jocruzcsumb.discotheque;

/**
 * Created by jcrzr on 3/25/2017.
 */

import org.json.JSONObject;
import org.json.JSONException;
import io.socket.client.Socket;
//import edu.jocruzcsumb.discotheque.Socket;
import io.socket.emitter.Emitter;
import io.socket.client.IO;
import java.net.URISyntaxException;



/**
 * Created by Admin on 3/25/2017.
 */

public class Sockets {
    public static void main(String[] args) {
        try {
            Socket socket = IO.socket("https://disco-theque.herokuapp.com/");
            JSONObject obj = new JSONObject();
            obj.put("genre", "punk");
            //socket.emit("connected");
            socket.on("song list", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject obj = (JSONObject) args[0];
                    System.out.println(obj);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
