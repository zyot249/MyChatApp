package com.shyn.zyot.wind.mychatapp;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        int SPLASH_TIME_OUT = 1500;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LauncherActivity.this, StartActivity.class));
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
