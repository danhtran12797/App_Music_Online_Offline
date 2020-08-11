package com.danhtran12797.thd.app_music_test.Activity;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;


public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.come_on_activity);

        Intent intent = new Intent(this, LoadActivity.class);
        startActivity(intent);
        finish();
    }
}
