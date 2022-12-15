package com.govt.spm.officer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
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
import com.govt.spm.Constants;
import com.govt.spm.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OfficerProfileActivity extends AppCompatActivity {

    private EditText etId,etEmail,etMobile,etName,etGender,etUniv;
    private Spinner spCollege,spDept;
    private Button btnUpdate;
    private ConstraintLayout createPost,updatePost,deletePost;
    static  final String TAG = "SPM_ERROR";
    private ProgressDialog dialog;
    private SharedPreferences userPref;

    private JSONArray jsonCollege;
    private JSONArray jsonDept;

    private ArrayAdapter<String> adapterCollege;
    private ArrayAdapter<String> adapterDept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_officer_profile);
        etId = (EditText) findViewById(R.id.etTPOId);
        etEmail = (EditText) findViewById(R.id.etTPOEmail);
        etMobile = (EditText) findViewById(R.id.etTPOMobile);
        etName = (EditText) findViewById(R.id.etTPOName);
        etGender = (EditText) findViewById(R.id.etTPOGender);
        etUniv = (EditText) findViewById(R.id.etTPOUniversity);

        spCollege = (Spinner) findViewById(R.id.spTPOCollege);
        spDept = (Spinner) findViewById(R.id.spTPODept);

        createPost=(ConstraintLayout) findViewById(R.id.create_post);
        updatePost=(ConstraintLayout) findViewById(R.id.update_post);
        deletePost=(ConstraintLayout) findViewById(R.id.delete_post);

        btnUpdate = (Button) findViewById(R.id.btnTPOUpdateProfile);
        dialog = new ProgressDialog(OfficerProfileActivity.this);

        userPref = getSharedPreferences("user",MODE_PRIVATE);

        jsonCollege = new JSONArray();
        jsonDept = new JSONArray();

        //fetch colleges on university selects
        fetchColleges(userPref.getString("univ_id","univ_id"));

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

        //set Profile Data
        setProfile();
        //Update TPO Profile when click
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    updateTPOProfile();
                }
            }
        });
    }

    //set profile of TPO
    private void setProfile(){
        etId.setText(userPref.getString("user_id","user_id"));
        etEmail.setText(userPref.getString("email","email"));
        etName.setText(userPref.getString("name","name"));
        etMobile.setText(userPref.getString("mob","mob"));
        etUniv.setText(userPref.getString("univ","univ"));

        String gn = userPref.getString("gender","gender");


        if(userPref.getString("CAN_MAKE_JOB_POST","CAN_MAKE_JOB_POST").equals("0")){
            createPost.setBackground(getDrawable(R.drawable.spm_red_all_round));
        }else{
            createPost.setBackground(getDrawable(R.drawable.spm_green_all_round));
        }
        if(userPref.getString("CAN_UPDATE_COMPANY","CAN_UPDATE_COMPANY").equals("0")){
            updatePost.setBackground(getDrawable(R.drawable.spm_red_all_round));
        }else{
            updatePost.setBackground(getDrawable(R.drawable.spm_green_all_round));
        }
        if(userPref.getString("CAN_REJECT_JOB_APPLICATION","CAN_REJECT_JOB_APPLICATION").equals("0")){
            deletePost.setBackground(getDrawable(R.drawable.spm_red_all_round));
        }else{
            deletePost.setBackground(getDrawable(R.drawable.spm_green_all_round));
        }

        if(gn.equals("M")){
            etGender.setText("Male");
        }else{
            etGender.setText("FeMale");
        }

    }

    //update the TPO Profile
    private void updateTPOProfile(){
        dialog.setMessage("Updating...");
        dialog.show();
        String tpoID = etId.getText().toString();
        String email = etEmail.getText().toString();
        String mobile = etMobile.getText().toString();
        String college = null;
        String dept = null;
        try {
            college = new JSONObject(jsonCollege.getString(spCollege.getSelectedItemPosition())).getString("college_id");
            dept = new JSONObject(jsonDept.getString(spDept.getSelectedItemPosition())).getString("dept_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String finalCollege = college;
        String finalDept = dept;

        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.UPDATE_USER_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.i(TAG, "updateTPOProfile: "+response);
                        try {
                            JSONObject jo = new JSONObject(response);
                            Toast.makeText(OfficerProfileActivity.this, jo.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "updateTPOProfile: "+error.getMessage());
                    }
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("user_role",userPref.getString("role","role"));
                param.put("user_id",tpoID);
                param.put("user_mob",mobile);
                param.put("email",email);
                param.put("dept_id",finalDept);
                param.put("college_id",finalCollege);

                return param;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(OfficerProfileActivity.this);
        requestQueue.add(request);
    }

    //validate the fields
    private boolean validate(){

        String tpoID = etId.getText().toString();
        String email = etEmail.getText().toString();
        String name = etName.getText().toString();
        String mobile = etMobile.getText().toString();

        if (tpoID.equals("")) {
            Toast.makeText(this, "Please Enter TPO Id", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (email.equals("")) {
            Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (name.equals("")) {
            Toast.makeText(this, "Please Enter Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mobile.equals("")) {
            Toast.makeText(this, "Please Enter Mobile", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //Fetch colleges as per university
    private void fetchColleges(String univ_id){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.GET_COLLEGES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "fetchColleges: "+response);
                        try {
                            ArrayList<String> collegesList = new ArrayList<>();
                            adapterCollege = new ArrayAdapter<String>(OfficerProfileActivity.this, android.R.layout.simple_spinner_dropdown_item,collegesList);
                            spCollege.setAdapter(adapterCollege);
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
//                                    if(userPref.getString("college_id","college_id").equals(jo.getString("college_id"))){
//                                        collegesList.add(0,jo.getString("college_name"));
//                                    }else{
                                        collegesList.add(jo.getString("college_name"));
//                                    }

//                                    Log.i(TAG, "onResponse: "+jo.getString("college_name"));
                                }
                            }
                            adapterCollege.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "fetchColleges: "+error.getMessage());
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
        RequestQueue requestQueue = Volley.newRequestQueue(OfficerProfileActivity.this);
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
                        Log.i(TAG, "fetchCollegeWiseDept: "+response);
                        try {
                            ArrayList<String> deptsList = new ArrayList<>();
                            adapterDept = new ArrayAdapter<String>(OfficerProfileActivity.this, android.R.layout.simple_spinner_dropdown_item,deptsList);
                            spDept.setAdapter(adapterDept);
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
                            adapterDept.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "fetchCollegeWiseDept: "+error.getMessage());
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
        RequestQueue requestQueue = Volley.newRequestQueue(OfficerProfileActivity.this);
        requestQueue.add(request);
    }

    //go back
    public void goToDashboard(View view) {
        finish();
    }

}