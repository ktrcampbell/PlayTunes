package com.bigbang.playtunes.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bigbang.playtunes.R;
import com.bigbang.playtunes.model.User;
import com.bigbang.playtunes.util.DebugLogger;
import com.bigbang.playtunes.viewmodel.SongViewModel;
import com.google.android.material.textfield.TextInputLayout;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegistrationActivity extends AppCompatActivity {

    @BindView(R.id.reg_username_edittext)
    EditText usernameEditText;

    @BindView(R.id.reg_password_edittext)
    EditText passwordEditText;

    @BindView(R.id.reg_button)
    Button registerButton;

    @BindView(R.id.reg_tologin_button)
    Button existLoginButton;

    private SongViewModel viewModel;
    private Observer<Boolean> regObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);

        regObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                handleRegistration(aBoolean);
            }
        };

        viewModel = ViewModelProviders.of(this).get(SongViewModel.class);
        registerButton.setOnClickListener(view -> {
            registerUser();
        });
    }

    private void handleRegistration(Boolean aBoolean) {
        if(aBoolean){
            Toast.makeText(getApplicationContext(), R.string.reg_check_verify_email, Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
    }

    private void registerUser() {
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
        viewModel.registerUser(loginUser);
        viewModel.getRegStatus().observe(this, regObserver);

    }
}
