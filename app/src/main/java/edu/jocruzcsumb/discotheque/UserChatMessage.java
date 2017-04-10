package edu.jocruzcsumb.discotheque;

/**
 * Created by Tommy on 3/23/2017.
 */

public class UserChatMessage extends User
{

	private String chatMessage;
	private String pub_time;

	public UserChatMessage()
	{
		super();
		chatMessage = "";
		pub_time = "";
	}

	public UserChatMessage(String firstName, String lastName, String photo, String chatMessage, String pub_time)
	{
		super(firstName, lastName, photo);
		this.chatMessage = chatMessage;
		this.pub_time = pub_time;
	}

	public String getChatMessage()
	{
		return chatMessage;
	}

	public void setChatMessage(String chatMessage)
	{
		this.chatMessage = chatMessage;
	}

	public String getPub_time()
	{
		return pub_time;
	}

	public void setPub_time(String pub_time)
	{
		this.pub_time = pub_time;
	}


}
