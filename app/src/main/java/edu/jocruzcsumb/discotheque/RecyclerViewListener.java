package edu.jocruzcsumb.discotheque;

import android.view.View;

/**
 * Created by Admin on 3/31/2017.
 */

public interface RecyclerViewListener
{

	public void onItemClick(View v, int position);

	public void onLongItemClick(View v, int position);
}
