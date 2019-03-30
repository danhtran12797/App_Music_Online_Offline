package com.danhtran12797.thd.app_music2019.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.danhtran12797.thd.app_music2019.R;

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
