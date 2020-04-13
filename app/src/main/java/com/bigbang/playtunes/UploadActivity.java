package com.bigbang.playtunes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.playtunes.model.UploadSong;
import com.bigbang.playtunes.util.DebugLogger;
import com.bigbang.playtunes.view.HomeActivity;
import com.bigbang.playtunes.view.LoginFragment;
import com.bigbang.playtunes.view.ShowSongsActivity;
import com.bigbang.playtunes.viewmodel.SongViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bigbang.playtunes.util.Constants.REQUEST_CODE;

public class UploadActivity extends AppCompatActivity {

    private boolean checkPermission = false;
    LoginFragment loginFragment = new LoginFragment();
    private SongViewModel songViewModel;
    private Uri audioUri;
    private StorageReference mStorageRef;
    private StorageTask mUploadTask;
    private DatabaseReference firebaseReferenceSongs;

    @BindView(R.id.song_title_edittext)
    EditText songTitleEditText;

    @BindView(R.id.upload_textview)
    TextView uploadTextView;

    @BindView(R.id.upload_progressbar)
    ProgressBar uploadProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        ButterKnife.bind(this);

        firebaseReferenceSongs = FirebaseDatabase.getInstance().getReference().child("songs");
        mStorageRef = FirebaseStorage.getInstance().getReference().child("songs");

        songViewModel = ViewModelProviders.of(this).get(SongViewModel.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.playlist_item:
                Intent i = new Intent(UploadActivity.this, ShowSongsActivity.class);
                startActivity(i);
                return true;
            case R.id.logout_item:
                //logout();
                Toast.makeText(this, "logout", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean validatePermissions() {
        Dexter.withContext(UploadActivity.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        checkPermission = true;
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                        permissionToken.continuePermissionRequest();
                    }
                }).check();
        return checkPermission;
    }



    public void openAudioFile(View v){
        if(validatePermissions()) {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.setType("audio/*");
            startActivityForResult(i, REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getData() != null) {
            if (requestCode == REQUEST_CODE) {
                audioUri = data.getData();
                String filename = getFileName(audioUri);
                uploadTextView.setText(filename);

            }
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if(uri.getScheme().equals(getString(R.string.content))){
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }finally
            {
                cursor.close();
            }
        }

        if (result == null){
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if(cut != -1){
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public void uploadAudioToFirebase(View v){
        if (uploadTextView.getText().toString().equals(getString(R.string.no_upload_textview))){
            Toast.makeText(getApplicationContext(), R.string.select_image, Toast.LENGTH_LONG).show();
        }else {
            if (mUploadTask != null && mUploadTask.isInProgress()) {
                Toast.makeText(getApplicationContext(), R.string.upload_in_progress, Toast.LENGTH_LONG).show();
            } else {
                uploadFile();
                Toast.makeText(this, R.string.upload_success_message, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void uploadFile() {
        String durationText;
        if(audioUri != null){
            Toast.makeText(getApplicationContext(), R.string.upload_mesage, Toast.LENGTH_LONG).show();
            uploadProgressBar.setVisibility(View.VISIBLE);
            StorageReference storageReference = mStorageRef.child(System.currentTimeMillis() +
                    "." + getFileExtension(audioUri));

            int durationInMillisec = findSongDuration(audioUri);
            if(durationInMillisec == 0){
                durationText = getString(R.string.n_a);
            }
            durationText = getDurationFromMillisec(durationInMillisec);
            final String fDurationText = durationText;

            mUploadTask = storageReference.putFile(audioUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    UploadSong uploadSong = new UploadSong(songTitleEditText.getText().toString(),
                                            fDurationText, uri.toString());

                                    String uploadId = firebaseReferenceSongs.push().getKey();
                                    firebaseReferenceSongs.child(uploadId).setValue(uploadSong);

                                }
                            });

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            uploadProgressBar.setProgress((int) progress);

                        }
                    });
        }
        else{
            Toast.makeText(getApplicationContext(), R.string.no_file_upload_text, Toast.LENGTH_LONG).show();
        }
    }

    private String getDurationFromMillisec(int durationInMillisec) {
        Date date = new Date(durationInMillisec);
        SimpleDateFormat df = new SimpleDateFormat(getString(R.string.date_format_text), Locale.US);
        return df.format(date);

    }

    private int findSongDuration(Uri audioUri) {
        int timeInMillisec = 0;
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, audioUri);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            timeInMillisec = Integer.parseInt(time);
            retriever.release();
            return timeInMillisec;
        }catch (Exception e){
            DebugLogger.logError(e);
        }
        return 0;
    }

    private String getFileExtension(Uri audioUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(audioUri));
    }

}


