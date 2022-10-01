package com.govt.spm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddJobsActivity extends AppCompatActivity {

    private EditText etDescription,etRole,etSkill,etSSC,etHSC,etUG,etPG,etMinQualification,etStartDate,etEndDate;
    private Spinner spCompany,spUniversity,spCollege,spDept,spStatus;
    private Button btnAddNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_jobs);
        etDescription = (EditText) findViewById(R.id.etAddJobDescription);
        etRole = (EditText) findViewById(R.id.etAddJobRole);
        etSkill = (EditText) findViewById(R.id.etAddJobSkills);
        etSSC = (EditText) findViewById(R.id.etAddJobSSC);
        etHSC = (EditText) findViewById(R.id.etAddJobHSC);
        etUG = (EditText) findViewById(R.id.etAddJobUG);
        etPG = (EditText) findViewById(R.id.etAddJobPG);
        etMinQualification = (EditText) findViewById(R.id.etAddJobMinQualification);
        etStartDate = (EditText) findViewById(R.id.etAddJobStartDate);
        etEndDate = (EditText) findViewById(R.id.etAddJobEndDate);

        spCompany = (Spinner) findViewById(R.id.spAddJobCompany);
        spUniversity = (Spinner) findViewById(R.id.spAddJobUniversity);
        spCollege = (Spinner) findViewById(R.id.spAddJobCollege);
        spDept = (Spinner) findViewById(R.id.spAddJobDept);
        spStatus = (Spinner) findViewById(R.id.spAddJobStatus);

        btnAddNew = (Button) findViewById(R.id.btnAddJobAddNew);

        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNewJobPost();
            }
        });
    }


    private void AddNewJobPost(){
        String Description = etDescription.getText().toString();
        String Role = etRole.getText().toString();
        String Skill = etSkill.getText().toString();
        String SSC = etSSC.getText().toString();
        String HSC = etHSC.getText().toString();
        String UG = etUG.getText().toString();
        String PG = etPG.getText().toString();
        String MinQualification = etMinQualification.getText().toString();
        String StartDate = etStartDate.getText().toString();
        String EndDate = etEndDate.getText().toString();

        String Company = spCompany.getSelectedItem().toString();
        String University = spUniversity.getSelectedItem().toString();
        String College = spCollege.getSelectedItem().toString();
        String Dept = spDept.getSelectedItem().toString();
        String Status = spStatus.getSelectedItem().toString();

        if(validate()){

        }
    }
    private boolean validate(){

        if(etDescription.getText().toString().equals("")){
            Toast.makeText(this, "Please Enter Description", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(etRole.getText().toString().equals("")){
            Toast.makeText(this, "Please Enter Role", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(etSkill.getText().toString().equals("")){
            Toast.makeText(this, "Please Enter Skill", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(etSSC.getText().toString().equals("")){
            Toast.makeText(this, "Please Enter SSC Score", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(etHSC.getText().toString().equals("")){
            Toast.makeText(this, "Please Enter HSC Score", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(etUG.getText().toString().equals("")){
            Toast.makeText(this, "Please Enter UG Score", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(etPG.getText().toString().equals("")){
            Toast.makeText(this, "Please Enter PG Score", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(etMinQualification.getText().toString().equals("")){
            Toast.makeText(this, "Please Enter Min. Qualification", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(etStartDate.getText().toString().equals("")){
            Toast.makeText(this, "Please Enter Start Date", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(etEndDate.getText().toString().equals("")){
            Toast.makeText(this, "Please Enter End Date", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void goToDashboard(View view) {
        finish();
    }
}