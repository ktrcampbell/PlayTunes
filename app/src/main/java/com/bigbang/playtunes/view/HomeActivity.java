package com.bigbang.playtunes.view;

import androidx.appcompat.app.AppCompatActivity;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigbang.playtunes.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bigbang.playtunes.util.Constants.SPLASH_SCREEN;

public class HomeActivity extends AppCompatActivity {

    private Animation topAnim, bottomAnim;

    @BindView(R.id.app_title_textview)
    TextView appTitleTextView;

    @BindView(R.id.splash_imageview)
    ImageView splashImageView;

    @BindView(R.id.copyright_textview)
    TextView copyrightTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        splashImageView.setAnimation(topAnim);
        appTitleTextView.setAnimation(bottomAnim);
        copyrightTextView.setAnimation(bottomAnim);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
//                startActivity(intent);
//                finish();
            Pair[] pairs = new Pair[2];
            pairs[0] = new Pair<View, String>(splashImageView, "logo_image");
            pairs[1] = new Pair<View, String>(appTitleTextView, "logo_text");

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation
                    (HomeActivity.this, pairs);
            startActivity(intent, options.toBundle());
        },SPLASH_SCREEN);
    }
}