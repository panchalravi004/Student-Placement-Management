package com.govt.spm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class StudentDashboardActivity extends AppCompatActivity {

    ListView upcoming_company_list;
    UpcomingCompanyAdapter uca;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        upcoming_company_list = (ListView) findViewById(R.id.upcoming_company_list);

//        Upcoming company list set
        ArrayList<String> company_name = new ArrayList<String>();
        ArrayList<String> interview_date = new ArrayList<String>();
        ArrayList<String> register_end_date = new ArrayList<String>();

        company_name.add("ABCD One Technology");
        company_name.add("ABCD Two Technology");
        company_name.add("ABCD Three Technology");
        company_name.add("ABCD Four Technology");

        interview_date.add("02-10-2022");
        interview_date.add("03-10-2022");
        interview_date.add("04-10-2022");
        interview_date.add("04-10-2022");

        register_end_date.add("28-09-2022");
        register_end_date.add("29-09-2022");
        register_end_date.add("30-09-2022");
        register_end_date.add("30-09-2022");

        uca = new UpcomingCompanyAdapter(StudentDashboardActivity.this,company_name,interview_date,register_end_date);
        upcoming_company_list.setAdapter(uca);
    }
}