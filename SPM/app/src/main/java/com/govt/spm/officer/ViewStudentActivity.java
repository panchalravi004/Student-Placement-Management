package com.govt.spm.officer;

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
import com.govt.spm.adapter.StudentAdapter;
import com.govt.spm.viewmodel.StudentLiveViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class ViewStudentActivity extends AppCompatActivity {
    private ImageButton btnSearch;
    private EditText etSearch;
    private Spinner spFilterCollege,spFilterDept;

    private RecyclerView student_rv;
    private LinearLayoutManager manager;
    private StudentAdapter sa;
    private ProgressBar pbLoadMore;
    private TextView tvCount;

    private static final String TAG = "SPM_ERROR";
    private JSONArray jsonStudent;

    private Boolean isScrolling = false;
    private int currentItem,totalItem,scrolledOutItems,totaldbitem;
    private SharedPreferences userPref;

    private StudentLiveViewModel studentLiveViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_student);

        btnSearch = (ImageButton) findViewById(R.id.btnViewStudentSearch);
        etSearch = (EditText) findViewById(R.id.etViewStudentSearch);
//        spFilterCollege = (Spinner) findViewById(R.id.spViewStudentFilterOne);
//        spFilterDept = (Spinner) findViewById(R.id.spViewStudentFilterTwo);
        pbLoadMore = (ProgressBar) findViewById(R.id.pbLoadMore);
        tvCount = (TextView) findViewById(R.id.tvViewStudentResultCount);
        student_rv = (RecyclerView) findViewById(R.id.recycleViewStudent);
        manager = new LinearLayoutManager(this);
        userPref = getSharedPreferences("user",MODE_PRIVATE);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        totaldbitem = 10;

        //Call Method
        getStudents();

        //Listener
        //search the data
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!etSearch.getText().equals("")){
                    JSONArray searchedStudent =  filterBySearch(jsonStudent,etSearch.getText().toString());
                    tvCount.setText("Result : "+ searchedStudent.length()+" Found");
                    sa = new StudentAdapter(ViewStudentActivity.this,searchedStudent);
                    student_rv.setAdapter(sa);
                    student_rv.setLayoutManager(manager);
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                studentLiveViewModel.makeApiCall(ViewStudentActivity.this,userPref);
            }
        });

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

//                sid.add("1234560005");
//                sname.add("Jack Fusion");
//                ssem.add("MCA SEM 3 2022");
//                sa.notifyDataSetChanged();

                pbLoadMore.setVisibility(View.GONE);
            }
        }, 5000);
    }

    //get student list
    private void getStudents(){
        pbLoadMore.setVisibility(View.VISIBLE);

        studentLiveViewModel = new StudentLiveViewModel();

        sa = new StudentAdapter(ViewStudentActivity.this,jsonStudent);
        student_rv.setAdapter(sa);
        student_rv.setLayoutManager(manager);

        studentLiveViewModel.getStudent().observe(this, new Observer<JSONArray>() {
            @Override
            public void onChanged(JSONArray jsonArray) {
                pbLoadMore.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                if(jsonArray != null){
                    jsonStudent = jsonArray;
                    tvCount.setText("Result : "+ jsonStudent.length()+" Found");
                    sa.updateStudent(jsonArray);
                }
            }
        });
        studentLiveViewModel.makeApiCall(this,userPref);
    }

    //search the data by student name
    private JSONArray filterBySearch(JSONArray allStudent,String searchText){
        JSONArray result = new JSONArray();
        for (int i = 0; i < allStudent.length(); i++) {
            try {
                JSONObject jo = new JSONObject(allStudent.getString(i));
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

    //go back
    public void goToDashboard(View view) {
        finish();
    }
}