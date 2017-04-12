package edu.jocruzcsumb.discotheque;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import static edu.jocruzcsumb.discotheque.FloorService.EVENT_FLOOR_JOINED;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_GET_MESSAGE_LIST;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_GET_SONG_LIST;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_GET_USER_LIST;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_JOIN_FLOOR;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_MESSAGE_ADD;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_MESSAGE_LIST_UPDATE;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_MESSAGE_SEND;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_SONG_LIST_UPDATE;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_USER_ADD;
import static edu.jocruzcsumb.discotheque.FloorService.EVENT_USER_LIST_UPDATE;

/**
 * Created by carsen on 4/11/17.
 */

public class FloorBroadcastReciever extends BroadcastReceiver
{
	public FloorBroadcastReciever(IntentFilter f)
	{
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		switch(intent.getAction())
		{
			case EVENT_FLOOR_JOINED:
				break;
			case EVENT_JOIN_FLOOR:
				break;
			case EVENT_MESSAGE_LIST_UPDATE:
				break;
			case EVENT_USER_LIST_UPDATE:
				break;
			case EVENT_SONG_LIST_UPDATE:
				break;
			case EVENT_GET_MESSAGE_LIST:
				break;
			case EVENT_GET_USER_LIST:
				break;
			case EVENT_GET_SONG_LIST:
				break;
			case EVENT_MESSAGE_ADD:
				break;
			case EVENT_MESSAGE_SEND:
				break;
			case EVENT_USER_ADD:
				break;
		}
	}
}
