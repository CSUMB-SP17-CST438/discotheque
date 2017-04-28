package edu.jocruzcsumb.discotheque;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class User implements Parcelable
{
	public static final String TAG = "Member";

	//For json parsing
	public static final String JSON_USERNAME_TAG = "username";
	public static final String JSON_CREATED_FLOORS_TAG = "created_floors";
	public static final String JSON_FNAME_TAG = "member_FName";
	public static final String JSON_LNAME_TAG = "member_LName";
	public static final String JSON_IMG_URL_TAG = "member_img_url";

	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>()
	{

		// This simply calls our new constructor (typically private) and
		// passes along the unmarshalled `Parcel`, and then returns the new object!
		@Override
		public User createFromParcel(Parcel in)
		{
			return new User(in);
		}

		// We just need to copy this and change the type to match our class.
		@Override
		public User[] newArray(int size)
		{
			return new User[size];
		}
	};
	private String userName = null;
	private String firstName = null;
	private String lastName = null;
	private String photo = null;
	private String bio = null;
	private ArrayList<String> genres = null;

	public User()
	{
		genres = new ArrayList<String>();
	}

	public User(String userName, String firstName, String lastName)
	{
		this();
		this.userName = userName;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public User(String userName, String firstName, String lastName, String photo)
	{
		this();
		this.userName = userName;
		this.firstName = firstName;
		this.lastName = lastName;
		this.photo = photo;

	}

	public User(String userName, String firstName, String lastName, String photo, String bio)
	{
		this();
		this.userName = userName;
		this.firstName = firstName;
		this.lastName = lastName;
		this.photo = photo;
		this.bio = bio;

	}

	private User(Parcel in)
	{
		userName = in.readString();
		firstName = in.readString();
		lastName = in.readString();
		photo = in.readString();
		bio = in.readString();
		//TODO
	}

	protected static User parse(JSONObject jsonUser) throws JSONException
	{
		Log.v(TAG, jsonUser.toString());
		return new User(
				jsonUser.getString(JSON_USERNAME_TAG),
				jsonUser.getString(JSON_FNAME_TAG),
				jsonUser.getString(JSON_LNAME_TAG),
				jsonUser.getString(JSON_IMG_URL_TAG)
		);
	}

	public static ArrayList<User> parse(JSONArray a) throws JSONException
	{
		Log.v(TAG, a.toString());
		int arrayLength = a.length();
		ArrayList<User> userList = new ArrayList<User>();
		for (int i = 0; i < arrayLength; i++)
		{
			userList.add(User.parse(a.getJSONObject(i)));
		}
		return userList;
	}

	protected static User parseProfile(JSONObject jsonUser) throws JSONException
	{
		//TODO: PARSE EVEN MORE USER INFO
		return new User(
				jsonUser.getString(JSON_USERNAME_TAG),
				jsonUser.getString(JSON_FNAME_TAG),
				jsonUser.getString(JSON_LNAME_TAG),
				jsonUser.getString(JSON_IMG_URL_TAG)
		);
	}

	public static boolean isValidUsername(String username)
	{
		if (username.length() > 120)
		{
			return false;
		}
		else if (username.length() < 6)
		{
			return false;
		}
		return true;
	}

	public static boolean isValidPassword(String password)
	{
		if (password.length() > 140)
		{
			return false;
		}
		else if (password.length() < 3)
		{
			return false;
		}
		return true;
	}

	public static boolean isValidEmail(String email)
	{
		if (email.length() > 120)
		{
			return false;
		}
		else if (email.length() < 6)
		{
			return false;
		}
		return email.contains("@");
	}

	public boolean equals(Object other)
	{
		if (other instanceof User)
		{
			return (((User) other).userName.equals(userName));
		}
		else
		{
			return false;
		}
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public ArrayList<String> getGenres()
	{
		return genres;
	}

	public String getPhoto()
	{
		return photo;
	}

	public void setPhoto(String photo)
	{
		this.photo = photo;
	}

	public String getBio()
	{
		return bio;
	}

	public void setBio(String bio)
	{
		this.bio = bio;
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(userName);
		dest.writeString(firstName);
		dest.writeString(lastName);
		dest.writeString(photo);
		dest.writeString(bio);
		//TODO
	}


//
//    public User(Parcel in){
//
//        this.userName = in.readString();
//        this.firstName = in.readString();
//        this.lastName = in.readString();
//        this.email = in.readString();
//        this.photo = in.readString();
//
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//
//        dest.writeString(userName);
//        dest.writeString(firstName);
//        dest.writeString(lastName);
//        dest.writeString(email);
//        dest.writeString(photo);
//
//    }
//    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
//        public User createFromParcel(Parcel in) {
//            return new User(in);
//        }
//
//        public User[] newArray(int size) {
//            return new User[size];
//        }
//    };

}
