package edu.jocruzcsumb.discotheque;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static junit.framework.Assert.fail;

/**
 * Created by Carsen on 4/7/17.
 */

public class Message implements Parcelable
{

	//For JSON parsing
	public static final String JSON_FROM_TAG = "member";
	public static final String JSON_FLOOR_TAG = "floor";
	public static final String JSON_TEXT_TAG = "text";
	public static final String JSON_PUB_TIME_TAG = "pubTime";
	public static final String JSON_ID_TAG = "mess_id";

	public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>()
	{

		// This simply calls our new constructor (typically private) and
		// passes along the unmarshalled `Parcel`, and then returns the new object!
		@Override
		public Message createFromParcel(Parcel in)
		{
			return new Message(in);
		}

		// We just need to copy this and change the type to match our class.
		@Override
		public Message[] newArray(int size)
		{
			return new Message[size];
		}
	};
	private int id = 0;
	private User author = null;
	private String text = null;
	private int floor = 0;
	private long pubTime = 0;

	public Message(int id, User author, String text, int floor, long pubTime)
	{
		this.id = id;
		this.text = text;
		this.author = author;
		this.floor = floor;
		this.pubTime = pubTime;
	}

	private Message(Parcel in)
	{
		id = in.readInt();
		author = in.readParcelable(User.class.getClassLoader());
		text = in.readString();
		pubTime = in.readLong();
		//TODO floor
	}

	public static Message parse(JSONObject jsonMessage) throws JSONException
	{
		User author = User.parse(jsonMessage.getJSONObject(JSON_FROM_TAG));
		String text = jsonMessage.getString(JSON_TEXT_TAG);
		long pubTime = jsonMessage.getLong(JSON_PUB_TIME_TAG);
		int floor = jsonMessage.getInt(JSON_FLOOR_TAG);
		int id = jsonMessage.getInt(JSON_ID_TAG);
		fail("NYI");
		return new Message(id, author, text, floor, pubTime);
	}

	public static ArrayList<Message> parse(JSONArray a) throws JSONException
	{
		int arrayLength = a.length();
		ArrayList<Message> messageList = new ArrayList<Message>();
		for(int i = 0; i < arrayLength; i++)
		{
			messageList.add(Message.parse(a.getJSONObject(i)));
		}
		return messageList;
	}

	public String getText()
	{
		return text;
	}

	public User getAuthor()
	{
		return author;
	}

	public int getId()
	{
		return id;
	}

	public int getFloor()
	{
		return floor;
	}

	public long getPubTime()
	{
		return pubTime;
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeInt(id);
		dest.writeParcelable(author, flags);
		dest.writeString(text);
		dest.writeLong(pubTime);
		//TODO floor
	}
}
