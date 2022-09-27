package com.govt.spm;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.ArrayList;

public class OfficerDashboardActivity extends AppCompatActivity {
    ImageButton btnMenuBar, btnSearch;
    EditText etSearch;

    ListView upcoming_company_list;
    UpcomingCompanyAdapter uca;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_officer_dashboard);
        btnMenuBar = (ImageButton) findViewById(R.id.btnBack);
        btnSearch = (ImageButton) findViewById(R.id.btnSearch);
        etSearch = (EditText) findViewById(R.id.etSearch);
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

        uca = new UpcomingCompanyAdapter(OfficerDashboardActivity.this,company_name,interview_date,register_end_date);
        upcoming_company_list.setAdapter(uca);

//        When click on menu bar button open popupmenu
        btnMenuBar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View view) {
                PopupMenu pm = new PopupMenu(getBaseContext(),btnMenuBar);
                pm.getMenuInflater().inflate(R.menu.tpo_dashboard_menu,pm.getMenu());
                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if(menuItem.getTitle().equals(getResources().getString(R.string.tpo_profile))){
                            Toast.makeText(OfficerDashboardActivity.this, getResources().getString(R.string.tpo_profile), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getBaseContext(),OfficerProfileActivity.class));
                        }
                        if(menuItem.getTitle().equals(getResources().getString(R.string.view_student))){
                            Toast.makeText(OfficerDashboardActivity.this, getResources().getString(R.string.view_student), Toast.LENGTH_SHORT).show();
                        }
                        if(menuItem.getTitle().equals(getResources().getString(R.string.manage_jobs))){
                            Toast.makeText(OfficerDashboardActivity.this, getResources().getString(R.string.manage_jobs), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getBaseContext(),ManageJobsActivity.class));
                        }
                        if(menuItem.getTitle().equals(getResources().getString(R.string.manage_activity))){
                            Toast.makeText(OfficerDashboardActivity.this, getResources().getString(R.string.manage_activity), Toast.LENGTH_SHORT).show();
                        }
                        if(menuItem.getTitle().equals(getResources().getString(R.string.logout))){
                            Toast.makeText(OfficerDashboardActivity.this, getResources().getString(R.string.logout), Toast.LENGTH_SHORT).show();
                        }

                        return true;
                    }
                });
                pm.setForceShowIcon(true);
                pm.show();
            }
        });
    }

}