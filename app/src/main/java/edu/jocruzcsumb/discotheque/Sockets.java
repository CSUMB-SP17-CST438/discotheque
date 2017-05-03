package edu.jocruzcsumb.discotheque;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static edu.jocruzcsumb.discotheque.CompileOptions.SOCKET_TIMEOUT;
import static junit.framework.Assert.fail;

public class Sockets
{
	private static final String TAG = "DTK Socket";
	public static boolean socketlock = false;
	private static Socket socket = null;

	public static final String EVENT_PING = "ping";
	public static final String EVENT_PONG = "pong";

	static Thread pingThread = null;
	static Runnable r = new Runnable()
	{
		@Override
		public void run()
		{
			SocketWaiter w = new SocketWaiter(EVENT_PING, EVENT_PONG);
			while(getSocket().connected())
			{
				try
				{
					Thread.sleep(CompileOptions.PING_INTERVAL);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
					Log.wtf(TAG, e.getMessage());
				}
				w.waitForEvent();
			}
			Log.e(TAG, "Socket was disconnected");
		}
	};

	public static void persist()
	{
		Log.i(TAG, "persist (Ping Pong)");
		if(pingThread == null)
		{
			(pingThread = new Thread(r)).start();
		}
	}


	public static boolean waitForConnect()
	{
		Log.i(TAG, "Waiting for server to connect");
		SocketWaiter w = new SocketWaiter(Socket.EVENT_CONNECT);
		boolean b = w.waitForEvent();
		if (b)
		{
			Log.i(TAG, "Connected");
		}
		else
		{
			Log.e(TAG, "Could not connect to server");
		}
		return b;
	}

	public static void clearSocket()
	{
		if (socket == null)
		{
			socket.disconnect();
			socket.close();
		}
		socket = null;
	}

	public static Socket getSocket()
	{
		while (socketlock)
		{
			try
			{
				Thread.sleep(1);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		socketlock = true;
		if (socket != null)
		{
			if (!socket.connected())
			{
				Log.wtf(TAG, "the main socket was disconnected");
				socket = null;
				createSocket();
			}
			socketlock = false;
			return socket;
		}
		createSocket();
		socketlock = false;
		return socket;
	}

	private static Socket createSocket()
	{
		try
		{
			socket = IO.socket(CompileOptions.SERVER_URL);
			socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener()
			{
				@Override
				public void call(Object... args)
				{

					if (!socket.connected())
					{
						while (socketlock)
						{
							try
							{
								Thread.sleep(1);
							}
							catch (InterruptedException e)
							{
								e.printStackTrace();
							}
						}
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
		return socket;
	}

	public static class SocketWaiter implements Emitter.Listener
	{
		private boolean success;
		private CountDownLatch socketLatch;
		private String signal, event;
		private JSONObject json;
		private JSONArray jsonArray;
		private Mode mode;
		private boolean create_socket = false;

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
			Socket s = create_socket ? createSocket() : getSocket();
			s.once(event, this);
			if (signal != null)
			{
				Log.i(TAG, "Sending event: " + signal);
				getSocket().emit(signal);
			}
			try
			{
				socketLatch.await(SOCKET_TIMEOUT, TimeUnit.SECONDS);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			return success;
		}

		public JSONObject getObj(JSONObject params)
		{
			mode = Mode.OBJECT;
			json = null;
			socketLatch = new CountDownLatch(1);
			success = false;
			getSocket().once(event, this);
			if (signal != null)
			{
				Log.i(TAG, "Sending socket event: " + signal);
				if (params == null)
				{
					getSocket().emit(signal);
				}
				else
				{
					getSocket().emit(signal, params);
				}
			}

			try
			{
				socketLatch.await(SOCKET_TIMEOUT, TimeUnit.SECONDS);
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
			getSocket().once(event, this);
			if (signal != null)
			{
				Log.i(TAG, "Sending event: " + signal);
				if (params == null)
				{
					getSocket().emit(signal);
				}
				else
				{
					getSocket().emit(signal, params);
				}
			}

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
			Log.i(TAG, "Received event: " + event);
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

		private enum Mode
		{
			ARRAY,
			OBJECT,
			NONE
		}


	}
}
