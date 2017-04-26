package edu.jocruzcsumb.discotheque;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static junit.framework.Assert.fail;

public class Sockets
{
    private static final String TAG = "DTK Socket";
    // TODO: Set to 0 for live server
    private static final int SELECTED_SERVER = 3;
    // Append to this list if you want to run a different server :D
    private static final String[] SERVERS = {
            "https://disco-theque.herokuapp.com",
            "http://carsen.ml:8080",
            "http://devev-jcrzry.c9users.io:8080",
			"http://testing-jcrzry.c9users.io:8080",
    };
    private static Socket socket = null;

	public static boolean waitForConnect()
	{
		Log.i(TAG, "Waiting for server to connect");
		SocketWaiter w = new SocketWaiter(Socket.EVENT_CONNECT);
		boolean b = w.waitForEvent();
		if(b) Log.i(TAG, "Connected");
		else Log.e(TAG, "Could not connect to server");
		return b;
	}

	public static void clearSocket()
	{
		if(socket == null)
		{
			socket.disconnect();
			socket.close();
		}
		socket = null;
	}

    public static String getServer()
    {
        return SERVERS[SELECTED_SERVER];
    }

    public static boolean socketlock = false;
    public static Socket getSocket()
    {
		while (socketlock) try
		{
			Thread.sleep(1);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		socketlock = true;
		if (socket != null)
        {
            if (!socket.connected())
            {
                Log.wtf(TAG, "the main socket was disconnected");
				fail();
            }
			socketlock = false;
            return socket;
        }
        try
        {
            socket = IO.socket(getServer());
			socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener()
			{
				@Override
				public void call(Object... args)
				{
					while (socketlock) try
					{
						Thread.sleep(1);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					if(!socket.connected())
					{
						socketlock = true;
						socket.connect();
						socketlock = false;
					}
				}
			});
		}
        catch (URISyntaxException e)
        {
			socketlock = false;
            e.printStackTrace();
            fail("Invalid server address bro");
        }
        socket.connect();
		socketlock = false;
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
        private Mode mode;

		private enum Mode
		{
			ARRAY,
			OBJECT,
			NONE
		}


        //event = the event we wait for.
        public SocketWaiter(String event)
        {
            this(null, event);
        }

        //Signal = what to send the server, event = event we wait for.
        public SocketWaiter(String signal, String event)
        {
            success = false;
            this.signal = signal;
            this.event = event;
        }

		public boolean waitForEvent()
		{
			mode = Mode.NONE;
            success = false;
			socketLatch = new CountDownLatch(1);
            getSocket().once(event, this);
			try
			{
				socketLatch.await(TIMEOUT, TimeUnit.SECONDS);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			return  success;
		}

        public JSONObject getObj(JSONObject params)
        {
			mode = Mode.OBJECT;
            json = null;
            socketLatch = new CountDownLatch(1);
            success = false;

            Log.d(TAG, "Sending socket event: " + signal);

            if (signal != null)
            {
                if (params == null)
                {
                    getSocket().emit(signal);
                }
                else
                {
                    getSocket().emit(signal, params);
                }
            }

            getSocket().once(event, this);
            try
            {
                socketLatch.await(TIMEOUT, TimeUnit.SECONDS);
                if (!success)
                {
                    return null;
                }

                return json;
            }
            catch (InterruptedException e)
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
			mode = Mode.ARRAY;
            jsonArray = null;
            socketLatch = new CountDownLatch(1);
            success = false;

            Log.d(TAG, "Sending event: " + signal);

            if (signal != null)
            {
                if (params == null)
                {
                    getSocket().emit(signal);
                }
                else
                {
                    getSocket().emit(signal, params);
                }
            }

            getSocket().once(event, this);
            try
            {
                //socketLatch.await(TIMEOUT, TimeUnit.SECONDS);
                socketLatch.await();
                if (!success)
                {
                    return null;
                }

                return jsonArray;
            }
            catch (InterruptedException e)
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
        public void call(Object... args)
        {
            Log.d(TAG, "Received event: " + event);
			switch (mode)
			{
				case ARRAY:
					jsonArray = (JSONArray) args[0];
					break;
				case OBJECT:
					json = (JSONObject) args[0];
					break;
				case NONE:

			}
            success = true;
            socketLatch.countDown();
        }


    }
}
