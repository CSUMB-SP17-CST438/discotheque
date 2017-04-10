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

public class Song implements Comparable<Song>, Parcelable
{

	public static final String TAG = "Song";

	public static final String JSON_TITLE_TAG = "title";
	public static final String JSON_ARTIST_TAG = "creator_user";
	public static final String JSON_STREAM_URL_TAG = "stream_url";
	public static final String JSON_ARTWORK_TAG = "artwork";
	public static final String JSON_CHOOSEN_BY_TAG = "random";

	public static final String JSON_START_TIME_TAG = "start_time";

	public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>()
	{
		// This simply calls our new constructor (typically private) and
		// passes along the unmarshalled `Parcel`, and then returns the new object!
		@Override
		public Song createFromParcel(Parcel in)
		{
			return new Song(in);
		}

		// We just need to copy this and change the type to match our class.
		@Override
		public Song[] newArray(int size)
		{
			return new Song[size];
		}
	};
	private String title = null;
	private String artist = null;
	private String streamUrl = null;
	private String artworkUrl = null;
	// This tells us whether the song was picked by a user, or chosenBy by the server.
	// If a song is picked by a user, it should be moved just above the first chosenBy song in the list
	private String chosenBy;
	private long startTime;

	public Song(String title, String artist, String streamUrl, String artworkUrl, String chosenBy, long startTime)
	{
		this.title = title;
		this.artist = artist;
		this.streamUrl = streamUrl;
		this.artworkUrl = artworkUrl;
		this.chosenBy = chosenBy;
		this.startTime = startTime;
	}

	private Song(Parcel in)
	{
		title = in.readString();
		artist = in.readString();
		streamUrl = in.readString();
		artworkUrl = in.readString();
		chosenBy = in.readString();
		startTime = in.readLong();
	}

	public static Song parse(JSONObject jsonSong) throws JSONException
	{
		Log.d(TAG, jsonSong.toString());
		long s = (jsonSong.has(JSON_START_TIME_TAG)? jsonSong.getLong(JSON_START_TIME_TAG) : 0);
		return new Song(
				jsonSong.getString(JSON_TITLE_TAG),
				jsonSong.getString(JSON_ARTIST_TAG),
				jsonSong.getString(JSON_STREAM_URL_TAG),
				jsonSong.getString(JSON_ARTWORK_TAG),
				jsonSong.getString(JSON_CHOOSEN_BY_TAG),
				s
		);
	}

	public static ArrayList<Song> parse(JSONArray a) throws JSONException
	{
		Log.d(TAG, a.toString());
		int arrayLength = a.length();
		ArrayList<Song> songList = new ArrayList<Song>();
		for(int i = 0; i < arrayLength; i++)
		{
			songList.add(Song.parse(a.getJSONObject(i)));
		}
		return songList;
	}

	public String getChosenBy()
	{
		return chosenBy;
	}

	public String getArtworkUrl()
	{
		return artworkUrl;
	}

	public void setArtworkUrl(String photo_link)
	{
		this.artworkUrl = photo_link;
	}

	public String getName()
	{
		return title;
	}

	public void setName(String songName)
	{
		this.title = songName;
	}

	public String getArtist()
	{
		return artist;
	}

	public void setArtist(String artist)
	{
		this.artist = artist;
	}

	public String getUrl()
	{
		return streamUrl;
	}

	public void setUrl(String song_uri)
	{
		this.streamUrl = song_uri;
	}

	public int compareTo(Song other)
	{
		if(this.title.compareTo(other.getName()) > 0)
		{
			return 1;
		}
		else if(this.title.compareTo(other.getName()) < 0)
		{
			return -1;
		}
		else
		{
			return 0;
		}
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(title);
		dest.writeString(artist);
		dest.writeString(streamUrl);
		dest.writeString(artworkUrl);
		dest.writeString(chosenBy);
		dest.writeLong(startTime);
	}

	public long getStartTime()
	{
		return startTime;
	}
}
