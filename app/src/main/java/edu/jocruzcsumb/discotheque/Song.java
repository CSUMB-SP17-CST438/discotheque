package edu.jocruzcsumb.discotheque;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tommy on 3/23/2017.
 */

public class Song implements Comparable<Song>
{

    private String title;
    private String artist;
    private String streamUrl;
    private String artworkUrl;

    public static final String JSON_TITLE_TAG = "title";
    public static final String JSON_ARTIST_TAG = "creator_user";
    public static final String JSON_STREAM_URL_TAG = "stream_url";
    public static final String JSON_ARTWORK_TAG = "artwork";

    public Song()
    {

    }

    public Song(String title, String artist, String streamUrl, String artworkUrl)
    {
        this();
        this.title = title;
        this.artist = artist;
        this.streamUrl = streamUrl;
        this.artworkUrl = artworkUrl;
    }

    public static Song parse(JSONObject jsonSong) throws JSONException
    {
        String title = jsonSong.getString(JSON_TITLE_TAG);
        String artist = jsonSong.getString(JSON_ARTIST_TAG);
        String streamUrl = jsonSong.getString(JSON_STREAM_URL_TAG);
        String artworkUrl = jsonSong.getString(JSON_ARTWORK_TAG);
        return new Song(title, artist, streamUrl, artworkUrl);
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
