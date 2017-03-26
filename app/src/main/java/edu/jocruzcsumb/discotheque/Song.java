package edu.jocruzcsumb.discotheque;

/**
 * Created by Tommy on 3/23/2017.
 */

public class Song {

    private String songName;
    private String artist;
    private String song_uri;

    public Song(){
        songName = "";
        artist = "";
        song_uri = "";
    }

    public String getSongName(){
        return songName;
    }

    public String getArtist(){
        return artist;
    }

    public String getSong_uri(){
        return song_uri;
    }

    public void setSongName(String songName){
        this.songName = songName;
    }

    public void setArtist(String artist){
        this.artist = artist;
    }

    public void setSong_uri(String song_uri){
        this.song_uri = song_uri;
    }
}
