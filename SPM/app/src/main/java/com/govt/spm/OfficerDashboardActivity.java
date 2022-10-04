package com.govt.spm;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class OfficerDashboardActivity extends AppCompatActivity {

    private ImageButton btnMenuBar, btnSearch, btnSelectedStudent;
    private EditText etSearch;
    private TextView tvStudentCount,tvCompanyCount,tvJobsCount,tvComingSoonCount;
    private ListView upcoming_company_list;
    private UpcomingJobsAdapter uca;
    private SharedPreferences userPref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_officer_dashboard);

        btnMenuBar = (ImageButton) findViewById(R.id.btnODMenuBar);
        btnSearch = (ImageButton) findViewById(R.id.btnODSearch);
        btnSelectedStudent = (ImageButton) findViewById(R.id.btnODSelectedStudent);

        tvStudentCount = (TextView) findViewById(R.id.tvODStudentCount);
        tvCompanyCount = (TextView) findViewById(R.id.tvODCompanyCount);
        tvJobsCount = (TextView) findViewById(R.id.tvODJobsCount);
        tvComingSoonCount = (TextView) findViewById(R.id.tvODComingSoonCount);

        etSearch = (EditText) findViewById(R.id.etODSearch);
        upcoming_company_list = (ListView) findViewById(R.id.list_OD_upcoming_company);

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

        uca = new UpcomingJobsAdapter(OfficerDashboardActivity.this,company_name,college_name,register_end_date);
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
                    startActivity(new Intent(getBaseContext(),ViewStudentActivity.class));
                }
                if(menuItem.getTitle().equals(getResources().getString(R.string.manage_jobs))){
                    Toast.makeText(OfficerDashboardActivity.this, getResources().getString(R.string.manage_jobs), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getBaseContext(),ManageJobsActivity.class));
                }
                if(menuItem.getTitle().equals(getResources().getString(R.string.manage_company))){
                    Toast.makeText(OfficerDashboardActivity.this, getResources().getString(R.string.manage_company), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getBaseContext(),ManageCompanyActivity.class));
                }
                if(menuItem.getTitle().equals(getResources().getString(R.string.logout))){
                    Toast.makeText(OfficerDashboardActivity.this, getResources().getString(R.string.logout), Toast.LENGTH_SHORT).show();
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