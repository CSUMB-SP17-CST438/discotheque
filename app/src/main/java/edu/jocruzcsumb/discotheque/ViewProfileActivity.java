package edu.jocruzcsumb.discotheque;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


public class ViewProfileActivity extends AppCompatActivity implements View.OnClickListener
{

    private EditText editFirstName;
    private EditText editLastName;
    private EditText editEmail;
    private EditText editDescription;
    private EditText editGenres;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_profile);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		editFirstName = (EditText) findViewById(R.id.firstName);
		editLastName = (EditText) findViewById(R.id.lastName);
		editEmail = (EditText) findViewById(R.id.email);
		editDescription = (EditText) findViewById(R.id.description);
		editGenres = (EditText) findViewById(R.id.genres);
		TextView viewFriends = (TextView) findViewById(R.id.friends);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(this);
		viewFriends.setOnClickListener(this);

        //pass object from previous activity
        Intent in = getIntent();
        User user = (User) in.getParcelableExtra("user");

        editFirstName.setText(user.getFirstName());
        editLastName.setText(user.getLastName());
        editDescription.setText(user.getDescription());
        editEmail.setText(user.getEmail());
        editGenres.setText(user.getGenre());



	}


	@Override
	public void onClick(View view)
	{
		switch(view.getId()){
			case R.id.fab:
			    String tempfirstName, tempLastName, tempDescription, tempEmail, tempGenres;
                tempfirstName = editFirstName.getText().toString();
                tempLastName = editLastName.getText().toString();
                tempDescription = editDescription.getText().toString();
                tempEmail = editEmail.getText().toString();
                tempGenres = editGenres.getText().toString();
                JSONObject jsonObject = new JSONObject();

                try{
                    jsonObject.put("firstName", tempfirstName);
                    jsonObject.put("lastName", tempLastName);
                    jsonObject.put("description", tempDescription);
                    jsonObject.put("tempEmail", tempEmail);
                    jsonObject.put("genre", tempGenres);
                }
                catch(JSONException e){
                    e.printStackTrace();
                }

                Sockets.getSocket().emit("update user", jsonObject);

				Snackbar.make(view, "Profile info saved", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
				break;

			case R.id.friends:
				//Intent guestLogin = new Intent(ViewProfileActivity.this, ViewFriendActivity.class);
				//startActivity(guestLogin);
		}

	}
}
