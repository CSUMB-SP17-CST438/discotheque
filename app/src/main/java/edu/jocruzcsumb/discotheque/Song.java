package edu.jocruzcsumb.discotheque;

/**
 * Created by Tommy on 3/23/2017.
 */

public class Song implements Comparable<Song> {

    private String songName;
    private String artist;
    private String song_uri;
    private String photo_link;

    public Song(){
        songName = "";
        artist = "";
        song_uri = "";
        photo_link =  "";
    }

    public String getPhoto_link(){return photo_link;}

    public String getSongName(){
        return songName;
    }

    public String getArtist(){
        return artist;
    }

    public String getSong_uri(){
        return song_uri;
    }

    public void setPhoto_link(String photo_link){this.photo_link = photo_link;}

    public void setSongName(String songName){
        this.songName = songName;
    }

    public void setArtist(String artist){
        this.artist = artist;
    }

    public void setSong_uri(String song_uri){
        this.song_uri = song_uri;
    }

    public int compareTo(Song compareSong){
        if(this.songName.compareTo(compareSong.getSongName()) > 0)
            return 1;
        else if(this.songName.compareTo(compareSong.getSongName()) < 0)
            return -1;
        else
            return 0;
    }
}
