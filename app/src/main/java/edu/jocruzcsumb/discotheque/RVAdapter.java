package edu.jocruzcsumb.discotheque;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by Admin on 3/31/2017.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.SongViewHolder> {

    SongList songList;
    Context mContext;
    CustomItemClickListener listener;
    Bitmap bmp;

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView songName;
        TextView name;
        ImageView image;

        SongViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            songName = (TextView) itemView.findViewById(R.id.song);
            name = (TextView) itemView.findViewById(R.id.artist);
            image = (ImageView) itemView.findViewById(R.id.photo);
        }
    }

    RVAdapter(Context mContext, SongList songList, CustomItemClickListener listener) {
        this.songList = songList;
        this.mContext = mContext;
        this.listener = listener;
    }

    public int getItemCount() {
        return songList.size();
    }


    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_song_list_view, viewGroup, false);
        final SongViewHolder svh = new SongViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, svh.getAdapterPosition());
            }
        });

        return svh;


    }

    @Override
    public void onBindViewHolder(SongViewHolder songViewHolder, int i) {
        songViewHolder.songName.setText(songList.getSong(i).getSongName());
        songViewHolder.name.setText(songList.getSong(i).getArtist());
        if (!songList.getSong(i).getPhoto_link().equals("null")) {
            Picasso.with(mContext).load(songList.getSong(i).getPhoto_link()).into(songViewHolder.image);

        }
        else{
            songViewHolder.image.setImageResource(R.drawable.ic_launcher);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

    }
}
