package edu.jocruzcsumb.discotheque;

import org.json.JSONObject;

import java.util.ArrayList;

import static junit.framework.Assert.fail;

/**
 * Created by Tommy on 3/23/2017.
 */

public class Room
{

    private String floorName;
    private int numOfUsers;
    private SongList songList;
    private UserList userList;
    private ArrayList<UserChatMessage> chatList;

    public Room()
    {
        floorName = "blank";
        numOfUsers = 0;
        songList = new SongList();
        userList = new UserList();
        chatList = new ArrayList<UserChatMessage>();
    }



    public static Room parseRoom(JSONObject jsonRoom)
    {
        //TODO
        fail("nyi");
        return null;
    }



    public String getFloorName()
    {
        return floorName;
    }

    public void addUserToList(User user)
    {
        userList.addUser(user);
        numOfUsers++;
    }

    public void addChatMessage(UserChatMessage chatMessage)
    {
        chatList.add(chatMessage);
    }

    public ArrayList<UserChatMessage> getChatList()
    {
        return chatList;
    }

    public UserList getUserList()
    {
        return userList;
    }

    public SongList getSongList()
    {
        return songList;
    }

    public boolean removeUserFromList(User user)
    {
        if (userList.deleteUser(user))
        {
            numOfUsers--;
            return true;
        }

        return false;
    }


}
