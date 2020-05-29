package com.bigbang.playtunes.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.bigbang.playtunes.R;
import com.bigbang.playtunes.adapter.SongsAdapter;
import com.bigbang.playtunes.model.UploadSong;
import com.bigbang.playtunes.util.DebugLogger;
import com.bigbang.playtunes.viewmodel.SongViewModel;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
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

    private AnimationDrawable animDrawable;
    private Observer<List<UploadSong>> songObservable;
    private List<UploadSong> songList = new ArrayList<>();
    private SongViewModel viewModel;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();

    @BindView(R.id.navigation)
    BottomNavigationView navigationView;

    @BindView(R.id.song_recyclerview)
    RecyclerView songRecyclerView;

    @BindView(R.id.constraintLayout)
    ConstraintLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_songs);
        ButterKnife.bind(this);

        animDrawable = (AnimationDrawable) rootLayout.getBackground();
        animDrawable.setEnterFadeDuration(10);
        animDrawable.setExitFadeDuration(5000);
        animDrawable.start();

        navigationView.setSelectedItemId(R.id.playlist_item);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.pick_song_item:
                        Intent i = new Intent(ShowSongsActivity.this, UploadActivity.class);
                        startActivity(i);
                        overridePendingTransition(0,0);
                        break;
                    case R.id.playlist_item:

                        break;
                    case R.id.logout_item:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                        finish();
                        return true;
                }
                return false;
            }
            });

        viewModel = ViewModelProviders.of(this).get(SongViewModel.class);

        songObservable = new Observer<List<UploadSong>>() {
            @Override
            public void onChanged(List<UploadSong> uploadSongs) {
                DebugLogger.logDebug(TAG, "count:" + uploadSongs.size());
                displaySongs(uploadSongs);
            }
        };

        if(viewModel.getUserLoggedIn()){
          getSongs();
        }
//        viewModel.getUploadSongs().observe(this, songObservable);
        mediaPlayer = new MediaPlayer();
    }
    private void getSongs(){
        viewModel.getUploadSongs().observe(this, songObservable);

    }
    private void displaySongs(List<UploadSong> uploadSongs) {
        SongsAdapter adapter = new SongsAdapter(uploadSongs);
        songRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        songRecyclerView.setAdapter(adapter);
        
    }

    @Override
    public void playSong(UploadSong song) {
         if (mediaPlayer.isPlaying()) {
             mediaPlayer.start();
         }
//            mediaPlayer.stop();
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }


        try {
            assert mediaPlayer != null;
            mediaPlayer.setDataSource(song.getSongLink());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mp.start();
//            }
//        });
       // mediaPlayer.prepareAsync();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
