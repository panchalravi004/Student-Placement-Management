package com.govt.spm;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.util.List;
import java.util.Map;

public class CreateOfficerProfileActivity extends AppCompatActivity {
    private EditText etId,etUsername,etNumber;
    private Spinner spGender,spUniversity,spCollege,spDept;
    private Button btnCreate;
    private ProgressDialog dialog;
    static final String TAG = "SPM_ERROR";
    private final String[] GENDER = {"M","F"};

    private JSONArray jsonUniversity;
    private JSONArray jsonCollege;
    private JSONArray jsonDept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_officer_profile);
        etId=(EditText) findViewById(R.id.etCreateTPOProfileID);
        etUsername=(EditText) findViewById(R.id.etCreateTPOProfileUsername);
        etNumber=(EditText) findViewById(R.id.etCreateTPOProfileNumber);

        spGender=(Spinner) findViewById(R.id.spCreateTPOProfileGender);
        spUniversity=(Spinner) findViewById(R.id.spCreateTPOProfileUniversity);
        spCollege=(Spinner) findViewById(R.id.spCreateTPOProfileCollege);
        spDept=(Spinner) findViewById(R.id.spCreateTPOProfileDept);

        btnCreate = (Button) findViewById(R.id.btnCreateTPOProfileCreate);
        dialog = new ProgressDialog(CreateOfficerProfileActivity.this);

        jsonUniversity = new JSONArray();
        jsonCollege = new JSONArray();
        jsonDept = new JSONArray();
        //fetch University Data
        fetchUniv();
        //fetch colleges on university selects
        spUniversity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                JSONObject univJo = null;
                try {
                    univJo = new JSONObject(jsonUniversity.getString(spUniversity.getSelectedItemPosition()));
                    fetchColleges(univJo.getString("univ_id"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        //fetch dept on college select
        spCollege.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                JSONObject collegeJo = null;
                try {
                    collegeJo = new JSONObject(jsonCollege.getString(spCollege.getSelectedItemPosition()));
                    fetchCollegeWiseDept(collegeJo.getString("college_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    createProfile();
                }
            }
        });
    }

    private void createProfile() {
        String id = etId.getText().toString();
        String username = etUsername.getText().toString();
        String number = etNumber.getText().toString();
        String gender = GENDER[spGender.getSelectedItemPosition()];
        String university = null;
        String college = null;
        String dept = null;
        try {
            JSONObject univJo = new JSONObject(jsonUniversity.getString(spUniversity.getSelectedItemPosition()));
            university = univJo.getString("univ_id");
            JSONObject collegeJo = new JSONObject(jsonCollege.getString(spCollege.getSelectedItemPosition()));
            college = collegeJo.getString("college_id");
            JSONObject deptJo = new JSONObject(jsonDept.getString(spDept.getSelectedItemPosition()));
            dept = deptJo.getString("dept_id");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String finalUniversity = university;
        String finalCollege = college;
        String finalDept = dept;
        dialog.setMessage("Creating...");
        dialog.show();

        StringRequest request = new StringRequest(
            Request.Method.POST,
            Constants.CREATE_USER_PROFILE,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    dialog.dismiss();
                    Log.i(TAG, "onResponse: "+response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Toast.makeText(CreateOfficerProfileActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        if(!jsonObject.getBoolean("error")){
                            startActivity(new Intent(CreateOfficerProfileActivity.this,LoginActivity.class));
                        }
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
                Map<String, String> params = new HashMap<>();
                params.put("user_id",id);
                params.put("user_name",username);
                params.put("gender",gender);
                params.put("user_mob",number);
                params.put("univ_id", finalUniversity);
                params.put("college_id", finalCollege);
                params.put("dept_id", finalDept);
                return params;
            }
        };

        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(CreateOfficerProfileActivity.this);
        requestQueue.add(request);

    }

    private boolean validate() {
        if(etId.getText().equals("")){
            Toast.makeText(this, "Enter TPO ID", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(etUsername.getText().equals("")){
            Toast.makeText(this, "Enter TPO Username", Toast.LENGTH_SHORT).show();
            return false;
        }if(etNumber.getText().equals("")){
            Toast.makeText(this, "Enter TPO Number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //Fetch University ALL
    private void fetchUniv(){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.GET_UNIV,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: "+response);
                        try {
                            ArrayList<String> univ = new ArrayList<>();
                            jsonUniversity = new JSONArray(response);

                            for(int i=0;i<jsonUniversity.length();i++){
                                JSONObject jo = new JSONObject(jsonUniversity.getString(i));
                                univ.add(jo.getString("univ_name"));
                                Log.i(TAG, "onResponse: "+jo.getString("univ_name"));
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateOfficerProfileActivity.this, android.R.layout.simple_spinner_dropdown_item,univ);
                            spUniversity.setAdapter(adapter);
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
                });
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(CreateOfficerProfileActivity.this);
        requestQueue.add(request);
    }

    //Fetch colleges as per university
    private void fetchColleges(String univ_id){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.GET_COLLEGES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: "+response);
                        try {
                            ArrayList<String> collegesList = new ArrayList<>();
                            ArrayAdapter<String> collegesAdapter = new ArrayAdapter<String>(CreateOfficerProfileActivity.this, android.R.layout.simple_spinner_dropdown_item,collegesList);
                            spCollege.setAdapter(collegesAdapter);
                            jsonCollege = new JSONArray(response);

                            if(new JSONObject(jsonCollege.getString(0)).has("error")){
                                if(new JSONObject(jsonCollege.getString(0)).getBoolean("error")){
                                    collegesList.clear();
                                    collegesList.add(new String("There is no Data"));
                                }
                            }else{
                                collegesList.clear();
                                for(int i=0;i<jsonCollege.length();i++){
                                    JSONObject jo = new JSONObject(jsonCollege.getString(i));
                                    collegesList.add(jo.getString("college_name"));
//                                    Log.i(TAG, "onResponse: "+jo.getString("college_name"));
                                }
                            }
                            collegesAdapter.notifyDataSetChanged();

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
                Map<String, String> params = new HashMap<>();
                params.put("univ_id",univ_id);
                return params;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(CreateOfficerProfileActivity.this);
        requestQueue.add(request);
    }

    //Fetch Dept as per Colleges
    private void fetchCollegeWiseDept(String college_id){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.GET_DEPARTMENTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: "+response);
                        try {
                            ArrayList<String> deptsList = new ArrayList<>();
                            ArrayAdapter<String> deptsAdapter = new ArrayAdapter<String>(CreateOfficerProfileActivity.this, android.R.layout.simple_spinner_dropdown_item,deptsList);
                            spDept.setAdapter(deptsAdapter);
                            jsonDept = new JSONArray(response);

                            if(new JSONObject(jsonDept.getString(0)).has("error")){
                                if(new JSONObject(jsonDept.getString(0)).getBoolean("error")){
                                    deptsList.clear();
                                    deptsList.add(new String("There is no Data"));
                                }
                            }else{
                                deptsList.clear();
                                for(int i=0;i<jsonDept.length();i++){
                                    JSONObject jo = new JSONObject(jsonDept.getString(i));
                                    deptsList.add(jo.getString("dept_name"));
//                                    Log.i(TAG, "onResponse: "+jo.getString("dept_name"));
                                }
                            }
                            deptsAdapter.notifyDataSetChanged();

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
                Map<String, String> params = new HashMap<>();
                params.put("college_id",college_id);
                return params;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(CreateOfficerProfileActivity.this);
        requestQueue.add(request);
    }

    public void goToBack(View view) {
        finish();
    }
}