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
import android.widget.EditText;
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
    private UpcomingJobsAdapter uca;
    private SharedPreferences userPref;
    private SharedPreferences.Editor editor;
    private static final String TAG = "SPM_ERROR";

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
//        view_jobs_rv = (RecyclerView) findViewById(R.id.list_OD_upcoming_jobs);
        manager = new LinearLayoutManager(this);
        userPref = getSharedPreferences("user",MODE_PRIVATE);
        editor = userPref.edit();

        jsonJob = new JSONArray();

        //CALL METHOD
//        getJobList(userPref.getString("univ_id","univ_id"));

        //When click on menu bar button open popupmenu
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
        getJobList(userPref.getString("univ_id","univ_id"));
        getStudentsCount();
        getCompaniesCount();
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

    private void getJobList(String univ_id){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.GET_JOB_LIST,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: "+response);
                        try {
                            jsonJob = new JSONArray(response);
                            //set total job count
                            tvJobsCount.setText(String.valueOf(jsonJob.length()));
                            //get coming soon job list
                            tvComingSoonCount.setText(String.valueOf(getComingSoonJobCount(jsonJob)));
                            JSONArray sorted = new JSONArray();
                            List list = new ArrayList();
                            for(int i = 0; i < jsonJob.length(); i++) {
                                list.add(jsonJob.getJSONObject(i));
                            }
//                            Log.i(TAG, "onResponse: unsorted "+jsonJob);

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
//                            Log.i(TAG, "onResponse: sorted "+sorted);


                            uja = new UpcomingJobsAdapter(OfficerDashboardActivity.this,sorted);
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
                        Log.i(TAG, "onErrorResponse: "+error.getMessage());
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
        RequestQueue requestQueue = Volley.newRequestQueue(OfficerDashboardActivity.this);
        requestQueue.add(request);
    }

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

    private void getStudentsCount(){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.GET_STUDENT_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray js = new JSONArray(response);
                            tvStudentCount.setText(String.valueOf(js.length()));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "onErrorResponse: "+error.getMessage());
                    }
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("univ_id",userPref.getString("univ_id","univ_id"));
                param.put("college_id",userPref.getString("college_id","college_id"));
                param.put("dept_id",userPref.getString("dept_id","dept_id"));

                return param;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(OfficerDashboardActivity.this);
        requestQueue.add(request);
    }

    private void getCompaniesCount(){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.GET_COMPANIES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jc = new JSONArray(response);
                            tvCompanyCount.setText(String.valueOf(jc.length()));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "onErrorResponse: "+error.getMessage());
                    }
                }
        );
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(OfficerDashboardActivity.this);
        requestQueue.add(request);
    }

    private void logout() {
        editor.clear();
        editor.apply();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

}