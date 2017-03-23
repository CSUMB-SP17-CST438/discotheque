package edu.jocruzcsumb.discotheque;

/**
 * Created by Tommy on 3/22/2017.
 */

public class User {

    private String userName;
    private String name;
    private String email;
    private String photo;
    private String genre;


    public User(){
        userName = "";
        name = "";
        email = "";
        photo = "";
        genre = "";
    }

    public User(String userName, String name, String email, String photo, String genre){
        this.userName = userName;
        this.name = name;
        this.email = email;
        this.photo = photo;
        this.genre = genre;

    }

    public String getUserName(){
        return userName;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public String getGenre(){
        return genre;
    }

    public String getPhoto(){
        return photo;
    }

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


}
