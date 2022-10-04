package com.govt.spm;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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

import java.lang.ref.ReferenceQueue;
import java.util.Map;

public class OfficerProfileActivity extends AppCompatActivity {

    private EditText etId,etEmail,etMobile,etName;
    private Spinner spGender,spUniversity,spCollege,spDept;
    private Button btnUpdate;
    static  final String TAG = "SPM_ERROR";
    private ProgressDialog dialog;
    private SharedPreferences userPref;
    private static final String[] GENDER = {"M","F"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_officer_profile);
        etId = (EditText) findViewById(R.id.etTPOId);
        etEmail = (EditText) findViewById(R.id.etTPOEmail);
        etMobile = (EditText) findViewById(R.id.etTPOMobile);
        etName = (EditText) findViewById(R.id.etTPOName);

        spGender = (Spinner) findViewById(R.id.spTPOGender);
        spUniversity = (Spinner) findViewById(R.id.spTPOUniversity);
        spCollege = (Spinner) findViewById(R.id.spTPOCollege);
        spDept = (Spinner) findViewById(R.id.spTPODept);

        btnUpdate = (Button) findViewById(R.id.btnTPOUpdateProfile);
        dialog = new ProgressDialog(OfficerProfileActivity.this);

        userPref = getSharedPreferences("user",MODE_PRIVATE);
        //Fetch TPO Profile data
        fetchProfileData(userPref.getString("user_id","user_id"));

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

    private void fetchProfileData(String user_id) {
        Log.i(TAG, "fetchProfileData: Request Sending...");
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.GET_COUNTRY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: "+response);
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
        RequestQueue requestQueue = Volley.newRequestQueue(OfficerProfileActivity.this);
        requestQueue.add(request);

    }

    private void updateTPOProfile(){
        String tpoID = etId.getText().toString();
        String email = etEmail.getText().toString();
        String name = etName.getText().toString();
        String mobile = etMobile.getText().toString();
        String gender = GENDER[spGender.getSelectedItemPosition()];
        String university = spUniversity.getSelectedItem().toString();
        String college = spCollege.getSelectedItem().toString();
        String dept = spDept.getSelectedItem().toString();
        Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
    }

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

    public void goToDashboard(View view) {
        finish();
    }
}