package com.govt.spm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.govt.spm.adapter.JobsAdapter;
import com.govt.spm.request.CacheRequest;
import com.govt.spm.viewmodel.JobLiveViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ManageJobsActivity extends AppCompatActivity {
    private ImageButton btnSearch,btnAdd,btnFilter,btnCloseFilter;
    private EditText etSearch;
    private TextView tvResultCount;
    private Spinner spCompany,spUniv,spCollege,spDept;
    private CheckBox cbOwnCompany;
    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView jobs_rv;
    private LinearLayoutManager manager;
    private ProgressBar pbLoadMore;
    private JobsAdapter ja;
    private JobLiveViewModel jobLiveViewModel;

    private Boolean isScrolling = false;
    private int currentItem;
    private int totalItem;
    private int scrollOutItem;
    private int totalDbItem;
    private SharedPreferences userPref;

    private static final String TAG = "SPM_ERROR";
    private JSONArray jsonJob,jsonCompany,jsonCollege,jsonDept;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_jobs);
        btnSearch = (ImageButton) findViewById(R.id.btnManageJobsSearch);
        btnFilter = (ImageButton) findViewById(R.id.btnManageJobsAddFilter);
        btnAdd = (ImageButton) findViewById(R.id.btnManageJobsAddJobs);
        etSearch = (EditText) findViewById(R.id.etManageJobsSearch);
        tvResultCount = (TextView) findViewById(R.id.tvManageJobsResultCount);
        pbLoadMore = (ProgressBar) findViewById(R.id.pbLoadMoreManageJobs);
        tvResultCount = (TextView) findViewById(R.id.tvManageJobsResultCount);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        jobs_rv = (RecyclerView) findViewById(R.id.recycleViewManageJobs);
        manager = new LinearLayoutManager(this);
        userPref = getSharedPreferences("user",MODE_PRIVATE);

        totalDbItem = 12;
        if(userPref.getString("CAN_MAKE_JOB_POST","CAN_MAKE_JOB_POST").equals("0")){
            btnAdd.setVisibility(View.GONE);
        }else{
            btnAdd.setVisibility(View.VISIBLE);
        }

        //CALL METHOD

        //LISTENER
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                etSearch.setText("");
                jobLiveViewModel.makeApiCall(ManageJobsActivity.this,userPref);
            }
        });

        //search the data
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!etSearch.getText().equals("")){
                    JSONArray searchedJob =  filterBySearch(jsonJob,etSearch.getText().toString());
                    tvResultCount.setText("Result : "+searchedJob.length()+" Found");
                    jsonJob = searchedJob;
                    ja.updateJob(searchedJob);
                }
            }
        });
        jobs_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItem = manager.getChildCount();
                totalItem = manager.getItemCount();
                scrollOutItem = manager.findFirstVisibleItemPosition();

                if(isScrolling && (currentItem + scrollOutItem == totalItem)){
                    isScrolling = false;
                    if(totalItem<=totalDbItem){
                        fetchData();
                    }
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getJobList();

    }

    private void fetchData(){
        pbLoadMore.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                pbLoadMore.setVisibility(View.GONE);
            }
        },5000);
    }

    //get all jobs list
    private void getJobList(){

        pbLoadMore.setVisibility(View.VISIBLE);
        jobLiveViewModel = new JobLiveViewModel();
        ja = new JobsAdapter(ManageJobsActivity.this,jsonJob);
        jobs_rv.setAdapter(ja);
        jobs_rv.setLayoutManager(manager);

        jobLiveViewModel.getJob().observe(this, new Observer<JSONArray>() {
            @Override
            public void onChanged(JSONArray jsonArray) {
                pbLoadMore.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                if(jsonArray != null){
                    jsonJob = jsonArray;
                    ja.updateJob(jsonArray);
                    tvResultCount.setText("Result : "+jsonJob.length()+" Found");
                    setFilter(jsonJob);
                }
            }
        });
        jobLiveViewModel.makeApiCall(this,userPref);
    }

    //Apply all filters here
    private void setFilter(JSONArray jbs){
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(ManageJobsActivity.this,R.style.DialogStyle);
                dialog.setContentView(R.layout.dialog_filter_job);
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.white_all_round);
                btnCloseFilter = dialog.findViewById(R.id.btnFilterGoBack);
                spCompany = dialog.findViewById(R.id.spJobFilterCompany);
                spUniv = dialog.findViewById(R.id.spJobFilterUniversity);
                spCollege = dialog.findViewById(R.id.spJobFilterCollege);
                spDept = dialog.findViewById(R.id.spJobFilterDept) ;
                cbOwnCompany = dialog.findViewById(R.id.cbFilterMyOwned);
                btnCloseFilter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.onSaveInstanceState();
                        dialog.dismiss();
                    }
                });
                //get and set company in spinner
                getCompanies();
                //get Colleges
                fetchColleges(userPref.getString("univ_id","univ_id"));

                //set filter on college and department
                spCollege.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        try {
                            if(String.valueOf(i).equals("0")){
                                //fetch all jobs from this university
                                tvResultCount.setText("Result : "+jbs.length()+" Found");
                                ja.updateJob(jbs);
                            }else{
                                JSONArray jsonCollegeJob = filterByCollege(jbs, new JSONObject(jsonCollege.getString(i - 1)).getString("college_id"), new JSONObject(jsonCollege.getString(i - 1)).getString("college_name"));
                                ja.updateJob(jsonCollegeJob);
                                //fetch college
                                fetchCollegeWiseDept(new JSONObject(jsonCollege.getString(i - 1)).getString("college_id"));
                                spDept.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        try {
                                            if (String.valueOf(i).equals("0")) {
                                                tvResultCount.setText("Result : " + jsonCollegeJob.length() + " Found");
                                                ja.updateJob(jsonCollegeJob);
                                            } else {
                                                JSONArray jsonDeptJob = filterByDept(jsonCollegeJob, new JSONObject(jsonDept.getString(i - 1)).getString("dept_id"), new JSONObject(jsonDept.getString(i - 1)).getString("dept_name"));
                                                tvResultCount.setText("Result : " + jsonDeptJob.length() + " Found");
                                                ja.updateJob(jsonDeptJob);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {}
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {}
                });

                //set filter on company
                spCompany.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()  {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if (String.valueOf(i).equals("0")) {
                            tvResultCount.setText("Result : "+jbs.length()+" Found");
                            ja.updateJob(jbs);
                        }else{
                            try {
                                JSONArray temp = filterByCompany(jbs,new JSONObject(jsonCompany.getString(i-1)).getString("company_id"));
                                tvResultCount.setText("Result : "+temp.length()+" Found");
                                ja.updateJob(temp);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {}
                });

                //filter by : my own jobs
                cbOwnCompany.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b){
                            JSONArray ownJob =  filterByOwnJobs(jbs);
                            tvResultCount.setText("Result : "+ownJob.length()+" Found");
                            ja.updateJob(ownJob);
                        }else{
                            tvResultCount.setText("Result : "+jbs.length()+" Found");
                            ja.updateJob(jbs);
                        }
                    }
                });

                dialog.show();
            }
        });
    }

    //get Company list
    private void getCompanies(){
        CacheRequest request = new CacheRequest(
                Request.Method.POST,
                Constants.GET_COMPANIES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "getCompanies: "+response);
                        try {
                            ArrayList<String> company = new ArrayList<>();
                            company.add(0,"ALL");
                            jsonCompany = new JSONArray(response);

                            for(int i=0;i<jsonCompany.length();i++){
                                JSONObject jo = new JSONObject(jsonCompany.getString(i));
                                company.add(jo.getString("company_name"));
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ManageJobsActivity.this, android.R.layout.simple_spinner_dropdown_item,company);
                            spCompany.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "getCompanies: "+error.getMessage());
                    }
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(ManageJobsActivity.this);
        requestQueue.add(request);
    }

    //Fetch colleges as per university
    private void fetchColleges(String univ_id){
        CacheRequest request = new CacheRequest(
                Request.Method.POST,
                Constants.GET_COLLEGES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "fetchColleges: "+response);
                        try {
                            ArrayList<String> collegesList = new ArrayList<>();
                            ArrayAdapter<String> collegesAdapter = new ArrayAdapter<String>(ManageJobsActivity.this, android.R.layout.simple_spinner_dropdown_item,collegesList);
                            spCollege.setAdapter(collegesAdapter);
                            jsonCollege = new JSONArray(response);

                            if(new JSONObject(jsonCollege.getString(0)).has("error")){
                                if(new JSONObject(jsonCollege.getString(0)).getBoolean("error")){
                                    collegesList.clear();
                                    collegesList.add(new String("There is no Data"));
                                }
                            }else{
                                collegesList.clear();
                                collegesList.add(0,"ALL");
                                for(int i=0;i<jsonCollege.length();i++){
                                    JSONObject jo = new JSONObject(jsonCollege.getString(i));
                                    collegesList.add(jo.getString("college_name"));
                                }
                            }
                            collegesAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "fetchColleges: "+error.getMessage());
                    }
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("univ_id",univ_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ManageJobsActivity.this);
        requestQueue.add(request);
    }

    //Fetch Dept as per Colleges
    private void fetchCollegeWiseDept(String college_id){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.GET_DEPARTMENTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "fetchCollegeWiseDept: "+response);
                        try {
                            ArrayList<String> deptsList = new ArrayList<>();
                            ArrayAdapter<String> deptsAdapter = new ArrayAdapter<String>(ManageJobsActivity.this, android.R.layout.simple_spinner_dropdown_item,deptsList);
                            spDept.setAdapter(deptsAdapter);
                            jsonDept = new JSONArray(response);

                            if(new JSONObject(jsonDept.getString(0)).has("error")){
                                if(new JSONObject(jsonDept.getString(0)).getBoolean("error")){
                                    deptsList.clear();
                                    deptsList.add(new String("There is no Data"));
                                }
                            }else{
                                deptsList.clear();
                                deptsList.add(0,"ALL");
                                for(int i=0;i<jsonDept.length();i++){
                                    JSONObject jo = new JSONObject(jsonDept.getString(i));
                                    deptsList.add(jo.getString("dept_name"));
                                }
                            }
                            deptsAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "fetchCollegeWiseDept: "+error.getMessage());
                    }
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("college_id",college_id);
                return params;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(ManageJobsActivity.this);
        requestQueue.add(request);
    }

    //filterByCheckBox
    private JSONArray filterByOwnJobs(JSONArray allJobs){
        JSONArray result = new JSONArray();
//        Log.i(TAG, "filterByOwnJobs: "+allJobs);
        for (int i = 0; i < allJobs.length(); i++) {
            try {
                JSONObject jo = new JSONObject(allJobs.getString(i));
                if(jo.getString("creator_id").equals(userPref.getString("user_id","user_id"))){
                    result.put(jo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    //search the data
    private JSONArray filterBySearch(JSONArray allJobs,String searchText){
        JSONArray result = new JSONArray();
//        Log.i(TAG, "filterByOwnJobs: "+allJobs);
        for (int i = 0; i < allJobs.length(); i++) {
            try {
                JSONObject jo = new JSONObject(allJobs.getString(i));
                String searchTextLower = searchText.toLowerCase(Locale.ROOT);
                String skillsLower = jo.getString("skills").toLowerCase(Locale.ROOT);

                if(skillsLower.contains(searchTextLower)){
                    result.put(jo);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    //filter by the company
    private JSONArray filterByCompany(JSONArray allJobs,String company_id){
        JSONArray result = new JSONArray();
        for (int i = 0; i < allJobs.length(); i++) {
            try {
                JSONObject jo = new JSONObject(allJobs.getString(i));
                if(jo.getString("company_id").equals(company_id)){
                    result.put(jo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    //filter by the college
    private JSONArray filterByCollege(JSONArray allJobs,String college_id,String college_name){
        if(college_name.equals("ALL")){
            return allJobs;
        }
        JSONArray result = new JSONArray();
        for (int i = 0; i < allJobs.length(); i++) {
            try {
                JSONObject jo = new JSONObject(allJobs.getString(i));
                if(jo.getString("college_id").equals(college_id)){
                    result.put(jo);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    //filter by the dept
    private JSONArray filterByDept(JSONArray allJobs,String dept_id,String dept_name){
        if(dept_name.equals("ALL")){
            return allJobs;
        }
//        Log.i(TAG, "filterByDept: "+dept_id);
        JSONArray result = new JSONArray();
        for (int i = 0; i < allJobs.length(); i++) {
            try {
                JSONObject jo = new JSONObject(allJobs.getString(i));
                if(jo.getString("dept_id").equals(dept_id)){
                    result.put(jo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public void goToDashboard(View view) {
        finish();
    }

    public void goToAddJobs(View view) {
        Intent i = new Intent(ManageJobsActivity.this,AddJobsActivity.class);
        i.putExtra("ACTION","ADD");
        startActivity(i);
    }
}