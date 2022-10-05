package com.govt.spm;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ManageCompanyActivity extends AppCompatActivity {
    private EditText etSearch;
    private ImageButton btnSearch;
    private Spinner spFilterOne,spFilterTwo;

    private RecyclerView company_rv;
    private LinearLayoutManager manager;
    private ProgressBar pbLoadMore;
    private ProgressDialog dialog;
    private Boolean isScrolling = false;
    private static final String TAG = "SPM_ERROR";

    private int currentItem,totalItem,scrollOutItem,totalDBItem;
    private JSONArray jsonCompany;
//    private ArrayList<HashMap<String,String>> cList;
//    private static ArrayList<ArrayList<HashMap<String,String>>> allCompanyList;
    private CompanyAdapter ca;
    private int fetchCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_company);

        btnSearch = (ImageButton) findViewById(R.id.btnManageCompanySearch);
        etSearch = (EditText) findViewById(R.id.etManageCompanySearch);
        spFilterOne = (Spinner) findViewById(R.id.spManageCompanyFilterOne);
        spFilterTwo = (Spinner) findViewById(R.id.spManageCompanyFilterTwo);
        pbLoadMore = (ProgressBar) findViewById(R.id.pbLoadMoreManageCompany);
        dialog = new ProgressDialog(ManageCompanyActivity.this);

        company_rv = (RecyclerView) findViewById(R.id.recycleViewManageCompany);
        manager = new LinearLayoutManager(this);
        totalDBItem = 12;

        fetchCount = 1;

//        cList = new ArrayList<>();
//        allCompanyList = new ArrayList<>();
        getCompanies();

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
                    fetchData();
                }
            }
        });

    }

    private void fetchData() {
        pbLoadMore.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                if(fetchCount < allCompanyList.size()){
//                    cList.addAll(allCompanyList.get(fetchCount));
//                    fetchCount++;
//                    ca.notifyDataSetChanged();
//                }
                pbLoadMore.setVisibility(View.GONE);
            }
        },3000);
    }

    private void getCompanies(){
        pbLoadMore.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.GET_COMPANIES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pbLoadMore.setVisibility(View.GONE);
                        Log.i(TAG, "onResponse: "+response);
                        try {

                            jsonCompany = new JSONArray(response);

                            TextView tvCount = (TextView) findViewById(R.id.tvManageCompanyResultCount);
                            tvCount.setText("Result : "+ jsonCompany.length()+" Found");

//                            int count = 1;
////                            ArrayList<HashMap<String,String>> tempList = new ArrayList<>();
//                            for(int i=0;i<jsonArray.length();i++){
//                                JSONObject jo = new JSONObject(jsonArray.getString(i));
//                                HashMap<String,String> map = new HashMap<>();
//                                map.put("company_id",jo.getString("company_id"));
//                                map.put("company_name",jo.getString("company_name"));
//                                map.put("hr1_name",jo.getString("hr1_name"));
//                                map.put("hr1_email",jo.getString("hr1_email"));
//                                map.put("hr2_name",jo.getString("hr2_name"));
//                                map.put("hr2_email",jo.getString("hr2_email"));
//                                map.put("about",jo.getString("about"));
//                                map.put("web_domain",jo.getString("web_domain"));
//                                map.put("address",jo.getString("address"));
//                                map.put("city_id",jo.getString("city_id"));
//                                map.put("creator_id",jo.getString("creator_id"));
////                                tempList.add(map);
////                                if(i<10){
//                                    cList.add(map);
////                                }
//
////                                if(count==10){
////                                    count=0;
////                                    allCompanyList.add(tempList);
////                                    tempList.clear();
////                                }
////                                count++;
//                            }
//                            allCompanyList.add(tempList);
//                            Log.i(TAG, "onResponse: "+allCompanyList.size());

                            ca = new CompanyAdapter(ManageCompanyActivity.this, jsonCompany);
                            company_rv.setAdapter(ca);
                            company_rv.setLayoutManager(manager);

                            //Filter data From filter on
                            spFilterOne.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    try {
                                        jsonCompany = filterByCity(new JSONArray(response),adapterView.getSelectedItem().toString());
                                        tvCount.setText("Result : "+ jsonCompany.length()+" Found");
                                        ca = new CompanyAdapter(ManageCompanyActivity.this, jsonCompany);
                                        company_rv.setAdapter(ca);
                                        company_rv.setLayoutManager(manager);
                                        ca.notifyDataSetChanged();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {}
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
                }
        );
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(ManageCompanyActivity.this);
        requestQueue.add(request);
    }

    private JSONArray filterByCity(JSONArray allCompany,String item) {
        if(item.equals("All")){
            return allCompany;
        }
        JSONArray result = new JSONArray();
        for (int i = 0; i < allCompany.length(); i++) {
            try {
                JSONObject jo = new JSONObject(allCompany.getString(i));
                if(jo.getString("city_id").equals(item)){
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

    public void goToAddCompany(View view) {
        startActivity(new Intent(ManageCompanyActivity.this,AddCompanyActivity.class));
    }
}