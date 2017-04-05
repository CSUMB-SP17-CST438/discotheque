package edu.jocruzcsumb.discotheque;

/**
 * Created by jcrzr on 3/25/2017.
 */

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import io.socket.client.Socket;
//import edu.jocruzcsumb.discotheque.Socket;
import io.socket.emitter.Emitter;
import io.socket.client.IO;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Sockets
{
	private static Socket socket = null;

    private static final boolean DEV_SERVER = false;

    public static String getServer()
	{
		return DEV_SERVER?"http://10.11.154.239":"https://disco-theque.herokuapp.com";
	}

	//Checks to see if the current user is authed
	public static boolean isUserAuthenticated()
	{
		//TODO: AUTH
		return true;
	}

	//Returns the authed user
	public static User getUser()
	{
		//TODO: AUTH
		return null;
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
		private JSONArray jsonArray;
		private boolean arrayMode;
		//event = the event we wait for.
		public SocketWaiter(String event)
		{
			this(null, event);
		}
		//Signal = what to send the server, event = event we wait for.
        public SocketWaiter(String signal, String event)
			{
            success = false;
            this.signal=signal;
            this.event=event;
        }

        public JSONObject getObj(JSONObject params)
        {
			arrayMode = false;
			json = null;
			socketLatch = new CountDownLatch(1);
			success = false;

			Log.d("Discotheque", "Sending socket event: "+signal);

			if(signal != null)
			{
				if(params == null) getSocket().emit(signal);
				else getSocket().emit(signal, params);
			}

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

        public JSONObject getObj()
        {
			return getObj(null);
        }



		public JSONArray getArray(JSONObject params)
		{
			arrayMode = true;
			jsonArray = null;
			socketLatch = new CountDownLatch(1);
			success = false;

			Log.d("Discotheque", "Sending socket event: "+signal);

			if(signal != null)
			{
				if(params == null) getSocket().emit(signal);
				else getSocket().emit(signal, params);
			}

			getSocket().once(event, this);
			try
			{
				//socketLatch.await(TIMEOUT, TimeUnit.SECONDS);
				socketLatch.await();
				if(!success)
					return null;

				return jsonArray;
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
			return null;
		}

		public JSONArray getArray()
		{
			return getArray(null);
		}
		@Override
		public void call(Object... args) {
			Log.d("Discotheque", "Received socket event: "+event);

			if(arrayMode) jsonArray = (JSONArray) args[0];
			else json = (JSONObject) args[0];

			success = true;
			socketLatch.countDown();
		}


    }
}
