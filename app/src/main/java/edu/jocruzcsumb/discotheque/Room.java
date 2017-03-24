package edu.jocruzcsumb.discotheque;

import java.util.ArrayList;

/**
 * Created by Tommy on 3/23/2017.
 */

public class Room {

    private String floorName;
    private int numOfUsers;
    private SongList songList;
    private UserList userList;
    private ArrayList<UserChatMessage> chatList;

    public Room(){
        floorName = "blank";
        numOfUsers = 0;
        songList = new SongList();
        userList = new UserList();
        chatList = new ArrayList<UserChatMessage>();
    }


    public String getFloorName(){
        return floorName;
    }

    public int getNumOfUsers(){
        return numOfUsers;
    }

    public void setFloorName(String floorName){
        this.floorName = floorName;
    }

    public void addAllSong(Song song){
        songList.addSong(song);
    }

    public void addUserToList(User user){
        userList.addUser(user);
        numOfUsers++;
    }

    public void addChatMessage(UserChatMessage chatMessage){
        chatList.add(chatMessage);
    }

    public ArrayList<UserChatMessage> getChatList(){
        return chatList;
    }

    public UserList getUserList(){
        return userList;
    }

    public SongList getSongList(){
        return songList;
    }

    public boolean removeUserFromList(User user){
        if(userList.deleteUser(user)){
            return true;
        }

        return false;
    }


}
