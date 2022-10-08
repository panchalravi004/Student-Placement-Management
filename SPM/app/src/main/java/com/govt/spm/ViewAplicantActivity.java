package com.govt.spm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ViewAplicantActivity extends AppCompatActivity {

    private EditText etSearch;
    private ImageButton btnSearch;
    private TextView tvTtitle;
    private CheckBox isSelected;

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

    FileOutputStream fos;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_aplicant);
        view_jobs_rv = (RecyclerView) findViewById(R.id.recycleViewAplicant);
        etSearch = (EditText) findViewById(R.id.etAplicantSearch);
        btnSearch = (ImageButton) findViewById(R.id.btnAplicantSearch);
        pbLoadMore = (ProgressBar) findViewById(R.id.pbLoadMoreAplicant);
        tvTtitle = (TextView) findViewById(R.id.tvViewAplicantTitle);
        manager = new LinearLayoutManager(this);
        userPref = getSharedPreferences("user",MODE_PRIVATE);
        isSelected = (CheckBox) findViewById(R.id.cbViewAplicantSelected);
        myInt = getIntent();
        totalDBItem = 10;
        jsonJob = new JSONArray();


        //CALL METHOD
        getJobAplicant(myInt.getStringExtra("JOB_ID"));

        //LISTENER
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

    private void getJobAplicant(String job_id){
        pbLoadMore.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.GET_JOB_APLICANT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: "+response);
                        try {
                            pbLoadMore.setVisibility(View.GONE);
                            jsonJob = new JSONArray(response);

                            TextView te = (TextView) findViewById(R.id.tvViewAplicantResultCount);
                            te.setText("Result : "+jsonJob.length()+" Found");
                            vaa = new ViewAplicantAdapter(ViewAplicantActivity.this,jsonJob);
                            view_jobs_rv.setAdapter(vaa);
                            view_jobs_rv.setLayoutManager(manager);

                            isSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                    try {
                                        if(b){
                                            JSONArray temp = filterGetSelected(jsonJob);
                                            te.setText("Result : "+temp.length()+" Found");
                                            vaa = new ViewAplicantAdapter(ViewAplicantActivity.this,temp);
                                            view_jobs_rv.setAdapter(vaa);
                                            view_jobs_rv.setLayoutManager(manager);
                                            vaa.notifyDataSetChanged();
                                        }else{

                                            te.setText("Result : "+jsonJob.length()+" Found");
                                            vaa = new ViewAplicantAdapter(ViewAplicantActivity.this,new JSONArray(response));
                                            view_jobs_rv.setAdapter(vaa);
                                            view_jobs_rv.setLayoutManager(manager);
                                            vaa.notifyDataSetChanged();


                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });

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

    public void goToBack(View view) {
        finish();
    }
}