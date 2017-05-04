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

	public static final String TAG = "Song";

	public static final String JSON_TITLE_TAG = "title";
	public static final String JSON_ARTIST_TAG = "creator_user";
	public static final String JSON_STREAM_URL_TAG = "stream_url";
	public static final String JSON_ARTWORK_TAG = "artwork";
	public static final String JSON_CHOSEN_BY_TAG = "chosen_by";
	public static final String JSON_DURATION_TAG = "duration";
	public static final String JSON_PERMALINK_TAG = "track_permalink";
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
	private String track_permalink = null;
	// This tells us whether the song was picked by a user, or chosenBy by the server.
	// If a song is picked by a user, it should be moved just above the first chosenBy song in the list
	private String chosenBy;
	private long startTime;
	private int duration;

	public Song(String title, String artist, String streamUrl, String artworkUrl, String chosenBy, long startTime, int duration, String track_permalink)
	{
		this.title = title;
		this.artist = artist;
		this.streamUrl = streamUrl;
		this.artworkUrl = artworkUrl;
		this.chosenBy = chosenBy;
		this.startTime = startTime;
		this.duration = duration;
		this.track_permalink = track_permalink;
	}

	private Song(Parcel in)
	{
		title = in.readString();
		artist = in.readString();
		streamUrl = in.readString();
		artworkUrl = in.readString();
		chosenBy = in.readString();
		startTime = in.readLong();
		duration = in.readInt();
		track_permalink = in.readString();
	}

	public static Song parse(JSONObject jsonSong) throws JSONException
	{
		Log.v(TAG, jsonSong.toString());
		long s = 0;
		try
		{
			s = (jsonSong.has(JSON_START_TIME_TAG) ? jsonSong.getLong(JSON_START_TIME_TAG) : 0);
		}
		catch (Exception e)
		{
		}
		int d = 0;
		try
		{
			d = (jsonSong.has(JSON_DURATION_TAG) ? jsonSong.getInt(JSON_DURATION_TAG) : 0);
		}
		catch (Exception e)
		{
		}
		String c = (jsonSong.has(JSON_CHOSEN_BY_TAG) ? jsonSong.getString(JSON_CHOSEN_BY_TAG) : "server");
		return new Song(
				jsonSong.getString(JSON_TITLE_TAG),
				jsonSong.getString(JSON_ARTIST_TAG),
				jsonSong.getString(JSON_STREAM_URL_TAG),
				jsonSong.getString(JSON_ARTWORK_TAG),
				c,
				s,
				d,
				jsonSong.getString(JSON_PERMALINK_TAG)
		);
	}

	public static ArrayList<Song> parse(JSONArray a) throws JSONException
	{
		Log.v(TAG, a.toString());
		int arrayLength = a.length();
		ArrayList<Song> songList = new ArrayList<Song>();
		for (int i = 0; i < arrayLength; i++)
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

	public String getTrack_permalink()
	{
		return track_permalink;
	}

	public String getUrl()
	{
		return streamUrl;
	}

	public int getDuration()
	{
		return duration;
	}

	public int compareTo(Song other)
	{
		if (this.title.compareTo(other.getName()) > 0)
		{
			return 1;
		}
		else if (this.title.compareTo(other.getName()) < 0)
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
		dest.writeInt(duration);
		dest.writeString(track_permalink);
	}

	public long getStartTime()
	{
		return startTime;
	}

	public void print(Log.Level level)
	{
		//print current song
		Log.l(level, TAG,
				"Title: " + getName() +
				"\nArtist: " + getArtist() +
				"\nStart time: " + getStartTime() +
				"\nURL: " + getUrl()
		);
	}

	public boolean equals(Object other)
	{
		if (other instanceof Song)
		{
			Song s = (Song) other;
//			print(Log.Level.Debug);
//			s.print(Log.Level.Debug);
			return title.equals(s.title) && artist.equals(s.artist) && track_permalink.equals(s.track_permalink);
		}
		else
		{
			return false;
		}
	}
}
