package com.govt.spm.officer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.govt.spm.Constants;
import com.govt.spm.R;
import com.govt.spm.adapter.CompanyAdapter;
import com.govt.spm.request.CacheRequest;
import com.govt.spm.viewmodel.CompanyLiveViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ManageCompanyActivity extends AppCompatActivity {
    private EditText etSearch;
    private ImageButton btnSearch;
    private Spinner spFilterCountry,spFilterState,spFilterCity;
    private TextView tvCount;

    private RecyclerView company_rv;
    private LinearLayoutManager manager;
    private ProgressBar pbLoadMore;
    private ProgressDialog dialog;
    private Boolean isScrolling = false;
    private static final String TAG = "SPM_ERROR";

    private int currentItem,totalItem,scrollOutItem,totalDBItem;
    private int fetchCount;
    private CompanyAdapter ca;
    private CompanyLiveViewModel companyLiveViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;

    private JSONArray jsonCompany;
    private JSONArray jsonCountry;
    private JSONArray jsonState;
    private JSONArray jsonCity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_company);
        tvCount = (TextView) findViewById(R.id.tvManageCompanyResultCount);
        btnSearch = (ImageButton) findViewById(R.id.btnManageCompanySearch);
        etSearch = (EditText) findViewById(R.id.etManageCompanySearch);
        spFilterCountry = (Spinner) findViewById(R.id.spManageCompanyFilterCountry);
        spFilterState = (Spinner) findViewById(R.id.spManageCompanyFilterState);
        spFilterCity = (Spinner) findViewById(R.id.spManageCompanyFilterCity);
        pbLoadMore = (ProgressBar) findViewById(R.id.pbLoadMoreManageCompany);
        dialog = new ProgressDialog(ManageCompanyActivity.this);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        company_rv = (RecyclerView) findViewById(R.id.recycleViewManageCompany);
        manager = new LinearLayoutManager(this);
        totalDBItem = 12;

        fetchCount = 1;

        jsonCountry = new JSONArray();
        jsonState = new JSONArray();
        jsonCity = new JSONArray();

        //call methods
