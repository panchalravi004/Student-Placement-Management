package com.govt.spm.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.govt.spm.Constants;
import com.govt.spm.request.CacheRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class CompanyLiveViewModel extends ViewModel {

    private MutableLiveData<JSONArray> company;
    private static String TAG = "SPM_ERROR";

    public CompanyLiveViewModel() {
        this.company = new MutableLiveData<>();
    }

    public MutableLiveData<JSONArray> getCompany(){
        return company;
    }

    public void makeApiCall(Context context,SharedPreferences userPref){

        CacheRequest cacheRequest = new CacheRequest(
                Request.Method.POST,
                Constants.GET_COMPANIES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: "+response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            company.postValue(jsonArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        company.postValue(null);
                        Log.i(TAG, "onErrorResponse: "+error.getMessage());
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(cacheRequest);

    }
}
