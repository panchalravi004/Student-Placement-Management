package com.govt.spm.officer;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.govt.spm.DialogLoading;
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

    private RecyclerView student_rv;
    private LinearLayoutManager manager;
    private StudentAdapter sa;
    private ProgressBar pbLoadMore;
    private TextView tvCount;

    private static final String TAG = "SPM_ERROR";
    private JSONArray jsonStudent;

    private SharedPreferences userPref;

    private StudentLiveViewModel studentLiveViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DialogLoading dialogLoading;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_student);

        btnSearch = (ImageButton) findViewById(R.id.btnViewStudentSearch);
        etSearch = (EditText) findViewById(R.id.etViewStudentSearch);
        pbLoadMore = (ProgressBar) findViewById(R.id.pbLoadMore);
        tvCount = (TextView) findViewById(R.id.tvViewStudentResultCount);
        student_rv = (RecyclerView) findViewById(R.id.recycleViewStudent);
        manager = new LinearLayoutManager(this);
        userPref = getSharedPreferences("user",MODE_PRIVATE);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        dialogLoading = new DialogLoading(this);

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
                    sa.updateStudent(searchedStudent);
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                etSearch.setText("");
                studentLiveViewModel.makeApiCall(ViewStudentActivity.this,userPref);
            }
        });
    }

    //get student list
    private void getStudents(){
        dialogLoading.show();
        studentLiveViewModel = new StudentLiveViewModel();

        sa = new StudentAdapter(ViewStudentActivity.this,jsonStudent);
        student_rv.setAdapter(sa);
        student_rv.setLayoutManager(manager);

        studentLiveViewModel.getStudent().observe(this, new Observer<JSONArray>() {
            @Override
            public void onChanged(JSONArray jsonArray) {
                swipeRefreshLayout.setRefreshing(false);
                dialogLoading.dismiss();
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