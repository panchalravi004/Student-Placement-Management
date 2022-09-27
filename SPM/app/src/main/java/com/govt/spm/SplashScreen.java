package com.govt.spm;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        startActivity(new Intent(this,MainActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        startActivity(new Intent(this,OfficerDashboardActivity.class));
    }
}
