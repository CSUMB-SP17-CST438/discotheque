package edu.jocruzcsumb.discotheque;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class ViewProfileActivity extends AppCompatActivity implements View.OnClickListener
{
    private TextView editBio;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        editBio = (TextView) findViewById(R.id.bio);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //image = (ImageView) findViewById(R.id.profile_picture);
        fab.setOnClickListener(this);
        //pass object from previous activity
        Intent in = getIntent();
        User user = (User) in.getParcelableExtra("user");
        if (user != null)
        {
            //if not current user, info is not edible
            //TODO:hide button if not local user
//			if(){
//				fab.setVisibility(View.GONE);
//			}
            getSupportActionBar().setTitle(user.getFirstName() + " " + user.getLastName());
            //Picasso.with(this).load(user.getPhoto()).into(image);
            if (user.getBio() != null)
            {
                editBio.setText(user.getBio());
            }
        }
    }


    @Override
    public void onClick(View view)
    {
        switch (view.getId())
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
                Intent intent = new Intent(ViewProfileActivity.this, UpdateProfileActivity.class);
                startActivity(intent);
                Snackbar.make(view, "Profile info saved", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show();
                break;

            //case R.id.friends:
            //Intent guestLogin = new Intent(ViewProfileActivity.this, ViewFriendActivity.class);
            //startActivity(guestLogin);
        }

    }
}
