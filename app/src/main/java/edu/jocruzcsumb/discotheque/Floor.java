package edu.jocruzcsumb.discotheque;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Tommy on 3/23/2017.
 */

public class Floor implements Parcelable
{
	public static final String TAG = "Floor";

    //regular parse
    public static final String JSON_NAME_TAG = "floor_name";
    public static final String JSON_ID_TAG = "floor_id";
    public static final String JSON_CREATOR_TAG = "creator";

    //Advanced parse
    public static final String JSON_SONGS_TAG = "songs";
    public static final String JSON_USERS_TAG = "users";
    public static final String JSON_MESSAGES_TAG = "messages";
    public static final String JSON_THEME_TAG = "theme";


    private User creator;
    private int id;
    private String name;
    private ArrayList<Message> messages = null;
    private ArrayList<Song> songs = null;
    private ArrayList<User> users = null;
    private Theme theme = null;

    public Floor(int id, String name, User creator)
    {
        this.id = id;
        this.name = name;
        this.creator = creator;
    }

    protected Floor(Parcel in)
    {
        id = in.readInt();
        name = in.readString();
        creator = in.readParcelable(User.class.getClassLoader());
        users = in.createTypedArrayList(User.CREATOR);
        songs = in.createTypedArrayList(Song.CREATOR);
        messages = in.createTypedArrayList(Message.CREATOR);
        theme = in.readParcelable(Theme.class.getClassLoader());
    }

    public static final Creator<Floor> CREATOR = new Creator<Floor>()
    {
        @Override
        public Floor createFromParcel(Parcel in)
        {
            return new Floor(in);
        }

        @Override
        public Floor[] newArray(int size)
        {
            return new Floor[size];
        }
    };

	public static Floor parse(JSONObject jsonFloor) throws JSONException
	{
		Log.d(TAG, "Parse Floor: " + jsonFloor.toString());
		Log.d(TAG, "Parse Floor: has tag floor: " + (jsonFloor.has("floor")?"true":"false"));
		jsonFloor = jsonFloor.getJSONObject("floor");
		Log.d(TAG, "Parse Floor: has tag floor_id: " + (jsonFloor.has("floor_id")?"true":"false"));

		return new Floor(
				jsonFloor.getInt(JSON_ID_TAG),
				jsonFloor.getString(JSON_NAME_TAG),
				User.parse(jsonFloor.getJSONObject(JSON_CREATOR_TAG))
		);
	}

	public static ArrayList<Floor> parse(JSONArray a) throws JSONException
	{
		int arrayLength = a.length();
		ArrayList<Floor> floorList = new ArrayList<Floor>();
		for(int i = 0; i < arrayLength; i++)
		{
			floorList.add(parse(a.getJSONObject(i)));
		}
		return floorList;
	}

    public Floor parseAdvanced(JSONObject jsonFloor) throws JSONException
    {
        Floor f = parse(jsonFloor);
        f.setUsers(User.parse(jsonFloor.getJSONArray(JSON_USERS_TAG)));
        f.setSongs(Song.parse(jsonFloor.getJSONArray(JSON_SONGS_TAG)));
        f.setMessages(Message.parse(jsonFloor.getJSONArray(JSON_MESSAGES_TAG)));
        f.setTheme(Theme.parse(jsonFloor.getJSONObject(JSON_THEME_TAG)));
        return f;
    }

    public ArrayList<Message> getMessages()
    {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages)
    {
        this.messages = messages;
    }

    public ArrayList<Song> getSongs()
    {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs)
    {
        this.songs = songs;
    }

    public ArrayList<User> getUsers()
    {
        return users;
    }

    public void setUsers(ArrayList<User> users)
    {
        this.users = users;
    }

    public void setTheme(Theme theme)
    {
        this.theme = theme;
    }

    public Theme getTheme()
    {
        return theme;
    }

    public String getName()
    {
        return name;
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
        dest.writeString(name);
        dest.writeParcelable(creator, flags);
        dest.writeTypedList(users);
        dest.writeTypedList(songs);
        dest.writeTypedList(messages);
        dest.writeParcelable(theme, flags);
    }

    public int getId()
    {
        return id;
    }
}
