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

public class LoginActivity extends AppCompatActivity {

    private EditText etUserId,etPassword;
    private Button btnLogin;
    private SharedPreferences userPref;
    private SharedPreferences.Editor editor;
    private ProgressDialog dialog;
    final static String TAG = "SPM_ERROR";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUserId = (EditText) findViewById(R.id.etUserIdLogin);
        etPassword = (EditText) findViewById(R.id.etPasswordLogin);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        dialog = new ProgressDialog(LoginActivity.this);
        userPref = getSharedPreferences("user",MODE_PRIVATE);
        editor = userPref.edit();

    }
    //Do login when click
    public void doLogin(View view) {
        String userid = etUserId.getText().toString();
        String password = etPassword.getText().toString();

        //start loading
        dialog.setMessage("Authenticating...");
        dialog.show();

        if(validate()){
            //Here actual login auth - and fetch user role
            //According user role - use intent to move to next activity

            authentication(userid,password);
            clearField();

        }else{
            dialog.dismiss();
        }
    }

    //For Authentication and fetch user role : ex : student , TPO
    private void authentication(String id,String pass){

        Log.i(TAG, "authentication: Request Sending...");
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.i(TAG, "onResponse: "+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(LoginActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            if(jsonObject.getBoolean("error")){
                                if(jsonObject.getString("error_flag").equals("CREATE_PROFILE_FIRST")){
                                    startActivity(new Intent(LoginActivity.this,CreateOfficerProfileActivity.class));
                                    finish();
                                }
                            }

                            if(!jsonObject.getBoolean("error")){

                                editor.putString("user_id",jsonObject.getString("id"));
                                editor.putString("role",jsonObject.getString("user_role"));
                                editor.apply();

                                if(jsonObject.getString("user_role").equals("FACULTY")){
                                    startActivity(new Intent(LoginActivity.this,OfficerDashboardActivity.class));
                                    finish();
                                }if(jsonObject.getString("user_role").equals("STUDENT")){
                                    startActivity(new Intent(LoginActivity.this,StudentDashboardActivity.class));
                                    finish();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG,error.getMessage());
                    }
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id",id);
                params.put("password",pass);
                return params;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(request);
    }

    private void clearField(){
        etUserId.setText("");
        etPassword.setText("");
    }

    //validating fields
    private boolean validate(){
        String userid = etUserId.getText().toString();
        String password = etPassword.getText().toString();

        if (userid.equals("")) {
            Toast.makeText(this, "Please Enter User Id", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.equals("")) {
            Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void goToCreateAccount(View view) {
        startActivity(new Intent(LoginActivity.this,CreateOfficerActivity.class));
    }
}