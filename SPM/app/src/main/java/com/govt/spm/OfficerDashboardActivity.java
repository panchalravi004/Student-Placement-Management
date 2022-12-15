package com.govt.spm;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.govt.spm.adapter.CompanyAdapter;
import com.govt.spm.adapter.JobsAdapter;
import com.govt.spm.adapter.StudentAdapter;
import com.govt.spm.adapter.UpcomingJobsAdapter;
import com.govt.spm.viewmodel.CompanyLiveViewModel;
import com.govt.spm.viewmodel.JobLiveViewModel;
import com.govt.spm.viewmodel.StudentLiveViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OfficerDashboardActivity extends AppCompatActivity {

    private ImageButton btnMenuBar;
    private TextView tvStudentCount,tvCompanyCount,tvJobsCount,tvComingSoonCount;
    private RecyclerView view_jobs_rv;
    private LinearLayoutManager manager;
    private SharedPreferences userPref;
    private SharedPreferences.Editor editor;
    private static final String TAG = "SPM_ERROR";
    private JobLiveViewModel jobLiveViewModel;
    private StudentLiveViewModel studentLiveViewModel;
    private CompanyLiveViewModel companyLiveViewModel;

    private JSONArray jsonJob;
    private UpcomingJobsAdapter uja;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_officer_dashboard);

        btnMenuBar = (ImageButton) findViewById(R.id.btnODMenuBar);

        tvStudentCount = (TextView) findViewById(R.id.tvODStudentCount);
        tvCompanyCount = (TextView) findViewById(R.id.tvODCompanyCount);
        tvJobsCount = (TextView) findViewById(R.id.tvODJobsCount);
        tvComingSoonCount = (TextView) findViewById(R.id.tvODComingSoonCount);

        view_jobs_rv = (RecyclerView) findViewById(R.id.list_OD_upcoming_company);
        manager = new LinearLayoutManager(this);
        userPref = getSharedPreferences("user",MODE_PRIVATE);
        editor = userPref.edit();
        jsonJob = new JSONArray();

        //Listener
        btnMenuBar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View view) {
                openPopUpMenu();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //CALL METHOD
        getJobList();
        getStudentsCount();
        getCompaniesCount();
    }

    //open pop up menu
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

    //get job list and set
    //this will set a list on sorted order
    //by registration end date
    private void getJobList(){

        jobLiveViewModel = new JobLiveViewModel();
        uja = new UpcomingJobsAdapter(OfficerDashboardActivity.this,jsonJob);
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
                    //get coming soon job list
                    tvComingSoonCount.setText(String.valueOf(getComingSoonJobCount(jsonJob)));

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

    //get coming soon list
    //in this list, we got list of jobs which haven't start registration yet.
    @RequiresApi(api = Build.VERSION_CODES.O)
    private int getComingSoonJobCount(JSONArray ja){
        int count = 0;
        LocalDate date = LocalDate.now();
        for (int i = 0; i < ja.length(); i++) {
            try {
                JSONObject jo = new JSONObject(ja.getString(i));
                if(LocalDate.parse(jo.getString("reg_start_date")).compareTo(date) >= 0){
                    count++;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return count;
    }

    //get student count
    private void getStudentsCount(){
        studentLiveViewModel = new StudentLiveViewModel();

        studentLiveViewModel.getStudent().observe(this, new Observer<JSONArray>() {
            @Override
            public void onChanged(JSONArray jsonArray) {
                if(jsonArray != null){
                    tvStudentCount.setText(String.valueOf(jsonArray.length()));
                }else{
                    tvStudentCount.setText("0");
                }
            }
        });
        studentLiveViewModel.makeApiCall(this,userPref);
    }

    //get company count
    private void getCompaniesCount(){
        companyLiveViewModel = new CompanyLiveViewModel();
        companyLiveViewModel.getCompany().observe(this, new Observer<JSONArray>() {
            @Override
            public void onChanged(JSONArray jsonArray) {
                if(jsonArray != null){
                    tvCompanyCount.setText(String.valueOf(jsonArray.length()));
                }else{
                    tvCompanyCount.setText("0");
                }
            }
        });
        companyLiveViewModel.makeApiCall(this,null);
    }

    //logout the current user
    private void logout() {
        editor.clear();
        editor.apply();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

}