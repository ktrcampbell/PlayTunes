package com.bigbang.playtunes.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.bigbang.playtunes.R;
import com.bigbang.playtunes.adapter.SongsAdapter;
import com.bigbang.playtunes.model.UploadSong;
import com.bigbang.playtunes.util.DebugLogger;
import com.bigbang.playtunes.viewmodel.SongViewModel;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import static com.bigbang.playtunes.util.Constants.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowSongsActivity extends AppCompatActivity implements SongsAdapter.SongClickListener {

    private Observer<List<UploadSong>> songObservable;
    private List<UploadSong> songList = new ArrayList<>();
    private SongViewModel viewModel;
    private MediaPlayer mediaPlayer;

    @BindView(R.id.navigation)
    BottomNavigationView navigationView;

    @BindView(R.id.song_recyclerview)
    RecyclerView songRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_songs);
        ButterKnife.bind(this);

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.pick_song_item:
                        Intent i = new Intent(ShowSongsActivity.this, UploadActivity.class);
                        startActivity(i);
                        break;
                    case R.id.playlist_item:

                        break;
                    case R.id.logout_item:
                        //logout();

                        //return true;
                        break;
                }
                return false;
            }
            });

        viewModel = ViewModelProviders.of(this).get(SongViewModel.class);
//        songRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        songRecyclerView.setAdapter(new SongsAdapter(songList));


        songObservable = new Observer<List<UploadSong>>() {
            @Override
            public void onChanged(List<UploadSong> uploadSongs) {
                DebugLogger.logDebug(TAG, "count:" + uploadSongs.size());
                displaySongs(uploadSongs);

            }
        };

        //songObservable = uploadSongs -> getSongs(uploadSongs);

        viewModel.getUploadSongs().observe(this, songObservable);
    }
    private void getSongs(List<UploadSong> uploadSongs){

//
//        adapter.notifyDataSetChanged();

    }
    private void displaySongs(List<UploadSong> uploadSongs) {
        SongsAdapter adapter = new SongsAdapter(uploadSongs);
        songRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        songRecyclerView.setAdapter(adapter);
        
    }

    @Override
    public void playSong(UploadSong song) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(song.getSongLink());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mediaPlayer.prepareAsync();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
