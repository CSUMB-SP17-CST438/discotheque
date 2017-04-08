package edu.jocruzcsumb.discotheque;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Tommy on 3/23/2017.
 */

public class Song implements Comparable<Song>, Serializable
{

    private String title = null;
    private String artist = null;
    private String streamUrl = null;
    private String artworkUrl = null;

    // This tells us whether the song was picked by a user, or randomized by the server.
    // If a song is picked by a user, it should be moved just above the first randomized song in the list
    private boolean randomized;

    public static final String JSON_TITLE_TAG = "title";
    public static final String JSON_ARTIST_TAG = "creator_user";
    public static final String JSON_STREAM_URL_TAG = "stream_url";
    public static final String JSON_ARTWORK_TAG = "artwork";
    public static final String JSON_RANDOM_TAG = "random";

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

    public String getName()
    {
        return title;
    }

    public String getArtist()
    {
        return artist;
    }

    public String getUrl()
    {
        return streamUrl;
    }

    public void setArtworkUrl(String photo_link)
    {
        this.artworkUrl = photo_link;
    }

    public void setName(String songName)
    {
        this.title = songName;
    }

    public void setArtist(String artist)
    {
        this.artist = artist;
    }

    public void setUrl(String song_uri)
    {
        this.streamUrl = song_uri;
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
}
