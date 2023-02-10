package com.anonymouscreations.sathurangam.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.anonymouscreations.sathurangam.LocalData.LocalUserData;
import com.anonymouscreations.sathurangam.R;
import com.anonymouscreations.sathurangam.database.UserData;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getSupportActionBar().hide();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(alreadyLoggedIn())
                    startActivity(new Intent(SplashScreenActivity.this, HomeActivity.class));
                else
                    startActivity(new Intent(SplashScreenActivity.this,LoginActivity.class));
                finish();
            }
        },2000);
    }

    boolean alreadyLoggedIn(){
        UserData data = new LocalUserData(getApplicationContext()).getUserData();
        if(data != null && data.getEmail().length()>0 && data.getPassword().length()>0 && data.getName().length() > 0)
            return true;
        return false;
    }
}