package edu.jocruzcsumb.discotheque;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


public class ViewProfileActivity extends AppCompatActivity implements View.OnClickListener
{


	private TextView editBio;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_profile);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		editBio = (TextView) findViewById(R.id.bio);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(this);
		//pass object from previous activity
		Intent in = getIntent();
		User user = (User) in.getParcelableExtra("user");
		//TODO: TOMMY, YOU ONLY GET EMAIL FOR LOCALUSER

		if(user != null) {
//			if(!LocalUser.getCurrentUser().equals(user)){
//				editFirstName.setEnabled(false);
//				editLastName.setEnabled(false);
//				editDescription.setEnabled(false);
//				editGenres.setEnabled(false);
//				fab.setVisibility(View.GONE);
//			}
			getSupportActionBar().setTitle(user.getFirstName() + " " + user.getLastName());
			if(user.getBio() != null){
				editBio.setText(user.getBio());
			}
		}
	}


	@Override
	public void onClick(View view)
	{
		switch(view.getId())
		{
			case R.id.fab:
//				String tempfirstName, tempLastName, tempDescription, tempEmail, tempGenres;
//				tempfirstName = editFirstName.getText().toString();
//				tempLastName = editLastName.getText().toString();
//				tempDescription = editDescription.getText().toString();
//				tempEmail = editEmail.getText().toString();
//				tempGenres = editGenres.getText().toString();
//				JSONObject jsonObject = new JSONObject();

//				try
//				{
//					jsonObject.put("firstName", tempfirstName);
//					jsonObject.put("lastName", tempLastName);
//					jsonObject.put("description", tempDescription);
//					jsonObject.put("tempEmail", tempEmail);
//					jsonObject.put("genre", tempGenres);
//				}
//				catch(JSONException e)
//				{
//					e.printStackTrace();
//				}

//				Sockets.getSocket().emit("update user", jsonObject);
//
//				Snackbar.make(view, "Profile info saved", Snackbar.LENGTH_LONG)
//						.setAction("Action", null).show();
				break;

			//case R.id.friends:
				//Intent guestLogin = new Intent(ViewProfileActivity.this, ViewFriendActivity.class);
				//startActivity(guestLogin);
		}

	}
}
