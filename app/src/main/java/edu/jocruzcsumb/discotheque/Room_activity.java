package edu.jocruzcsumb.discotheque;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Room_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_activity);

        //used to change to the current song
        TextView roomTitle = (TextView) findViewById(R.id.lister_rooms_title);
        TextView CurrentSong = (TextView) findViewById(R.id.curr_song_name_textview);
        TextView currentArtist = (TextView) findViewById(R.id.curr_artist_textiew);
    }
}
