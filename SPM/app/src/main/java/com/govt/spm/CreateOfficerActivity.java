package com.govt.spm;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.badge.BadgeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateOfficerActivity extends AppCompatActivity {
    private EditText etTPOId,etTPOEmail,etTPOPassword;
    private Button btnCreate;
    private ProgressDialog dialog;
    final static String TAG = "SPM_ERROR";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_officer);
        etTPOId = (EditText) findViewById(R.id.etCreateTPOID);
        etTPOEmail = (EditText) findViewById(R.id.etCreateTPOEmail);
        etTPOPassword = (EditText) findViewById(R.id.etCreateTPOPassword);
        btnCreate = (Button) findViewById(R.id.btnCreateTPOCreate);
        dialog = new ProgressDialog(this);

        //Listeners
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    createAccount();
                }
            }
        });
    }

    //create TPO account
    private void createAccount() {
        final String id = etTPOId.getText().toString();
        final String email = etTPOEmail.getText().toString();
        final String password = etTPOPassword.getText().toString();
        dialog.setMessage("Creating...");
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST,
                Constants.REGISTER_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        clearField();
                        Log.i(TAG, "createAccount: "+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(CreateOfficerActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                            if(jsonObject.getBoolean("error")){
                                //if user account is already created
                                if(jsonObject.getString("error_flag").equals("EXISTING_DATA")){
                                    startActivity(new Intent(CreateOfficerActivity.this,CreateOfficerProfileActivity.class));
                                    finish();
                                }
                            }else{
                                //if account created successfully then create the profile
                                startActivity(new Intent(CreateOfficerActivity.this,CreateOfficerProfileActivity.class));
                                finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "createAccount: "+error.getMessage());
                    }
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id",id);
                params.put("email",email);
                params.put("password",password);
                return params;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(CreateOfficerActivity.this);
        requestQueue.add(request);
    }

    //validate a fields
    private boolean validate() {
        String id = etTPOId.getText().toString();
        String email = etTPOEmail.getText().toString();
        String password = etTPOPassword.getText().toString();

        if(id.equals("")){
            Toast.makeText(this, "Enter TPO ID", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(email.equals("")){
            Toast.makeText(this, "Enter TPO Email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.equals("")){
            Toast.makeText(this, "Enter TPO Password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //clear fields
    private void clearField(){
        etTPOId.setText("");
        etTPOEmail.setText("");
        etTPOPassword.setText("");
    }

    public void goToBack(View view) {
        finish();
    }
}