package com.govt.spm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class ManageJobsActivity extends AppCompatActivity {
    ImageButton btnSearch;
    EditText etSearch;
    Spinner spFilterOne,spFilterTwo;
    TextView tvResultCount;

    ListView jobs_list;
    JobsAdapter ja;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_jobs);
        btnSearch = (ImageButton) findViewById(R.id.btnManageJobsSearch);
        etSearch = (EditText) findViewById(R.id.etManageJobsSearch);
        spFilterOne = (Spinner) findViewById(R.id.spManageJobsFilterOne);
        spFilterTwo = (Spinner) findViewById(R.id.spManageJobsFilterTwo);
        tvResultCount = (TextView) findViewById(R.id.tvManageJobsResultCount);
        jobs_list = (ListView) findViewById(R.id.list_manage_jobs_job);

        //Jobs company list set
        ArrayList<String> company_name = new ArrayList<String>();
        ArrayList<String> college_name = new ArrayList<String>();
        ArrayList<String> register_end_date = new ArrayList<String>();

        company_name.add("ABCD One Technology");
        company_name.add("ABCD Two Technology");
        company_name.add("ABCD Three Technology");
        company_name.add("ABCD Four Technology");

        college_name.add("AMPICS");
        college_name.add("AMPICS");
        college_name.add("AMPICS");
        college_name.add("AMPICS");


        register_end_date.add("28-09-2022");
        register_end_date.add("29-09-2022");
        register_end_date.add("30-09-2022");
        register_end_date.add("30-09-2022");

        ja = new JobsAdapter(ManageJobsActivity.this,company_name,college_name,register_end_date);
        jobs_list.setAdapter(ja);
    }

    public void goToDashboard(View view) {
        finish();
    }

    public void goToAddJobs(View view) {
        startActivity(new Intent(ManageJobsActivity.this,AddJobsActivity.class));
    }
}