package com.bigbang.playtunes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bigbang.playtunes.R;
import com.bigbang.playtunes.model.UploadSong;
import com.bumptech.glide.Glide;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongViewHolder> {

    private Context context;
    private List<UploadSong> uploadSongs;
    private SongClickListener songClickListener;

    public SongsAdapter(List<UploadSong> uploadSongs) {
        //this.context = context;
        this.uploadSongs = uploadSongs;
    }

    public interface SongClickListener{
        void playSong(UploadSong song);
    }

    @NonNull
    @Override
    public SongsAdapter.SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item_layout, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongsAdapter.SongViewHolder holder, int position) {
        holder.songTitleTextView.setText(uploadSongs.get(position).getSongTitle());
        holder.songDurationTextView.setText(uploadSongs.get(position).getSongDuration());
        Glide.with(holder.itemView.getContext())
                .load(R.drawable.ic_music_note)
                .into(holder.musicImageView);
        Glide.with(holder.itemView.getContext())
                .load(R.drawable.ic_play_arrow)
                .into(holder.playImageView);
        Glide.with(holder.itemView.getContext())
                .load(R.drawable.ic_stop)
                .into(holder.stopImageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(songClickListener != null) {
                    songClickListener.playSong(uploadSongs.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return uploadSongs.size();
    }

    class SongViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.song_title_textview)
        TextView songTitleTextView;

        @BindView(R.id.song_duration_textview)
        TextView songDurationTextView;

        @BindView(R.id.music_imageview)
        ImageView musicImageView;

        @BindView(R.id.play_imageview)
        ImageView playImageView;

        @BindView(R.id.stop_imageview)
        ImageView stopImageView;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
