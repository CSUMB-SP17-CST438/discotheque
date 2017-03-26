package edu.jocruzcsumb.discotheque;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener {


    //setting listers

    ArrayList<Button> buttons = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int[] ids = new int[]{R.id.guest_login_btn, R.id.login_btn, R.id.register_btn};
        for(int id:ids){
            Button b = (Button) findViewById(id);
            buttons.add(b);
            b.setOnClickListener(this);
        }
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

            case R.id.register_btn:
                //go to activity
                Intent register = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(register);

                break;
        }
    }
}
