package edu.jocruzcsumb.discotheque;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by carsen on 4/11/17.
 */

public class FloorFragment extends Fragment
{
	private Floor f = null;
	BroadcastReceiver r = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{

		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);
		f = ((FloorActivity) getActivity()).floor;
	}
}
