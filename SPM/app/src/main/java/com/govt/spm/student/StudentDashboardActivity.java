package com.govt.spm.student;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.govt.spm.LoginActivity;
import com.govt.spm.R;
import com.govt.spm.adapter.UpcomingJobsAdapter;
import com.govt.spm.viewmodel.AppliedJobLiveViewModel;
import com.govt.spm.viewmodel.JobLiveViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StudentDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private RecyclerView view_jobs_rv;
    private LinearLayoutManager manager;
    private ImageButton btnMenuBar,btnGoTOAppliedJob;
    private TextView tvJobsCount,tvAppliedInCount,tvSelectedInCount;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private SharedPreferences userPref;
    private SharedPreferences.Editor editor;
    private static final String TAG = "SPM_ERROR";

    private JSONArray jsonJob;
    private UpcomingJobsAdapter uja;
    private JobLiveViewModel jobLiveViewModel;
    private AppliedJobLiveViewModel appliedJobLiveViewModel;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        view_jobs_rv = (RecyclerView) findViewById(R.id.list_SD_upcoming_jobs);
        btnMenuBar = (ImageButton) findViewById(R.id.btnSDMenuBar);
        tvJobsCount = (TextView) findViewById(R.id.tvSDJobsCount);
        tvAppliedInCount = (TextView) findViewById(R.id.tvSDAppliedInCount);
        tvSelectedInCount = (TextView) findViewById(R.id.tvSDSelectedInCount);
        btnGoTOAppliedJob = (ImageButton) findViewById(R.id.btnSDSelectedIn);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        jsonJob = new JSONArray();
        manager = new LinearLayoutManager(this);
        userPref = getSharedPreferences("user",MODE_PRIVATE);
        editor = userPref.edit();

        navigationView.setNavigationItemSelectedListener(this);

        setNavHeader();

        //Listeners
        btnMenuBar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        btnGoTOAppliedJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StudentDashboardActivity.this,StudentAppliedActivity.class));
            }
        });
    }

    public void setNavHeader(){

        TextView studentName = navigationView.getHeaderView(0).findViewById(R.id.tvStudentName);
        TextView studentEnroll = navigationView.getHeaderView(0).findViewById(R.id.tvStudentEnroll);
        studentName.setText(userPref.getString("name","name"));
        studentEnroll.setText(userPref.getString("stud_id","stud_id"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //CALL METHOD
        getJobList();
        getAppliedJobList(userPref.getString("stud_id","stud_id"));
    }

    //get jobs list and set counts of jobs
    private void getJobList(){
        jobLiveViewModel = new JobLiveViewModel();
        uja = new UpcomingJobsAdapter(StudentDashboardActivity.this,jsonJob);
        view_jobs_rv.setAdapter(uja);
        view_jobs_rv.setLayoutManager(manager);

        jobLiveViewModel.getJob().observe(this, new Observer<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChanged(JSONArray jsonArray) {
                if(jsonArray != null){
                    jsonJob = jsonArray;
                    //set total job count
                    tvJobsCount.setText(String.valueOf(jsonJob.length()));

                    JSONArray sorted = new JSONArray();
                    List list = new ArrayList();
                    for(int i = 0; i < jsonJob.length(); i++) {
                        try {
                            list.add(jsonJob.getJSONObject(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Collections.sort(list, new Comparator<JSONObject>() {
                        private static final String KEY_NAME = "reg_end_date";
                        @Override
                        public int compare(JSONObject a, JSONObject b) {
                            String str1 = new String();
                            String str2 = new String();
                            try {
                                str1 = (String)a.get(KEY_NAME);
                                str2 = (String)b.get(KEY_NAME);
                            } catch(JSONException e) {
                                e.printStackTrace();
                            }
                            return str1.compareTo(str2);
                        }
                    });

                    for(int i = 0; i < jsonJob.length(); i++) {
                        sorted.put(list.get(i));
                    }
                    jsonJob = sorted;
                    uja.updateJob(sorted);
                }
            }
        });
        jobLiveViewModel.makeApiCall(this,userPref);
    }

    //get applied list count
    private void getAppliedJobList(String stud_id){
        appliedJobLiveViewModel = new AppliedJobLiveViewModel();

        appliedJobLiveViewModel.getAppliedJob().observe(this, new Observer<JSONArray>() {
            @Override
            public void onChanged(JSONArray jsonArray) {
                if(jsonArray != null){
                    //set applied job list count
                    tvAppliedInCount.setText(String.valueOf(jsonArray.length()));
                    //get applied job list count and set
                    tvSelectedInCount.setText(String.valueOf(filterGetSelected(jsonArray)));
                }
            }
        });
        appliedJobLiveViewModel.makeApiCall(this,userPref);
    }

    //get selected job list counts
    private int filterGetSelected(JSONArray jsonJob){
        int count = 0;
        for (int i = 0; i < jsonJob.length(); i++) {
            try {
                JSONObject jo = new JSONObject(jsonJob.getString(i));
                if(jo.getString("HasGotJobOffer").equals("1")){
                    count++;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return count;
    }

    //logout the current user
    private void logout() {
        editor.clear();
        editor.apply();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals(getResources().getString(R.string.student_profile))){
            Toast.makeText(StudentDashboardActivity.this, getResources().getString(R.string.student_profile), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getBaseContext(),StudentProfileActivity.class));
        }
        if(item.getTitle().equals(getResources().getString(R.string.view_jobs))){
            Toast.makeText(StudentDashboardActivity.this, getResources().getString(R.string.view_jobs), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getBaseContext(),ViewJobsActivity.class));
        }
        if(item.getTitle().equals(getResources().getString(R.string.applied_in))){
            Toast.makeText(StudentDashboardActivity.this, getResources().getString(R.string.applied_in), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getBaseContext(),StudentAppliedActivity.class));
        }
        if(item.getTitle().equals(getResources().getString(R.string.logout))){
            Toast.makeText(StudentDashboardActivity.this, getResources().getString(R.string.logout), Toast.LENGTH_SHORT).show();
            logout();
        }

        return true;
    }
}