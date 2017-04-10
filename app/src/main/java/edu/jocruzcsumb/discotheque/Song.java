package edu.jocruzcsumb.discotheque;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Tommy on 3/23/2017.
 */

public class Song implements Comparable<Song>, Parcelable
{

	public static final String JSON_TITLE_TAG = "title";
	public static final String JSON_ARTIST_TAG = "creator_user";
	public static final String JSON_STREAM_URL_TAG = "stream_url";
	public static final String JSON_ARTWORK_TAG = "artwork";
	public static final String JSON_RANDOM_TAG = "random";

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
	// This tells us whether the song was picked by a user, or randomized by the server.
	// If a song is picked by a user, it should be moved just above the first randomized song in the list
	private boolean randomized;

	public Song()
	{
		randomized = true;
	}

	public Song(String title, String artist, String streamUrl, String artworkUrl, boolean randomized)
	{
		this();
		this.title = title;
		this.artist = artist;
		this.streamUrl = streamUrl;
		this.artworkUrl = artworkUrl;
		this.randomized = randomized;
	}

	private Song(Parcel in)
	{
		title = in.readString();
		artist = in.readString();
		streamUrl = in.readString();
		artworkUrl = in.readString();
		randomized = in.readInt() == 1;
	}

	public static Song parse(JSONObject jsonSong) throws JSONException
	{
		return new Song(
				jsonSong.getString(JSON_TITLE_TAG),
				jsonSong.getString(JSON_ARTIST_TAG),
				jsonSong.getString(JSON_STREAM_URL_TAG),
				jsonSong.getString(JSON_ARTWORK_TAG),
				jsonSong.getBoolean(JSON_RANDOM_TAG)
		);
	}

	public static ArrayList<Song> parse(JSONArray a) throws JSONException
	{
		int arrayLength = a.length();
		ArrayList<Song> songList = new ArrayList<Song>();
		for(int i = 0; i < arrayLength; i++)
		{
			songList.add(Song.parse(a.getJSONObject(i)));
		}
		return songList;
	}

	public boolean getIsUserPicked()
	{
		return !randomized;
	}

	public boolean getIsRandomized()
	{
		return randomized;
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
		dest.writeInt(randomized? 1 : 0);
	}
}
