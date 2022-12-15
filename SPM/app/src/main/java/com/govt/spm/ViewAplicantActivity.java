package com.govt.spm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.govt.spm.adapter.ViewAplicantAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ViewAplicantActivity extends AppCompatActivity {

    private EditText etSearch;
    private ImageButton btnSearch;
    private TextView tvTitle;
    private CheckBox isSelected;
    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView view_jobs_rv;
    private LinearLayoutManager manager;
    private ProgressBar pbLoadMore;
    private Boolean isScrolling = false;
    private ViewAplicantAdapter vaa;

    private int currentItem,totalItem,scrollOutItem,totalDBItem;
    private SharedPreferences userPref;
    private static final String TAG = "SPM_ERROR";
    private JSONArray jsonJob;
    private Intent myInt;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_aplicant);
        view_jobs_rv = (RecyclerView) findViewById(R.id.recycleViewAplicant);
        etSearch = (EditText) findViewById(R.id.etAplicantSearch);
        btnSearch = (ImageButton) findViewById(R.id.btnAplicantSearch);
        pbLoadMore = (ProgressBar) findViewById(R.id.pbLoadMoreAplicant);
        tvTitle = (TextView) findViewById(R.id.tvViewAplicantTitle);
        manager = new LinearLayoutManager(this);
        userPref = getSharedPreferences("user",MODE_PRIVATE);
        isSelected = (CheckBox) findViewById(R.id.cbViewAplicantSelected);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        myInt = getIntent();
        totalDBItem = 10;
        jsonJob = new JSONArray();


        //CALL METHOD
        getJobApplicant(myInt.getStringExtra("JOB_ID"));

        //LISTENER
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getJobApplicant(myInt.getStringExtra("JOB_ID"));
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

    //get job applicant list
    private void getJobApplicant(String job_id){
        pbLoadMore.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.GET_JOB_APLICANT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "getJobApplicant: "+response);
                        swipeRefreshLayout.setRefreshing(false);
                        try {
                            pbLoadMore.setVisibility(View.GONE);
                            jsonJob = new JSONArray(response);

                            TextView te = (TextView) findViewById(R.id.tvViewAplicantResultCount);
                            te.setText("Result : "+jsonJob.length()+" Found");
                            vaa = new ViewAplicantAdapter(ViewAplicantActivity.this,jsonJob);
                            view_jobs_rv.setAdapter(vaa);
                            view_jobs_rv.setLayoutManager(manager);

                            //set filter
                            setFilter();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "getJobApplicant: "+error.getMessage());
                    }
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("job_id",job_id);
                return param;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(ViewAplicantActivity.this);
        requestQueue.add(request);
    }

    //set filter
    private void setFilter(){
        TextView te = (TextView) findViewById(R.id.tvViewAplicantResultCount);
        isSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){
                    JSONArray temp = filterGetSelected(jsonJob);
                    te.setText("Result : "+temp.length()+" Found");
                    vaa = new ViewAplicantAdapter(ViewAplicantActivity.this,temp);
                    view_jobs_rv.setAdapter(vaa);
                    view_jobs_rv.setLayoutManager(manager);
                    vaa.notifyDataSetChanged();
                }else{

                    te.setText("Result : "+jsonJob.length()+" Found");
                    vaa = new ViewAplicantAdapter(ViewAplicantActivity.this,jsonJob);
                    view_jobs_rv.setAdapter(vaa);
                    view_jobs_rv.setLayoutManager(manager);
                    vaa.notifyDataSetChanged();


                }

            }
        });

        //search the data
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!etSearch.getText().equals("")){
                    JSONArray searchedStudent =  filterBySearch(jsonJob,etSearch.getText().toString());
                    te.setText("Result : "+searchedStudent.length()+" Found");
                    vaa = new ViewAplicantAdapter(ViewAplicantActivity.this,searchedStudent);
                    view_jobs_rv.setAdapter(vaa);
                    view_jobs_rv.setLayoutManager(manager);
                    vaa.notifyDataSetChanged();
                }
            }
        });

    }

    //filter selected student
    private JSONArray filterGetSelected(JSONArray jsonJob){
        JSONArray result = new JSONArray();

        for (int i = 0; i < jsonJob.length(); i++) {
            try {
                JSONObject jo = new JSONObject(jsonJob.getString(i));
                if(jo.getString("is_placed").equals("1")){
                    result.put(jo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return result;
    }

    //filter by search
    private JSONArray filterBySearch(JSONArray allJobs,String searchText){
        JSONArray result = new JSONArray();
        Log.i(TAG, "filterByOwnJobs: "+allJobs);
        for (int i = 0; i < allJobs.length(); i++) {
            try {
                JSONObject jo = new JSONObject(allJobs.getString(i));
                String searchTextLower = searchText.toLowerCase(Locale.ROOT);
                String nameLower = jo.getString("stud_name").toLowerCase(Locale.ROOT);

                if(nameLower.contains(searchTextLower)){
                    result.put(jo);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public void goToBack(View view) {
        finish();
    }
}