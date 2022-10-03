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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateOfficerProfileActivity extends AppCompatActivity {
    private EditText etId,etUsername,etNumber;
    private Spinner spGender,spUniversity,spCollege,spDept;
    private Button btnCreate;
    private ProgressDialog dialog;
    static final String TAG = "SPM_ERROR";
    private final String[] GENDER = {"M","F"};
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
        String university = "1";
        String college = "2";
        String dept = "1";

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
                params.put("univ_id",university);
                params.put("college_id",college);
                params.put("dept_id",dept);
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

    public void goToBack(View view) {
        finish();
    }
}