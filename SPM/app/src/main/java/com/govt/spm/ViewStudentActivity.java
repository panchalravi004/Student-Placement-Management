package com.govt.spm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import java.util.HashMap;
import java.util.Map;

public class ViewStudentActivity extends AppCompatActivity {
    private ImageButton btnSearch;
    private EditText etSearch;
    private Spinner spFilterOne,spFilterTwo;

    private RecyclerView student_rv;
    private LinearLayoutManager manager;
    private StudentAdapter sa;
    private ProgressBar pbLoadMore;

    private static final String TAG = "SPM_ERROR";
    private JSONArray jsonStudent;

    private Boolean isScrolling = false;
    private int currentItem,totalItem,scrolledOutItems,totaldbitem;
    private SharedPreferences userPref;
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
        userPref = getSharedPreferences("user",MODE_PRIVATE);

        totaldbitem = 10;

        getStudents();

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

    private void getStudents(){
        pbLoadMore.setVisibility(View.VISIBLE);

        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.GET_STUDENT_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pbLoadMore.setVisibility(View.GONE);
                        Log.i(TAG, "onResponse: student list"+response);
                        try {

//                            JSONObject jsonObject = new JSONObject(response);
                            jsonStudent = new JSONArray(response);

                            TextView tvCount = (TextView) findViewById(R.id.tvViewStudentResultCount);
                            tvCount.setText("Result : "+ jsonStudent.length()+" Found");

                            sa = new StudentAdapter(ViewStudentActivity.this,jsonStudent);
                            student_rv.setAdapter(sa);
                            student_rv.setLayoutManager(manager);


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
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("univ_id",userPref.getString("univ_id","univ_id"));
                param.put("college_id",userPref.getString("college_id","college_id"));
                param.put("dept_id",userPref.getString("dept_id","dept_id"));

                return param;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(ViewStudentActivity.this);
        requestQueue.add(request);
    }

    public void goToDashboard(View view) {
        finish();
    }
}