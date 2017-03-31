package edu.jocruzcsumb.discotheque;

import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Tommy on 3/23/2017.
 */

public class UserList{

    private ArrayList<User> arrayList;

    public UserList(){
        arrayList = new ArrayList<User>();
    }

    public void addUser(User user){
        arrayList.add(user);
    }

    public int numUser(){

        return arrayList.size();
    }

    public boolean deleteUser(User user){
        if(arrayList.contains(user)){
            arrayList.remove(user);
            return true;
        }
        return false;
    }

    public ArrayList<User> allUsers(){
        return arrayList;
    }


}
