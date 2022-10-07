package com.govt.spm;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class StudentDashboardActivity extends AppCompatActivity {

    ListView upcoming_company_list;
    UpcomingJobsAdapter uca;
    ImageButton btnMenuBar,btnSearch;
    TextView tvJobsCount,tvAppliedInCount,tvSelectedInCount;

    private SharedPreferences userPref;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        upcoming_company_list = (ListView) findViewById(R.id.list_SD_upcoming_jobs);
        btnMenuBar = (ImageButton) findViewById(R.id.btnSDMenuBar);
        btnSearch = (ImageButton) findViewById(R.id.btnSDSearch);

        tvJobsCount = (TextView) findViewById(R.id.tvSDJobsCount);
        tvAppliedInCount = (TextView) findViewById(R.id.tvSDAppliedInCount);
        tvSelectedInCount = (TextView) findViewById(R.id.tvSDSelectedInCount);

        userPref = getSharedPreferences("user",MODE_PRIVATE);
        editor = userPref.edit();

        //Upcoming company list set
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

        uca = new UpcomingJobsAdapter(StudentDashboardActivity.this,company_name,college_name,register_end_date);
        upcoming_company_list.setAdapter(uca);

        //When click on menu bar button open popupmenu
        btnMenuBar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View view) {
                openPopUpMenu();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void openPopUpMenu(){
        PopupMenu pm = new PopupMenu(getBaseContext(),btnMenuBar);
        pm.getMenuInflater().inflate(R.menu.student_dashboard_menu,pm.getMenu());
        pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getTitle().equals(getResources().getString(R.string.student_profile))){
                    Toast.makeText(StudentDashboardActivity.this, getResources().getString(R.string.student_profile), Toast.LENGTH_SHORT).show();
                }
                if(menuItem.getTitle().equals(getResources().getString(R.string.view_jobs))){
                    Toast.makeText(StudentDashboardActivity.this, getResources().getString(R.string.view_jobs), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getBaseContext(),ViewJobsActivity.class));
                }
                if(menuItem.getTitle().equals(getResources().getString(R.string.applied_in))){
                    Toast.makeText(StudentDashboardActivity.this, getResources().getString(R.string.applied_in), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getBaseContext(),StudentAppliedActivity.class));
                }
                if(menuItem.getTitle().equals(getResources().getString(R.string.logout))){
                    Toast.makeText(StudentDashboardActivity.this, getResources().getString(R.string.logout), Toast.LENGTH_SHORT).show();
                    logout();
                }

                return true;
            }
        });
        pm.setForceShowIcon(true);
        pm.show();
    }
    private void logout() {
        editor.clear();
        editor.apply();
        finish();
    }
}