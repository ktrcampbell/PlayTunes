package com.bigbang.playtunes.viewmodel;

import android.app.Application;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.bigbang.playtunes.R;
import com.bigbang.playtunes.model.UploadSong;
import com.bigbang.playtunes.model.User;
import com.bigbang.playtunes.util.DebugLogger;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SongViewModel extends AndroidViewModel {

    private DatabaseReference dbReference;

    private MutableLiveData<Boolean> regMLD = new MutableLiveData<>();
    private MutableLiveData<Boolean> loginMLD = new MutableLiveData<>();

    private MutableLiveData<List<UploadSong>> uploadSongs = new MutableLiveData<>();

    public Boolean getUserLoggedIn() {
        if (FirebaseAuth.getInstance()
                .getCurrentUser() != null && FirebaseAuth.getInstance().getCurrentUser().isEmailVerified())
            return true;
        else
            return false;
    }

    public SongViewModel(@NonNull Application application) {
        super(application);

        dbReference = FirebaseDatabase.getInstance().getReference("songs");
    }

    public MutableLiveData<List<UploadSong>> getUploadSongs(){
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<UploadSong> songList = new ArrayList<>();
                for (DataSnapshot currentSnap : dataSnapshot.getChildren()){
                    UploadSong currentSong = currentSnap.getValue(UploadSong.class);
                    assert currentSong != null;
                    currentSong.setmKey(currentSnap.getKey());
                    songList.add(currentSong);
                }
                uploadSongs.setValue(songList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                DebugLogger.logError(databaseError.toException());
            }
        });
        return uploadSongs;
    }
    public void loginUser(User user) {
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(user.getUserName(), user.getUserPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isComplete() && task.isSuccessful()) {

                            if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                                loginMLD.setValue(true);
                            } else
                                Toast.makeText(getApplication(), R.string.verify_user_email + user.getUserName(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplication(), R.string.login_failed + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            loginMLD.setValue(false);
                        }
                    }
                });
    }

    public MutableLiveData<Boolean> getRegStatus() {
        return regMLD;
    }

    public MutableLiveData<Boolean> getLoginStatus() {
        return loginMLD;
    }

    public void registerUser(User user) {

        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(user.getUserName(), user.getUserPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isComplete() && task.isSuccessful()) {
                            Toast.makeText(getApplication(), R.string.user_reg_success, Toast.LENGTH_LONG).show();
                            FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
                            regMLD.setValue(true);
                        } else {

                            Toast.makeText(getApplication(), task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            regMLD.setValue(false);
                        }
                    }
                });
    }

}
