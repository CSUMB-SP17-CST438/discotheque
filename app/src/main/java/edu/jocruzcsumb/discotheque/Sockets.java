package edu.jocruzcsumb.discotheque;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.client.IO;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Sockets
{
	private static final String TAG = "DTK Socket";

	private static Socket socket = null;

	// TODO: Set to 0 for live server
	private static final int SELECTED_SERVER = 1	;
	// Append to this list if you want to run a different server :D
	private static final String[] SERVERS = {
			"https://disco-theque.herokuapp.com",
			"http://carsen.ml",
			//"http://INSERT IP",
	};

    public static String getServer()
	{

		return SERVERS[SELECTED_SERVER];
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

	// This is a long operation
	public static boolean login(LocalUser.LoginType loginType, String token)
	{
		//We will emit 'login' and wait for 'login status'
		SocketWaiter loginWaiter = new SocketWaiter("login","login status");
		JSONObject obj = new JSONObject();
		try
		{
			obj.put(LocalUser.getTokenJSONKey(loginType), token);
		}
		catch(JSONException e)
		{
			e.printStackTrace();
			return false;
		}
		// This line of code will send the login message
		// and wait until it recieves login status back
		obj = loginWaiter.getObj(obj);
		
		if(obj == null){
			Log.d(TAG, "login returned null (timeout or other error)");
			return false;
		} else
		{
			int a = 0;
			String
					username = null,
					firstname = null,
					lastname = null,
					email = null,
					photo = null,
					bio = null;
			try
			{
				a = obj.getInt("authorized");
				if(a == 1)
				{
					//TODO: get user info from JSON
	//				username = obj.getString("username");
	//				firstname = obj.getString("firstname");
	//				lastname = obj.getString("lastname");
	//				email = obj.getString("email");
	//				photo = obj.getString("photo");
	//				bio = obj.getString("bio");

					LocalUser u = new LocalUser(loginType, token, username, firstname, lastname, email, photo, bio);
					LocalUser.setCurrentUser(u);
					return true;
				}
				else
				{
					return false;
				}
			}
			catch(JSONException e)
			{
				e.printStackTrace();
				return false;
			}
		}
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

			Log.d(TAG, "Sending socket event: "+signal);

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

			Log.d(TAG, "Sending event: "+signal);

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
			Log.d(TAG, "Received event: "+event);

			if(arrayMode) jsonArray = (JSONArray) args[0];
			else json = (JSONObject) args[0];

			success = true;
			socketLatch.countDown();
	}


    }
}
