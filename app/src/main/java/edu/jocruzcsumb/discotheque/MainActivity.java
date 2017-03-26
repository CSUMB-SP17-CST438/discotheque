package edu.jocruzcsumb.discotheque;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener {


    //setting listers


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //button reference to widgets
        Button guest_login_button = (Button) findViewById(R.id.guest_login_btn);
        Button login_button = (Button) findViewById(R.id.login_btn);

        //setting listeners
        guest_login_button.setOnClickListener(this);
        login_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.guest_login_btn:
                //go to activity
                Intent guestLogin = new Intent(MainActivity.this, JoinRoom.class);
                startActivity(guestLogin);

                break;

            case R.id.login_btn:
                //go to activity
                Intent login = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(login);

                break;
        }
    }
}
