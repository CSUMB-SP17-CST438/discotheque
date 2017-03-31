package edu.jocruzcsumb.discotheque;

import android.os.Parcel;
import android.os.Parcelable;


public class User implements Parcelable {

    private String member_id;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String photo;
    private String description;
    private String genre;
    private UserList userList;



    public User(){
        member_id = "";
        userName = "";
        firstName = "";
        lastName = "";
        email = "";
        photo = "";
        description = "";
        genre = "";
        userList = new UserList();
    }
    public User(String userName, String firstName, String lastName, String email, String photo, String description){
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.photo = photo;
        this.description = description;
        userList = new UserList();
    }

    public User(String member_id, String userName, String firstName, String lastName, String email, String photo, String genre, String description){
        this.member_id = member_id;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.photo = photo;
        this.genre = genre;
        this.description = description;
        userList = new UserList();
    }

    public String getMember_id(){return member_id;}

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

    public String getGenre(){
        return genre;
    }

    public String getPhoto(){
        return photo;
    }

    public String getDescription(){return description;}

    public void setMember_id(String member_id){this.member_id = member_id;}

    public void setFirstName(String firstName){this.firstName = firstName;}

    public void setLastName(String lastName){this.lastName = lastName;}

    public void setDescription(String description){this.description = description;}

    public void setUserName(String userName){
        this.userName = userName;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setPhoto(String photo){
        this.photo = photo;
    }

    public void setGenre(String genre){
        this.genre = genre;
    }

    public void addFriend(User user){
        userList.addUser(user);
    }

    public boolean deleteFriend(User user){
        return userList.deleteUser(user);
    }

    public int numOfFriends(){return userList.numUser();}

    public UserList userList(){return userList;}

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

    public User(Parcel in){

        this.member_id = in.readString();
        this.userName = in.readString();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.email = in.readString();
        this.photo = in.readString();
        this.genre = in.readString();
        this.description = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(member_id);
        dest.writeString(userName);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(email);
        dest.writeString(photo);
        dest.writeString(genre);
        dest.writeString(description);

    }
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };










}
