package com.govt.spm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.material.badge.BadgeUtils;

public class CreateOfficerProfileActivity extends AppCompatActivity {
    private EditText etId,etUsername,etNumber;
    private Spinner spGender,spUniversity,spCollege,spDept;
    private Button btnCreate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_officer_profile);
        etId=(EditText) findViewById(R.id.etCreateTPOProfileID);
        etUsername=(EditText) findViewById(R.id.etCreateTPOProfileUsername);
        etNumber=(EditText) findViewById(R.id.etCreateTPOProfileNumber);

        spGender=(Spinner) findViewById(R.id.spCreateTPOProfileGender);
        spUniversity=(Spinner) findViewById(R.id.spCreateTPOProfileUniversity);
        spCollege=(Spinner) findViewById(R.id.spCreateTPOProfileCollege);
        spDept=(Spinner) findViewById(R.id.spCreateTPOProfileDept);

        btnCreate = (Button) findViewById(R.id.btnCreateTPOProfileCreate);


    }

    public void goToBack(View view) {
        finish();
    }
}