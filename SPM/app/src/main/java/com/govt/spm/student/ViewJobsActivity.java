package com.govt.spm.student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.govt.spm.R;
import com.govt.spm.adapter.ViewJobsAdapter;
import com.govt.spm.viewmodel.JobLiveViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class ViewJobsActivity extends AppCompatActivity {

    private EditText etSearch;
    private ImageButton btnSearch;
    private Spinner spFilterOne,spFilterTwo;
    private TextView tvResultCount;
    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView view_jobs_rv;
    private LinearLayoutManager manager;
    private ProgressBar pbLoadMore;
    private Boolean isScrolling = false;
    private ViewJobsAdapter vja;
    private JobLiveViewModel jobLiveViewModel;

    private int currentItem,totalItem,scrollOutItem,totalDBItem;
    private SharedPreferences userPref;
    private static final String TAG = "SPM_ERROR";
    private JSONArray jsonJob;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_jobs);
        view_jobs_rv = (RecyclerView) findViewById(R.id.recycleViewViewJobs);
        etSearch = (EditText) findViewById(R.id.etViewJobsSearch);
        btnSearch = (ImageButton) findViewById(R.id.btnViewJobsSearch);
        spFilterOne = (Spinner) findViewById(R.id.spViewJobsFilterOne);
        spFilterTwo = (Spinner) findViewById(R.id.spViewJobsFilterTwo);
        tvResultCount = (TextView) findViewById(R.id.tvViewJobsResultCount);
        pbLoadMore = (ProgressBar) findViewById(R.id.pbLoadMoreViewJobs);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        manager = new LinearLayoutManager(this);
        userPref = getSharedPreferences("user",MODE_PRIVATE);

        totalDBItem = 10;
        jsonJob = new JSONArray();
        //CALL METHOD
        getJobList();

        //LISTENER
        //search the data
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!etSearch.getText().equals("")){
                    JSONArray searchedJob =  filterBySearch(jsonJob,etSearch.getText().toString());
                    tvResultCount.setText("Result : "+searchedJob.length()+" Found");
                    jsonJob = searchedJob;
                    vja.updateJob(searchedJob);
                }
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                etSearch.setText("");
                jobLiveViewModel.makeApiCall(ViewJobsActivity.this,userPref);
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
//                company_name.add("ABCD Four Technology");
//                register_end_date.add("28-09-2022");
//                college_name.add("AMPICS");
                pbLoadMore.setVisibility(View.GONE);
            }
        }, 5000);
    }

    //get jobs list by university
    private void getJobList(){
        pbLoadMore.setVisibility(View.VISIBLE);
        jobLiveViewModel = new JobLiveViewModel();

        vja = new ViewJobsAdapter(ViewJobsActivity.this,jsonJob);
        view_jobs_rv.setAdapter(vja);
        view_jobs_rv.setLayoutManager(manager);

        jobLiveViewModel.getJob().observe(this, new Observer<JSONArray>() {
            @Override
            public void onChanged(JSONArray jsonArray) {
                pbLoadMore.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                if(jsonArray != null){
                    jsonJob = jsonArray;
                    vja.updateJob(jsonArray);
                    tvResultCount.setText("Result : "+jsonJob.length()+" Found");
                }
            }
        });
        jobLiveViewModel.makeApiCall(this,userPref);
    }

    //search the data by required skills of jobs post
    private JSONArray filterBySearch(JSONArray allJobs,String searchText){
        JSONArray result = new JSONArray();
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

    public void goToDashboard(View view) {
        finish();
    }
}