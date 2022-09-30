package com.govt.spm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

public class ViewJobsActivity extends AppCompatActivity {
    ListView view_jobs_list;
    ViewJobsAdapter vja;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_jobs);
        view_jobs_list = (ListView) findViewById(R.id.view_jobs_list);

    //        view company list set
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

        vja = new ViewJobsAdapter(ViewJobsActivity.this,company_name,college_name,register_end_date);
        view_jobs_list.setAdapter(vja);

    }

    public void goToDashboard(View view) {
        finish();
    }
}