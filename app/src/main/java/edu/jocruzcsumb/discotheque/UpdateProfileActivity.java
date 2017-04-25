package edu.jocruzcsumb.discotheque;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


/**
 * Created by Admin on 4/18/2017.
 */

public class UpdateProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_floor);
        Spinner spinner1 = (Spinner) findViewById(R.id.profileGenre);
        Spinner spinner2 = (Spinner) findViewById(R.id.profileGenre2);
        Spinner spinner3 = (Spinner) findViewById(R.id.profileGenre3);

        spinner1.setOnItemSelectedListener(this);
        spinner2.setOnItemSelectedListener(this);
        spinner3.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.profileGenre, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter);
        spinner3.setAdapter(adapter);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        parent.getItemAtPosition(pos);

        //do something with the selected genre

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
            //Don't think we need to mess with this?
    }
}