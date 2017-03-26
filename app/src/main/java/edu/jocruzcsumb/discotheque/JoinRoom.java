package edu.jocruzcsumb.discotheque;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class JoinRoom extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_room);


        //button reference to widgets
        Button room = (Button) findViewById(R.id.TEMP_go_to_room);

        //setting listeners
        room.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.TEMP_go_to_room:
                //go to activity
                Intent goToRoom = new Intent(JoinRoom.this, ChatRoomActivity.class);
                startActivity(goToRoom);

                break;
        }
    }
}
