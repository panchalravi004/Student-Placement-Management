package com.govt.spm;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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
import java.util.Map;

public class AddJobsActivity extends AppCompatActivity {

    private EditText etDescription,etRole,etSkill,etSSC,etHSC,etUG,etPG,etMinQualification,etStartDate,etEndDate;
    private Spinner spCompany,spUniversity,spCollege,spDept,spStatus;
    private Button btnAddNew,btnUpdate;

    private static final String TAG = "SPM_ERROR";
    private ProgressDialog dialog;

    private JSONArray jsonUniversity;
    private JSONArray jsonCollege;
    private JSONArray jsonDept;
    private JSONArray jsonCompany;

    private SharedPreferences userPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_jobs);
        etDescription = (EditText) findViewById(R.id.etAddJobDescription);
        etRole = (EditText) findViewById(R.id.etAddJobRole);
        etSkill = (EditText) findViewById(R.id.etAddJobSkills);
        etSSC = (EditText) findViewById(R.id.etAddJobSSC);
        etHSC = (EditText) findViewById(R.id.etAddJobHSC);
        etUG = (EditText) findViewById(R.id.etAddJobUG);
        etPG = (EditText) findViewById(R.id.etAddJobPG);
        etMinQualification = (EditText) findViewById(R.id.etAddJobMinQualification);
        etStartDate = (EditText) findViewById(R.id.etAddJobStartDate);
        etEndDate = (EditText) findViewById(R.id.etAddJobEndDate);

        spCompany = (Spinner) findViewById(R.id.spAddJobCompany);
        spUniversity = (Spinner) findViewById(R.id.spAddJobUniversity);
        spCollege = (Spinner) findViewById(R.id.spAddJobCollege);
        spDept = (Spinner) findViewById(R.id.spAddJobDept);
        spStatus = (Spinner) findViewById(R.id.spAddJobStatus);

        btnAddNew = (Button) findViewById(R.id.btnAddJobAddNew);
        btnUpdate = (Button) findViewById(R.id.btnAddJobUpdate);
        userPref = getSharedPreferences("user",MODE_PRIVATE);

        dialog = new ProgressDialog(AddJobsActivity.this);
        jsonUniversity = new JSONArray();
        jsonCollege = new JSONArray();
        jsonDept = new JSONArray();
        jsonCompany = new JSONArray();

        Intent i = getIntent();
        if(i.getStringExtra("ACTION").equals("ADD")){
            btnAddNew.setVisibility(View.VISIBLE);
            btnUpdate.setVisibility(View.GONE);
        }else if(i.getStringExtra("ACTION").equals("UPDATE")){
            setJobPostDetails(i.getStringExtra("job_id"));
            btnAddNew.setVisibility(View.GONE);
            btnUpdate.setVisibility(View.VISIBLE);
        }

        //CALL METHOD
        fetchUniv();
        getCompanies();
        //LISTENER
        spUniversity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    JSONObject jo = new JSONObject(jsonUniversity.getString(i));
                    fetchColleges(jo.getString("univ_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        spCollege.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    JSONObject jo = new JSONObject(jsonCollege.getString(i));
                    fetchCollegeWiseDept(jo.getString("college_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    AddUpdateJobPost("CREATE",null);
                }
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    AddUpdateJobPost("UPDATE",i.getStringExtra("job_id"));
                }
            }
        });
        etStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd = new DatePickerDialog(AddJobsActivity.this,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        etStartDate.setText(String.valueOf(i)+"-"+String.valueOf(i1)+"-"+String.valueOf(i2));
                    }
                }, 2022, 10, 2);
                dpd.show();
            }
        });
        etEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd = new DatePickerDialog(AddJobsActivity.this,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        etEndDate.setText(String.valueOf(i)+"-"+String.valueOf(i1)+"-"+String.valueOf(i2));
                    }
                }, 2022, 10, 2);
                dpd.show();
            }
        });
    }

    //set job post data before update
    private void setJobPostDetails(String job_post_id){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.GET_JOB_DETAIL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: "+response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            etDescription.setText(new JSONObject(jsonArray.getString(0)).getString("JOB_DESC"));
                            etRole.setText(new JSONObject(jsonArray.getString(0)).getString("ROLE"));
                            etSkill.setText(new JSONObject(jsonArray.getString(0)).getString("SKILLS"));
                            etSSC.setText(new JSONObject(jsonArray.getString(0)).getString("REQ_SSC_SCORE"));
                            etHSC.setText(new JSONObject(jsonArray.getString(0)).getString("REQ_HSC_SCORE"));
                            etUG.setText(new JSONObject(jsonArray.getString(0)).getString("REQ_UG_SCORE"));
                            etPG.setText(new JSONObject(jsonArray.getString(0)).getString("REQ_PG_SCORE"));
                            etMinQualification.setText(new JSONObject(jsonArray.getString(0)).getString("MIN_QUALIFICATION"));
                            etStartDate.setText(new JSONObject(jsonArray.getString(0)).getString("REG_START_DATE"));
                            etEndDate.setText(new JSONObject(jsonArray.getString(0)).getString("REG_END_DATE"));
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
                param.put("job_id",job_post_id);
                return param;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(AddJobsActivity.this);
        requestQueue.add(request);
    }

    //add update job post from here
    private void AddUpdateJobPost(String COMMAND,String job_id){
        dialog.setMessage("Adding...");
        dialog.show();
        String Description = etDescription.getText().toString();
        String Role = etRole.getText().toString();
        String Skill = etSkill.getText().toString();
        String SSC = etSSC.getText().toString();
        String HSC = etHSC.getText().toString();
        String UG = etUG.getText().toString();
        String PG = etPG.getText().toString();
        String MinQualification = etMinQualification.getText().toString();
        String StartDate = etStartDate.getText().toString();
        String EndDate = etEndDate.getText().toString();

        String Company = null;
        String University = null;
        String College = null;
        String Dept = null;
        String Status = spStatus.getSelectedItem().toString();

        try {
            University = new JSONObject(jsonUniversity.getString(spUniversity.getSelectedItemPosition())).getString("univ_id");
            College = new JSONObject(jsonCollege.getString(spCollege.getSelectedItemPosition())).getString("college_id");
            Dept = new JSONObject(jsonDept.getString(spDept.getSelectedItemPosition())).getString("dept_id");
            Company = new JSONObject(jsonCompany.getString(spCompany.getSelectedItemPosition())).getString("company_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String finalUniversity = University;
        String finalCollege = College;
        String finalDept = Dept;
        String finalCompany = Company;

        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.MANAGE_JOB_DETAIL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        clearField();
                        Log.i(TAG, "onResponse: "+response);
                        try {
                            JSONObject jo = new JSONObject(response);
                            if(jo.getBoolean("error")){
                                Toast.makeText(AddJobsActivity.this, jo.getString("message"), Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(AddJobsActivity.this, jo.getString("message"), Toast.LENGTH_SHORT).show();
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

                if(COMMAND.equals("UPDATE")){
                    params.put("job_id",job_id);
                }
                params.put("command",COMMAND);
                params.put("cmp_id",finalCompany);
                params.put("job_desc",Description);
                params.put("role",Role);
                params.put("skills",Skill);
                params.put("req_ssc_score",SSC);
                params.put("req_hsc_score",HSC);
                params.put("req_ug_score",UG);
                params.put("req_pg_score",PG);
                params.put("min_qualification",MinQualification);
                params.put("reg_start_date", StartDate);
                params.put("reg_end_date",EndDate);
                params.put("univ_id",finalUniversity);
                params.put("college_id",finalCollege);
                params.put("dept_id",finalDept);
                params.put("creator_id",userPref.getString("user_id","user_id"));

                return params;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(AddJobsActivity.this);
        requestQueue.add(request);

    }

    private void getCompanies(){

        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.GET_COMPANIES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: "+response);
                        try {
                            ArrayList<String> company = new ArrayList<>();
                            jsonCompany = new JSONArray(response);

                            for(int i=0;i<jsonCompany.length();i++){
                                JSONObject jo = new JSONObject(jsonCompany.getString(i));
                                company.add(jo.getString("company_name"));
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddJobsActivity.this, android.R.layout.simple_spinner_dropdown_item,company);
                            spCompany.setAdapter(adapter);
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
        RequestQueue requestQueue = Volley.newRequestQueue(AddJobsActivity.this);
        requestQueue.add(request);
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
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddJobsActivity.this, android.R.layout.simple_spinner_dropdown_item,univ);
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
        RequestQueue requestQueue = Volley.newRequestQueue(AddJobsActivity.this);
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
                            ArrayAdapter<String> collegesAdapter = new ArrayAdapter<String>(AddJobsActivity.this, android.R.layout.simple_spinner_dropdown_item,collegesList);
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
        RequestQueue requestQueue = Volley.newRequestQueue(AddJobsActivity.this);
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
                            ArrayAdapter<String> deptsAdapter = new ArrayAdapter<String>(AddJobsActivity.this, android.R.layout.simple_spinner_dropdown_item,deptsList);
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
        RequestQueue requestQueue = Volley.newRequestQueue(AddJobsActivity.this);
        requestQueue.add(request);
    }

    //validate field
    private boolean validate(){

        if(etDescription.getText().toString().equals("")){
            Toast.makeText(this, "Please Enter Description", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(etRole.getText().toString().equals("")){
            Toast.makeText(this, "Please Enter Role", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(etSkill.getText().toString().equals("")){
            Toast.makeText(this, "Please Enter Skill", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(etSSC.getText().toString().equals("")){
            Toast.makeText(this, "Please Enter SSC Score", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(etHSC.getText().toString().equals("")){
            Toast.makeText(this, "Please Enter HSC Score", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(etUG.getText().toString().equals("")){
            Toast.makeText(this, "Please Enter UG Score", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(etPG.getText().toString().equals("")){
            Toast.makeText(this, "Please Enter PG Score", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(etMinQualification.getText().toString().equals("")){
            Toast.makeText(this, "Please Enter Min. Qualification", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(etStartDate.getText().toString().equals("")){
            Toast.makeText(this, "Please Enter Start Date", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(etEndDate.getText().toString().equals("")){
            Toast.makeText(this, "Please Enter End Date", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //clear field
    private void clearField(){
        etDescription.setText("");
        etRole.setText("");
        etSkill.setText("");
        etSSC.setText("");
        etHSC.setText("");
        etUG.setText("");
        etPG.setText("");
        etMinQualification.setText("");
        etStartDate.setText("");
        etEndDate.setText("");
    }

    public void goToDashboard(View view) {
        finish();
    }
}