//        getCompanies();
//        fetchCountry();

        //listener
        //search on click
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!etSearch.getText().equals(null)){
                        JSONArray temp = filterBySearch(jsonCompany,etSearch.getText().toString());
                        tvCount.setText("Result : "+ temp.length()+" Found");
                        ca.updateCompany(temp);
                }
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                etSearch.setText("");
                companyLiveViewModel.makeApiCall(ManageCompanyActivity.this,null);
            }
        });
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

    @Override
    protected void onResume() {
        super.onResume();
        //CAll METHOD
        getCompanies();
        fetchCountry();

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

    //get companies list
    private void getCompanies(){
        pbLoadMore.setVisibility(View.VISIBLE);
        companyLiveViewModel = new CompanyLiveViewModel();
        ca = new CompanyAdapter(ManageCompanyActivity.this,jsonCompany);
        company_rv.setAdapter(ca);
        company_rv.setLayoutManager(manager);

        companyLiveViewModel.getCompany().observe(this, new Observer<JSONArray>() {
            @Override
            public void onChanged(JSONArray jsonArray) {
                pbLoadMore.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                if(jsonArray != null){
                    jsonCompany = jsonArray;
                    tvCount.setText("Result : "+ jsonCompany.length()+" Found");
                    ca.updateCompany(jsonArray);
                    setFilterSpinner(jsonCompany);
                }
            }
        });
        companyLiveViewModel.makeApiCall(this,null);
    }

    //set the spinner filters here
    private void setFilterSpinner(JSONArray response){
        spFilterCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    if(String.valueOf(i).equals("0")){
                        jsonCompany = response;
                        tvCount.setText("Result : "+ jsonCompany.length()+" Found");
                        ca.updateCompany(jsonCompany);
                    }else{
                        jsonCompany = filterByCountry(response,new JSONObject(jsonCountry.getString(i-1)).getString("country_name"),new JSONObject(jsonCountry.getString(i-1)).getString("country_id"));
                        tvCount.setText("Result : "+ jsonCompany.length()+" Found");
                        ca.updateCompany(jsonCompany);
                        //Fetch State
                        JSONObject jo = new JSONObject(jsonCountry.getString(i-1));
                        fetchState(jo.getString("country_id"));
                        //Second Filter By State
                        spFilterState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                try{
                                    if(String.valueOf(i).equals("0")){
                                        tvCount.setText("Result : "+ jsonCompany.length()+" Found");
                                        ca.updateCompany(jsonCompany);
                                    }else {
                                        JSONArray jsonStateCompany = filterByState(jsonCompany, new JSONObject(jsonState.getString(i - 1)).getString("state_name"), new JSONObject(jsonState.getString(i - 1)).getString("state_id"));
                                        tvCount.setText("Result : " + jsonStateCompany.length() + " Found");
                                        ca.updateCompany(jsonStateCompany);
                                        //Fetch City
                                        fetchCity(new JSONObject(jsonState.getString(i - 1)).getString("state_id"));
                                        spFilterCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                try{
                                                    if(String.valueOf(i).equals("0")){
                                                        tvCount.setText("Result : "+ jsonStateCompany.length()+" Found");
                                                        ca.updateCompany(jsonStateCompany);
                                                    }else {
                                                        JSONArray jsonCityCompany = filterByCity(jsonStateCompany, new JSONObject(jsonCity.getString(i - 1)).getString("city_name"), new JSONObject(jsonCity.getString(i - 1)).getString("city_id"));
                                                        tvCount.setText("Result : " + jsonCityCompany.length() + " Found");
                                                        ca.updateCompany(jsonCityCompany);
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> adapterView) {}
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {}
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private JSONArray filterByCountry(JSONArray allCompany,String country_name,String country_id) {
        if(country_name.equals("ALL")){
            return allCompany;
        }
        JSONArray result = new JSONArray();
        for (int i = 0; i < allCompany.length(); i++) {
            try {
                JSONObject jo = new JSONObject(allCompany.getString(i));
                if(jo.getString("country_id").equals(country_id)){
                    result.put(jo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private JSONArray filterByState(JSONArray allCountryFilterCompany,String state_name,String state_id) {
        if(state_name.equals("ALL")){
            return allCountryFilterCompany;
        }
        JSONArray result = new JSONArray();
        for (int i = 0; i < allCountryFilterCompany.length(); i++) {
            try {
                JSONObject jo = new JSONObject(allCountryFilterCompany.getString(i));
                if(jo.getString("state_id").equals(state_id)){
                    result.put(jo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private JSONArray filterByCity(JSONArray allStateFilterCompany,String city_name,String city_id) {
        if(city_name.equals("ALL")){
            return allStateFilterCompany;
        }
        JSONArray result = new JSONArray();
        for (int i = 0; i < allStateFilterCompany.length(); i++) {
            try {
                JSONObject jo = new JSONObject(allStateFilterCompany.getString(i));
                if(jo.getString("city_id").equals(city_id)){
                    result.put(jo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private JSONArray filterBySearch(JSONArray allCompany,String searchText){
        JSONArray result = new JSONArray();

        for (int i = 0; i < allCompany.length() ;i++) {
            try {
                JSONObject jo = new JSONObject(allCompany.getString(i));
                String searchTxt = searchText.toLowerCase(Locale.ROOT);
                String dataText = jo.getString("company_name").toLowerCase(Locale.ROOT);
                if(dataText.contains(searchTxt)){
                    result.put(jo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    private void fetchCountry(){
        CacheRequest request = new CacheRequest(
                Request.Method.POST,
                Constants.GET_COUNTRY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "fetchCountry: "+response);
                        try {
                            ArrayList<String> country = new ArrayList<>();
                            country.add(0,"ALL");
                            jsonCountry = new JSONArray(response);

                            for(int i=0;i<jsonCountry.length();i++){
                                JSONObject jo = new JSONObject(jsonCountry.getString(i));
                                country.add(jo.getString("country_name"));
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ManageCompanyActivity.this, android.R.layout.simple_spinner_dropdown_item,country);
                            spFilterCountry.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "fetchCountry: "+error.getMessage());
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(ManageCompanyActivity.this);
        requestQueue.add(request);
    }

    private void fetchState(String country_id){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.GET_STATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "fetchState: "+response);
                        try {
                            ArrayList<String> state = new ArrayList<>();
                            state.add(0,"ALL");
                            jsonState = new JSONArray(response);

                            for(int i=0;i<jsonState.length();i++){
                                JSONObject jo = new JSONObject(jsonState.getString(i));
                                state.add(jo.getString("state_name"));
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ManageCompanyActivity.this, android.R.layout.simple_spinner_dropdown_item,state);
                            spFilterState.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "fetchState: "+error.getMessage());
                    }
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("country_id",country_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ManageCompanyActivity.this);
        requestQueue.add(request);
    }

    private void fetchCity(String state_id){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.GET_CITY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "fetchCity: "+response);
                        try {
                            ArrayList<String> city = new ArrayList<>();
                            city.add(0,"ALL");
                            jsonCity = new JSONArray(response);

                            for(int i=0;i<jsonCity.length();i++){
                                JSONObject jo = new JSONObject(jsonCity.getString(i));
                                city.add(jo.getString("city_name"));
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ManageCompanyActivity.this, android.R.layout.simple_spinner_dropdown_item,city);
                            spFilterCity.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "fetchCity: "+error.getMessage());
                    }
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("state_id",state_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ManageCompanyActivity.this);
        requestQueue.add(request);
    }

    public void goToDashboard(View view) {
        finish();
    }

    public void goToAddCompany(View view) {
        Intent i = new Intent(ManageCompanyActivity.this,AddCompanyActivity.class);
        i.putExtra("ACTION","ADD");
        startActivity(i);
    }
}