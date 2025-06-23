package com.govt.spm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.govt.spm.officer.OfficerDashboardActivity;
import com.govt.spm.student.StudentDashboardActivity;

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
        if(userPref.contains("user_id") || userPref.contains("stud_id")){
            if(userPref.getString("role","role").equals("FACULTY")){
                startActivity(new Intent(this, OfficerDashboardActivity.class));
            }
            else if(userPref.getString("role","role").equals("STUDENT")){
                startActivity(new Intent(this, StudentDashboardActivity.class));
            }

        }else{
            startActivity(new Intent(this,LoginActivity.class));
        }
    }
}
