package edu.jocruzcsumb.discotheque;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

import static junit.framework.Assert.fail;

/**
 * Created by Carsen on 4/7/17.
 */

public class Message implements Serializable
{

    //For JSON parsing
    public static final String JSON_FROM_TAG = "member";
    public static final String JSON_FLOOR_TAG = "floor";
    public static final String JSON_TEXT_TAG = "text";
    public static final String JSON_PUB_TIME_TAG = "pubTime";
    public static final String JSON_ID_TAG = "mess_id";

    private int id = 0;
    private User author = null;
    private String text = null;
    private int floor = 0;
    private Date pubTime = null;

    public Message(int id, User author, String text, int floor, Date pubTime)
    {
        this.id = id;
        this.text = text;
        this.author = author;
        this.floor = floor;
        this.pubTime = pubTime;
    }

    public static Message parse(JSONObject jsonMessage) throws JSONException
    {
        User author = User.parse(jsonMessage.getJSONObject(JSON_FROM_TAG));
        String text = jsonMessage.getString(JSON_TEXT_TAG);
        Date pubTime = parsePubTime(jsonMessage.getLong(JSON_PUB_TIME_TAG));
        int floor = jsonMessage.getInt(JSON_FLOOR_TAG);
        int id = jsonMessage.getInt(JSON_ID_TAG);
        fail("NYI");
        return new Message(id, author, text, floor, pubTime);
    }

    private static Date parsePubTime(long epochTime)
    {
        return new Date(epochTime);
    }

    public String getText()
    {
        return text;
    }

    public User getAuthor()
    {
        return author;
    }

    public int getId()
    {
        return id;
    }

    public int getFloor()
    {
        return floor;
    }

    public Date getPubTime()
    {
        return pubTime;
    }
}
