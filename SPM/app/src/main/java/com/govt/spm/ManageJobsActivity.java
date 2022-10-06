package com.govt.spm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

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

public class ManageJobsActivity extends AppCompatActivity {
    private ImageButton btnSearch;
    private EditText etSearch;
    private Spinner spFilterOne,spFilterTwo;
    private TextView tvResultCount;

    private RecyclerView jobs_rv;
    private LinearLayoutManager manager;
    private ProgressBar pbLoadMore;
    private JobsAdapter ja;

    private Boolean isScrolling = false;
    private int currentItem;
    private int totalItem;
    private int scrollOutItem;
    private int totalDbItem;

    private static final String TAG = "SPM_ERROR";
    private JSONArray jsonJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_jobs);
        btnSearch = (ImageButton) findViewById(R.id.btnManageJobsSearch);
        etSearch = (EditText) findViewById(R.id.etManageJobsSearch);
        spFilterOne = (Spinner) findViewById(R.id.spManageJobsFilterOne);
        spFilterTwo = (Spinner) findViewById(R.id.spManageJobsFilterTwo);
        tvResultCount = (TextView) findViewById(R.id.tvManageJobsResultCount);
        pbLoadMore = (ProgressBar) findViewById(R.id.pbLoadMoreManageJobs);

        jobs_rv = (RecyclerView) findViewById(R.id.recycleViewManageJobs);
        manager = new LinearLayoutManager(this);

        totalDbItem = 12;

        //CALL METHOD
        getJobList();
        //LISTENER
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

    private void fetchData(){
        pbLoadMore.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                pbLoadMore.setVisibility(View.GONE);
            }
        },5000);
    }

    private void getJobList(){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.GET_JOB_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: "+response);
                        try {
                            jsonJob = new JSONArray(response);
                            TextView count = (TextView) findViewById(R.id.tvManageJobsResultCount);
                            count.setText("Result : "+jsonJob.length()+" Found");
                            ja = new JobsAdapter(ManageJobsActivity.this,jsonJob);
                            jobs_rv.setAdapter(ja);
                            jobs_rv.setLayoutManager(manager);
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
        RequestQueue requestQueue = Volley.newRequestQueue(ManageJobsActivity.this);
        requestQueue.add(request);
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