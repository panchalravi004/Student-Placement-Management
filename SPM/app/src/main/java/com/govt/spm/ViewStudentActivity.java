package com.govt.spm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class ViewStudentActivity extends AppCompatActivity {
    ImageButton btnSearch;
    EditText etSearch;
    Spinner spFilterOne,spFilterTwo;

    RecyclerView student_rv;
    LinearLayoutManager manager;
    StudentAdapter sa;
    ProgressBar pbLoadMore;
    ArrayList<String> sid;
    ArrayList<String> sname;
    ArrayList<String> ssem;

    Boolean isScrolling = false;
    int currentItem,totalItem,scrolledOutItems,totaldbitem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_student);

        btnSearch = (ImageButton) findViewById(R.id.btnViewStudentSearch);
        etSearch = (EditText) findViewById(R.id.etViewStudentSearch);
        spFilterOne = (Spinner) findViewById(R.id.spViewStudentFilterOne);
        spFilterTwo = (Spinner) findViewById(R.id.spViewStudentFilterTwo);
        pbLoadMore = (ProgressBar) findViewById(R.id.pbLoadMore);

        student_rv = (RecyclerView) findViewById(R.id.recycleViewStudent);
        manager = new LinearLayoutManager(this);


        totaldbitem = 10;

        sid = new ArrayList<String>();
        sname = new ArrayList<String>();
        ssem = new ArrayList<String>();

        sid.add("1234560001");
        sid.add("1234560002");
        sid.add("1234560003");
        sid.add("1234560004");
        sid.add("1234560001");
        sid.add("1234560002");
        sid.add("1234560003");
        sid.add("1234560004");

        sname.add("Ravi Panchal");
        sname.add("Richesh Gaurav");
        sname.add("Priyank Sukhadiya");
        sname.add("Banti Shrivastav");
        sname.add("Ravi Panchal");
        sname.add("Richesh Gaurav");
        sname.add("Priyank Sukhadiya");
        sname.add("Banti Shrivastav");

        ssem.add("MCA SEM 3 2022");
        ssem.add("MCA SEM 3 2022");
        ssem.add("MCA SEM 3 2022");
        ssem.add("MCA SEM 3 2022");
        ssem.add("MCA SEM 3 2022");
        ssem.add("MCA SEM 3 2022");
        ssem.add("MCA SEM 3 2022");
        ssem.add("MCA SEM 3 2022");

        sa = new StudentAdapter(ViewStudentActivity.this,sid,sname,ssem);
        student_rv.setAdapter(sa);
        student_rv.setLayoutManager(manager);
        student_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                scrolledOutItems = manager.findFirstVisibleItemPosition();
                if(isScrolling && (currentItem + scrolledOutItems == totalItem)){
                    isScrolling = false;
                    if(totalItem<=totaldbitem){
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

                sid.add("1234560005");
                sname.add("Jack Fusion");
                ssem.add("MCA SEM 3 2022");
                sa.notifyDataSetChanged();

                pbLoadMore.setVisibility(View.GONE);
            }
        }, 5000);
    }

    public void goToDashboard(View view) {
        finish();
    }
}