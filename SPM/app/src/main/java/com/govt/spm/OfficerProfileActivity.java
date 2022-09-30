package com.govt.spm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

public class OfficerProfileActivity extends AppCompatActivity {

    private EditText etId,etEmail,etMobile,etName;
    private Spinner spGender,spUniversity,spCollege,spDept;
    private Button btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_officer_profile);
        etId = (EditText) findViewById(R.id.etTPOId);
        etEmail = (EditText) findViewById(R.id.etTPOEmail);
        etMobile = (EditText) findViewById(R.id.etTPOMobile);
        etName = (EditText) findViewById(R.id.etTPOName);

        spGender = (Spinner) findViewById(R.id.spTPOGender);
        spUniversity = (Spinner) findViewById(R.id.spTPOUniversity);
        spCollege = (Spinner) findViewById(R.id.spTPOCollege);
        spDept = (Spinner) findViewById(R.id.spTPODept);

        btnUpdate = (Button) findViewById(R.id.btnTPOUpdateProfile);
        //Update TPO Profile when click
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    updateTPOProfile();
                }
            }
        });
    }
    private void updateTPOProfile(){
        String tpoID = etId.getText().toString();
        String email = etEmail.getText().toString();
        String name = etName.getText().toString();
        String mobile = etMobile.getText().toString();
        String gender = spGender.getSelectedItem().toString();
        String university = spUniversity.getSelectedItem().toString();
        String college = spCollege.getSelectedItem().toString();
        String dept = spDept.getSelectedItem().toString();
        Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
    }

    private boolean validate(){

        String tpoID = etId.getText().toString();
        String email = etEmail.getText().toString();
        String name = etName.getText().toString();
        String mobile = etMobile.getText().toString();

        if (tpoID.equals("")) {
            Toast.makeText(this, "Please Enter TPO Id", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (email.equals("")) {
            Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (name.equals("")) {
            Toast.makeText(this, "Please Enter Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mobile.equals("")) {
            Toast.makeText(this, "Please Enter Mobile", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void goToDashboard(View view) {
        finish();
    }
}