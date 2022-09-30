package com.govt.spm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    SharedPreferences userPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        startActivity(new Intent(this,MainActivity.class));
        userPref = getSharedPreferences("user",MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(userPref.contains("userId")){
            if(userPref.getString("role","role").equals("TPO")){
                startActivity(new Intent(this,OfficerDashboardActivity.class));
            }
            else if(userPref.getString("role","role").equals("STUDENT")){
                startActivity(new Intent(this,StudentDashboardActivity.class));
            }

        }else{
            startActivity(new Intent(this,LoginActivity.class));
        }
    }
}
