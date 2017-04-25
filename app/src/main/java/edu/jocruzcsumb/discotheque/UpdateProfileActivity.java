package edu.jocruzcsumb.discotheque;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Admin on 4/18/2017.
 */

public class UpdateProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editBio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

    }

    @Override
    public void onClick(View v){
        
    }
}