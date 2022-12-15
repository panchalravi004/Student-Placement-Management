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

public class AppliedJobLiveViewModel extends ViewModel {

    private MutableLiveData<JSONArray> job;
    private static String TAG = "SPM_ERROR";

    public AppliedJobLiveViewModel() {
        this.job = new MutableLiveData<>();
    }

    public MutableLiveData<JSONArray> getAppliedJob(){
        return job;
    }

    public void makeApiCall(Context context,SharedPreferences userPref){

        CacheRequest cacheRequest = new CacheRequest(
                Request.Method.POST,
                Constants.GET_APPLIED_JOB_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: "+response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            job.postValue(jsonArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        job.postValue(null);
                        Log.i(TAG, "onErrorResponse: "+error.getMessage());
                    }
                }){
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("stud_id",userPref.getString("stud_id","stud_id"));
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(cacheRequest);

    }
}
