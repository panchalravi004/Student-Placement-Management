package com.govt.spm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
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
import com.govt.spm.adapter.AppliedJobAdapter;
import com.govt.spm.viewmodel.AppliedJobLiveViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StudentAppliedActivity extends AppCompatActivity {
    private EditText etSearch;
    private ImageButton btnSearch;
    private Spinner spFilterOne,spFilterTwo;
    private TextView tvResultCount;
    private CheckBox cbApproved,cbSelected;
    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView view_jobs_rv;
    private LinearLayoutManager manager;
    private ProgressBar pbLoadMore;
    private Boolean isScrolling = false;
    private AppliedJobAdapter vja;
    private AppliedJobLiveViewModel appliedJobLiveViewModel;

    private int currentItem,totalItem,scrollOutItem,totalDBItem;
    private SharedPreferences userPref;
    private static final String TAG = "SPM_ERROR";
    private JSONArray jsonJob;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_applied);
        view_jobs_rv = (RecyclerView) findViewById(R.id.recycleViewStudentApplied);
        etSearch = (EditText) findViewById(R.id.etStudentAppliedSearch);
        btnSearch = (ImageButton) findViewById(R.id.btnStudentAppliedSearch);
        spFilterOne = (Spinner) findViewById(R.id.spStudentAppliedFilterOne);
        spFilterTwo = (Spinner) findViewById(R.id.spStudentAppliedFilterTwo);
        tvResultCount = (TextView) findViewById(R.id.tvStudentAppliedResultCount);
        pbLoadMore = (ProgressBar) findViewById(R.id.pbLoadMoreStudentApplied);
        manager = new LinearLayoutManager(this);
        userPref = getSharedPreferences("user",MODE_PRIVATE);
        cbApproved = (CheckBox) findViewById(R.id.cbJobApproved);
        cbSelected = (CheckBox) findViewById(R.id.cbJobSelectedIn);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        totalDBItem = 10;
        jsonJob = new JSONArray();

        //CALL METHOD
        getAppliedJobList(userPref.getString("stud_id","stud_id"));
        setFilters();

        //LISTENER
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                etSearch.setText("");
                cbApproved.setChecked(false);
                cbSelected.setChecked(false);
                appliedJobLiveViewModel.makeApiCall(StudentAppliedActivity.this,userPref);
            }
        });
        view_jobs_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    if(totalItem<=totalDBItem){
                        fetchData();
                    }
                }
            }
        });
    }

    private void fetchData() {
        pbLoadMore.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //company_name.add("ABCD Four Technology");
                //register_end_date.add("28-09-2022");
                //college_name.add("AMPICS");
                pbLoadMore.setVisibility(View.GONE);
            }
        }, 5000);
    }

    //fetch the applied job list of current student
    private void getAppliedJobList(String stud_id){
        pbLoadMore.setVisibility(View.VISIBLE);

        appliedJobLiveViewModel = new AppliedJobLiveViewModel();
        vja = new AppliedJobAdapter(StudentAppliedActivity.this,jsonJob);
        view_jobs_rv.setAdapter(vja);
        view_jobs_rv.setLayoutManager(manager);

        appliedJobLiveViewModel.getAppliedJob().observe(this, new Observer<JSONArray>() {
            @Override
            public void onChanged(JSONArray jsonArray) {
                pbLoadMore.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                if(jsonArray != null){
                    jsonJob = jsonArray;
                    tvResultCount.setText("Result : "+jsonJob.length()+" Found");
                    vja.updateJob(jsonArray);
                }
            }
        });
        appliedJobLiveViewModel.makeApiCall(this,userPref);
    }

    //set the filters like search , approved , selected
    //on checkbox and search
    private void setFilters(){
        //search the data
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!etSearch.getText().equals("")){
                    JSONArray searchedJob =  filterBySearch(jsonJob,etSearch.getText().toString());
                    tvResultCount.setText("Result : "+searchedJob.length()+" Found");
                    vja.updateJob(searchedJob);
                }
            }
        });

        //get approved jobs
        cbApproved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    JSONArray temp = filterGetApproved(jsonJob);
                    tvResultCount.setText("Result : "+temp.length()+" Found");
                    vja.updateJob(temp);
                }else{

                    tvResultCount.setText("Result : "+jsonJob.length()+" Found");
                    vja.updateJob(jsonJob);
                }
            }
        });
        //get selected in  jobs
        cbSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    JSONArray temp = filterGetSelected(jsonJob);
                    tvResultCount.setText("Result : "+temp.length()+" Found");
                    vja.updateJob(temp);
                }else{

                    tvResultCount.setText("Result : "+jsonJob.length()+" Found");
                    vja.updateJob(jsonJob);
                }
            }
        });
    }

    //search the data by required skill of job
    private JSONArray filterBySearch(JSONArray allJobs,String searchText){
        JSONArray result = new JSONArray();
        for (int i = 0; i < allJobs.length(); i++) {
            try {
                JSONObject jo = new JSONObject(allJobs.getString(i));
                String searchTextLower = searchText.toLowerCase(Locale.ROOT);
                String skillsLower = jo.getString("SKILLS").toLowerCase(Locale.ROOT);

                if(skillsLower.contains(searchTextLower)){
                    result.put(jo);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    //filter by approve application
    private JSONArray filterGetApproved(JSONArray jsonJob){
        JSONArray result = new JSONArray();
        for (int i = 0; i < jsonJob.length(); i++) {
            try {
                JSONObject jo = new JSONObject(jsonJob.getString(i));
                if(jo.getString("AppStat").equals("APPROVED")){
                    result.put(jo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return result;
    }

    //filter selected student
    private JSONArray filterGetSelected(JSONArray jsonJob){
        JSONArray result = new JSONArray();
        for (int i = 0; i < jsonJob.length(); i++) {
            try {
                JSONObject jo = new JSONObject(jsonJob.getString(i));
                if(jo.getString("HasGotJobOffer").equals("1")){
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
}