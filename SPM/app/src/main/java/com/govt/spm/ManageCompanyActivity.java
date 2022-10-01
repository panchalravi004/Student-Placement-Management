package com.govt.spm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.util.ArrayList;

public class ManageCompanyActivity extends AppCompatActivity {
    EditText etSearch;
    ImageButton btnSearch;
    Spinner spFilterOne,spFilterTwo;

    RecyclerView company_rv;
    LinearLayoutManager manager;
    ProgressBar pbLoadMore;
    Boolean isScrolling = false;

    int currentItem,totalItem,scrollOutItem,totalDBItem;
    ArrayList<String> cname;
    CompanyAdapter ca;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_company);

        btnSearch = (ImageButton) findViewById(R.id.btnManageCompanySearch);
        etSearch = (EditText) findViewById(R.id.etManageCompanySearch);
        spFilterOne = (Spinner) findViewById(R.id.spManageCompanyFilterOne);
        spFilterTwo = (Spinner) findViewById(R.id.spManageCompanyFilterTwo);
        pbLoadMore = (ProgressBar) findViewById(R.id.pbLoadMoreManageCompany);

        company_rv = (RecyclerView) findViewById(R.id.recycleViewManageCompany);
        manager = new LinearLayoutManager(this);
        totalDBItem = 12;

        cname = new ArrayList<String>();
        cname.add("Info it Solution One");
        cname.add("Info it Solution Two");
        cname.add("Info it Solution One");
        cname.add("Info it Solution Two");
        cname.add("Info it Solution One");
        cname.add("Info it Solution Two");
        cname.add("Info it Solution One");
        cname.add("Info it Solution Two");


        ca = new CompanyAdapter(ManageCompanyActivity.this,cname);
        company_rv.setAdapter(ca);
        company_rv.setLayoutManager(manager);
        company_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                cname.add("Info it Solution New One");
                cname.add("Info it Solution New Two");
                pbLoadMore.setVisibility(View.GONE);
            }
        },5000);
    }

    public void goToDashboard(View view) {
        finish();
    }

    public void goToAddCompany(View view) {
        startActivity(new Intent(ManageCompanyActivity.this,AddCompanyActivity.class));
    }
}