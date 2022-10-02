package com.govt.spm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.badge.BadgeUtils;

public class CreateOfficerActivity extends AppCompatActivity {
    private EditText etTPOId,etTPOEmail,etTPOPassword;
    private Button btnCreate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_officer);
        etTPOId = (EditText) findViewById(R.id.etCreateTPOID);
        etTPOEmail = (EditText) findViewById(R.id.etCreateTPOEmail);
        etTPOPassword = (EditText) findViewById(R.id.etCreateTPOPassword);
        btnCreate = (Button) findViewById(R.id.btnCreateTPOCreate);
    }

    public void goToBack(View view) {
        finish();
    }
}