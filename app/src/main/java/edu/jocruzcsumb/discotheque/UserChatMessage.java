package edu.jocruzcsumb.discotheque;

import java.util.HashMap;

/**
 * Created by Tommy on 3/23/2017.
 */

public class UserChatMessage extends User{

    private String chatMessage;

    public UserChatMessage(String userName, String name, String email, String photo, String chatMessage){
        super(userName, name, email, photo);
        this.chatMessage = chatMessage;
    }

    public String getChatMessage(){
        return chatMessage;
    }

    public void setChatMessage(String chatMessage){
        this.chatMessage = chatMessage;
    }






}
