package com.govt.spm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class ManageJobsActivity extends AppCompatActivity {
    ImageButton btnSearch;
    EditText etSearch;
    Spinner spFilterOne,spFilterTwo;
    TextView tvResultCount;

    RecyclerView jobs_rv;
    LinearLayoutManager manager;
    ProgressBar pbLoadMore;
    JobsAdapter ja;

    Boolean isScrolling = false;
    int currentItem;
    int totalItem;
    int scrollOutItem;
    int totalDbItem;

    ArrayList<String> company_name;
    ArrayList<String> college_name;
    ArrayList<String> register_end_date;
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

        //Jobs company list set
        company_name = new ArrayList<String>();
        college_name = new ArrayList<String>();
        register_end_date = new ArrayList<String>();

        company_name.add("ABCD One Technology");
        company_name.add("ABCD Two Technology");
        company_name.add("ABCD Three Technology");
        company_name.add("ABCD Four Technology");
        company_name.add("ABCD One Technology");
        company_name.add("ABCD Two Technology");
        company_name.add("ABCD Three Technology");
        company_name.add("ABCD Four Technology");

        college_name.add("AMPICS");
        college_name.add("AMPICS");
        college_name.add("AMPICS");
        college_name.add("AMPICS");
        college_name.add("AMPICS");
        college_name.add("AMPICS");
        college_name.add("AMPICS");
        college_name.add("AMPICS");

        register_end_date.add("28-09-2022");
        register_end_date.add("29-09-2022");
        register_end_date.add("30-09-2022");
        register_end_date.add("30-09-2022");
        register_end_date.add("28-09-2022");
        register_end_date.add("29-09-2022");
        register_end_date.add("30-09-2022");
        register_end_date.add("30-09-2022");

        ja = new JobsAdapter(ManageJobsActivity.this,company_name,college_name,register_end_date);
        jobs_rv.setAdapter(ja);
        jobs_rv.setLayoutManager(manager);
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

                company_name.add("ABCD Five Technology");
                register_end_date.add("28-09-2022");
                college_name.add("AMPICS");

                pbLoadMore.setVisibility(View.GONE);
            }
        },5000);
    }

    public void goToDashboard(View view) {
        finish();
    }

    public void goToAddJobs(View view) {
        startActivity(new Intent(ManageJobsActivity.this,AddJobsActivity.class));
    }
}