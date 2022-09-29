package com.govt.spm;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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

public class StudentDashboardActivity extends AppCompatActivity {
    ImageButton btnMenuBarr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        btnMenuBarr = (ImageButton) findViewById(R.id.btnMenuBarr);


        //        When click on menu bar button open popupmenu
    btnMenuBarr.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View view) {
                PopupMenu pm = new PopupMenu(getBaseContext(), btnMenuBarr);
                pm.getMenuInflater().inflate(R.menu.student_dashboard_menu, pm.getMenu());
                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getTitle().equals(getResources().getString(R.string.student_profile))) {
                            Toast.makeText(StudentDashboardActivity.this, getResources().getString(R.string.student_profile), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getBaseContext(), OfficerProfileActivity.class));
                        }
                        if (menuItem.getTitle().equals(getResources().getString(R.string.manage_jobs))) {
                            Toast.makeText(StudentDashboardActivity.this, getResources().getString(R.string.manage_jobs), Toast.LENGTH_SHORT).show();
                        }
                        if (menuItem.getTitle().equals(getResources().getString(R.string.manage_activity))) {
                            Toast.makeText(StudentDashboardActivity.this, getResources().getString(R.string.manage_activity), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getBaseContext(), ManageJobsActivity.class));
                        }
                        if (menuItem.getTitle().equals(getResources().getString(R.string.logout))) {
                            Toast.makeText(StudentDashboardActivity.this, getResources().getString(R.string.logout), Toast.LENGTH_SHORT).show();
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

