package com.bigbang.playtunes.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.playtunes.R;
import com.bigbang.playtunes.model.User;
import com.bigbang.playtunes.viewmodel.SongViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.io.PrintWriter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.new_user_button)
    Button registerButton;

    @BindView(R.id.sigin_button)
    Button loginButton;

    @BindView(R.id.forget_password_button)
    Button forgetPasswordButton;

    @BindView(R.id.login_imageview)
    ImageView loginImageView;

    @BindView(R.id.login_header_textview)
    TextView loginHeaderTextView;

    @BindView(R.id.siginin_textview)
    TextView loginSloganTextView;

    @BindView(R.id.username_edittext)
    EditText usernameEditText;

    @BindView(R.id.password_edittext)
    EditText passwordEditText;

    @BindView(R.id.username_text_inputlayout)
    TextInputLayout usernameTextInputLayout;

    @BindView(R.id.password_text_inputlayout)
    TextInputLayout passwordTextInputLayout;

    private SongViewModel viewModel;
    private Observer<Boolean> loginObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        viewModel = ViewModelProviders.of(this).get(SongViewModel.class);

        loginObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                handleLogin(aBoolean);
            }
        };

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });

        registerButton.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
//                startActivity(intent);

            Pair[] pairs = new Pair[7];
            pairs[0] = new Pair<View, String>(loginImageView, "logo_image");
            pairs[1] = new Pair<View, String>(loginHeaderTextView, "logo_text");
            pairs[2] = new Pair<View, String>(loginSloganTextView, "slogan_text");
            pairs[3] = new Pair<View, String>(usernameTextInputLayout, "username_trans");
            pairs[4] = new Pair<View, String>(passwordTextInputLayout, "password_trans");
            pairs[5] = new Pair<View, String>(loginButton, "signin_button_trans");
            pairs[6] = new Pair<View, String>(registerButton, "login_button_trans");

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation
                    (LoginActivity.this, pairs);
            startActivity(intent, options.toBundle());
        });

        forgetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle(R.string.reset_password);
                passwordResetDialog.setMessage(R.string.reset_password_message);
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mail = resetMail.getText().toString();
                        FirebaseAuth.getInstance().sendPasswordResetEmail(mail)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(LoginActivity.this,
                                        R.string.reset_link_message,
                                        Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this,
                                        getString(R.string.reset_link_error) + e,
                                        Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                passwordResetDialog.create().show();
            }
        });
    }

    private void handleLogin(Boolean aBoolean) {
        if(aBoolean){
            startActivity(new Intent(getApplicationContext(), UploadActivity.class));
            finish();
        }
    }

    private void userLogin() {
        String userName = usernameEditText.getText().toString().trim();
        String passWord = passwordEditText.getText().toString().trim();

        if(TextUtils.isEmpty(userName)){
            usernameEditText.setError("Username is required.");
            return;
        }
        if(TextUtils.isEmpty(passWord)){
            passwordEditText.setError("Password is required.");
            return;
        }

        if(passWord.length() < 6){
            passwordEditText.setError("Password must be >= 6 characters.");
            return;
        }
        User loginUser = new User(userName, passWord);
        viewModel.loginUser(loginUser);
        viewModel.getLoginStatus().observe(this, loginObserver);
    }
}
