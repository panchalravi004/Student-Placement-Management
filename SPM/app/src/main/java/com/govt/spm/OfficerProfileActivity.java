package com.govt.spm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class OfficerProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_officer_profile);

    }

    public void goToDashboard(View view) {
        startActivity(new Intent(getBaseContext(),OfficerDashboardActivity.class));
    }
}