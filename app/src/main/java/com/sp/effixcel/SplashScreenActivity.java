package com.sp.effixcel;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private static final long SPLASH_SCREEN_DELAY = 3090; // 3.09 seconds
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Create a MediaPlayer instance and load the sound file from the raw directory
        mediaPlayer = MediaPlayer.create(this, R.raw.mysound);

        // Start playing the sound
        mediaPlayer.start();

        // Delay the launching of the next activity (e.g., MainActivity) using a Handler
        new Handler().postDelayed(() -> {
            // Stop the sound when the delay is over
            mediaPlayer.stop();
            mediaPlayer.release();

            // Start the MainActivity after the Splash Screen delay
            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(intent);

            // Finish the Splash Screen activity so that the user cannot navigate back to it
            finish();
        }, SPLASH_SCREEN_DELAY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release the MediaPlayer resources when the activity is destroyed
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}