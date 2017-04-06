package edu.jocruzcsumb.discotheque;

import java.util.ArrayList;

/**
 * Created by Tommy on 3/22/2017.
 */

public class User {

    private String userName = null;
    private String firstName = null;
    private String lastName = null;
    private String email = null;
    private String photo = null;
    private String bio = null;
	private ArrayList<String> genres = null;
    private UserList friendsList = null;

    public User(){
		genres = new ArrayList<String>();
        friendsList = new UserList();
    }
    public User(String userName, String firstName, String lastName, String email, String photo, String bio){
		this();
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.photo = photo;
        this.bio = bio;
    }

    public String getUserName(){
        return userName;
    }

    public String getFirstName(){
        return firstName;
    }

    public String getLastName(){return lastName;}

    public String getEmail(){
        return email;
    }

    public ArrayList<String> getGenres(){
        return genres;
    }

    public String getPhoto(){
        return photo;
    }

    public String getBio(){return bio;}

    public void setFirstName(String firstName){this.firstName = firstName;}

    public void setLastName(String lastName){this.lastName = lastName;}

    public void setBio(String bio){this.bio = bio;}

    public void setUserName(String userName){
        this.userName = userName;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setPhoto(String photo){
        this.photo = photo;
    }

    public void addGenre(String genre){
        this.genres.add(genre);
    }

    public void addFriend(User user){
        friendsList.addUser(user);
    }

    public boolean deleteFriend(User user){
        return friendsList.deleteUser(user);
    }

    public int numOfFriends(){return friendsList.numUser();}

    public UserList userList(){return friendsList;}

    public static boolean isValidUsername(String username)
    {
        if(username.length() > 120) return false;
        else if(username.length() < 6) return false;
        return true;
    }

    public static boolean isValidPassword(String password)
    {
        if(password.length() > 140) return false;
        else if(password.length() < 3) return false;
        return true;
    }
    public static boolean isValidEmail(String email)
    {
        if(email.length() > 120) return false;
        else if(email.length() < 6) return false;
        return email.contains("@");
    }








}
