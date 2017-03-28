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

public class Sockets
{
	private static Socket socket = null;

    private static final boolean DEV_SERVER = true;

    public static String getServer()
	{
		return DEV_SERVER?"http://carsen.ml":"https://disco-theque.herokuapp.com";
	}


    public static Socket getSocket()
	{
		if(socket != null ) return socket;
		try
		{
			socket = IO.socket(getServer());
		}
		catch(URISyntaxException e)
		{
			e.printStackTrace();
		}
		socket.connect();
		return socket;
	}

    public static class SocketWaiter implements Emitter.Listener
	{
		private static final long TIMEOUT = 8L;
        private boolean success;
        private CountDownLatch socketLatch;
        private String signal, event;
		private JSONObject json;
        //Signal is what to send the server, event the event we wait for.
        public SocketWaiter(String signal, String event)
        {
            success = false;
            this.signal=signal;
            this.event=event;
        }

        public JSONObject get(JSONObject params)
        {
			json = null;
			socketLatch = new CountDownLatch(1);
			success = false;

			Log.d("Discotheque", "Sending socket event: "+signal);

			if(params == null) getSocket().emit(signal);
			else getSocket().emit(signal, params);

			getSocket().once(event, this);
			try
			{
				socketLatch.await(TIMEOUT, TimeUnit.SECONDS);
				if(!success)
					return null;

				return json;
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
			return null;
		}

        public JSONObject get()
        {
			return get(null);
        }

		@Override
		public void call(Object... args) {
			Log.d("Discotheque", "Received socket event: "+event);
			json = (JSONObject) args[0];
			success = true;
			socketLatch.countDown();
		}


    }
}
