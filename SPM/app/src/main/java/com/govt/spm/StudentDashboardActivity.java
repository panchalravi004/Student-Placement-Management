package com.govt.spm;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.ListView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentDashboardActivity extends AppCompatActivity {

    private RecyclerView view_jobs_rv;
    private LinearLayoutManager manager;
    private ImageButton btnMenuBar,btnGoTOAppliedJob;
    private TextView tvJobsCount,tvAppliedInCount,tvSelectedInCount;

    private SharedPreferences userPref;
    private SharedPreferences.Editor editor;
    private static final String TAG = "SPM_ERROR";

    private JSONArray jsonJob;
    private UpcomingJobsAdapter uja;
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

        jsonJob = new JSONArray();
        manager = new LinearLayoutManager(this);
        userPref = getSharedPreferences("user",MODE_PRIVATE);
        editor = userPref.edit();

        //Listeners
        btnMenuBar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View view) {
                openPopUpMenu();
            }
        });
        btnGoTOAppliedJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StudentDashboardActivity.this,StudentAppliedActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //CALL METHOD
        getJobList(userPref.getString("univ_id","univ_id"));
        getAppliedJobList(userPref.getString("stud_id","stud_id"));
    }

    //open Pop Up Menu Bar
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void openPopUpMenu(){
        PopupMenu pm = new PopupMenu(getBaseContext(),btnMenuBar);
        pm.getMenuInflater().inflate(R.menu.student_dashboard_menu,pm.getMenu());
        pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getTitle().equals(getResources().getString(R.string.student_profile))){
                    Toast.makeText(StudentDashboardActivity.this, getResources().getString(R.string.student_profile), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getBaseContext(),StudentProfileActivity.class));
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

    //get jobs list and set counts of jobs
    private void getJobList(String univ_id){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.GET_JOB_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "getJobList: "+response);
                        try {
                            jsonJob = new JSONArray(response);
                            //Set Counts of Jobs
                            tvJobsCount.setText(String.valueOf(jsonJob.length()));

                            //Add JSONObject in list
                            JSONArray sorted = new JSONArray();
                            List list = new ArrayList();
                            for(int i = 0; i < jsonJob.length(); i++) {
                                list.add(jsonJob.getJSONObject(i));
                            }
                            //Log.i(TAG, "getJobList: unsorted "+jsonJob);
                            //Sort the list by the registration end date
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
                            //Now add all values(JSONObject) in sorted JSONArray
                            for(int i = 0; i < jsonJob.length(); i++) {
                                sorted.put(list.get(i));
                            }
                            //Log.i(TAG, "getJobList: sorted "+sorted);

                            uja = new UpcomingJobsAdapter(StudentDashboardActivity.this,sorted);
                            view_jobs_rv.setAdapter(uja);
                            view_jobs_rv.setLayoutManager(manager);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "getJobList: "+error.getMessage());
                    }
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("univ_id",univ_id);
                return param;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(StudentDashboardActivity.this);
        requestQueue.add(request);
    }

    //get applied list count
    private void getAppliedJobList(String stud_id){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.GET_APPLIED_JOB_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "getAppliedJobList: "+response);
                        try {
                            JSONArray jsonAppliedJob = new JSONArray(response);
                            //set applied job list count
                            tvAppliedInCount.setText(String.valueOf(jsonAppliedJob.length()));
                            //get applied job list count and set
                            tvSelectedInCount.setText(String.valueOf(filterGetSelected(jsonAppliedJob)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "getAppliedJobList: "+error.getMessage());
                    }
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("stud_id",stud_id);
                return param;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(StudentDashboardActivity.this);
        requestQueue.add(request);
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
        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }
}