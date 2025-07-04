package com.govt.spm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.govt.spm.officer.OfficerDashboardActivity;
import com.govt.spm.student.StudentDashboardActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "102";
    private EditText etUserId,etPassword;
    private Button btnLogin;
    private SharedPreferences userPref;
    private SharedPreferences.Editor editor;
    private ProgressDialog dialog;
    private String USER_TOKEN = "";
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

        createNotificationChannel();
        getToken();

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
            //stop loading
            dialog.dismiss();
        }
    }

    //For Authentication and fetch user role : ex : student , TPO
    private void authentication(String id,String pass){

        Log.i(TAG, "Authentication: Request Sending...");
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.i(TAG, "Authentication: "+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(jsonObject.getBoolean("error")){
                                //if user profile is not create
                                if(jsonObject.getString("error_flag").equals("CREATE_PROFILE_FIRST")){
                                    Toast.makeText(LoginActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this,CreateOfficerProfileActivity.class));
                                    finish();
                                }
                                //when user login credential is wrong
                                if(jsonObject.getString("error_flag").equals("INVALID_CRED")){
                                    Toast.makeText(LoginActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toast.makeText(LoginActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                //check the role of user
                                //Store the faculty information into our local storage : Shred Preference
                                if(jsonObject.getString("user_role").equals("FACULTY")){
                                    editor.putString("user_id",jsonObject.getString("id"));
                                    editor.putString("role",jsonObject.getString("user_role"));
                                    editor.putString("name",jsonObject.getString("Name"));
                                    editor.putString("email",jsonObject.getString("Email"));
                                    editor.putString("mob",jsonObject.getString("Mob"));
                                    editor.putString("gender",jsonObject.getString("Gender"));
                                    editor.putString("univ",jsonObject.getString("University"));
                                    editor.putString("college",jsonObject.getString("College"));
                                    editor.putString("dept",jsonObject.getString("Department"));
                                    editor.putString("univ_id",jsonObject.getString("univ_id"));
                                    editor.putString("college_id",jsonObject.getString("college_id"));
                                    editor.putString("dept_id",jsonObject.getString("dept_id"));
                                    editor.putString("CAN_UPDATE_COMPANY",jsonObject.getString("CAN_UPDATE_COMPANY"));
                                    editor.putString("CAN_MAKE_JOB_POST",jsonObject.getString("CAN_MAKE_JOB_POST"));
                                    editor.putString("CAN_REJECT_JOB_APPLICATION",jsonObject.getString("CAN_REJECT_JOB_APPLICATION"));
                                    editor.apply();
                                    startActivity(new Intent(LoginActivity.this,OfficerDashboardActivity.class));
                                    finish();
                                }
                                //Store the student information into our local storage : Shared Preference
                                if(jsonObject.getString("user_role").equals("STUDENT")){
                                    editor.putString("stud_id",jsonObject.getString("stud_id"));
                                    editor.putString("role",jsonObject.getString("user_role"));
                                    editor.putString("name",jsonObject.getString("stud_name"));
                                    editor.putString("photo",jsonObject.getString("stud_photo"));
                                    editor.putString("mob",jsonObject.getString("stud_mob"));
                                    editor.putString("gender",jsonObject.getString("stud_gender"));
                                    editor.putString("stud_address",jsonObject.getString("stud_address"));
                                    editor.putString("country",jsonObject.getString("country"));
                                    editor.putString("state",jsonObject.getString("state"));
                                    editor.putString("city",jsonObject.getString("city"));
                                    editor.putString("zip_code",jsonObject.getString("Zip_code"));
                                    editor.putString("primary_skill",jsonObject.getString("primary_skill"));
                                    editor.putString("secondary_skill",jsonObject.getString("secondary_skill"));
                                    editor.putString("tertiary_skill",jsonObject.getString("tertiary_skill"));
                                    editor.putString("university",jsonObject.getString("University"));
                                    editor.putString("univ_id",jsonObject.getString("univ_id"));
                                    editor.putString("college",jsonObject.getString("College"));
                                    editor.putString("college_id",jsonObject.getString("college_id"));
                                    editor.putString("dept",jsonObject.getString("Department"));
                                    editor.putString("dept_id",jsonObject.getString("dept_id"));
                                    editor.putString("academic_session",jsonObject.getString("academic_session"));
                                    editor.putString("session_start_month",jsonObject.getString("session_start_month"));
                                    editor.putString("academic_level",jsonObject.getString("academic_level"));
                                    editor.putString("ssc_score",jsonObject.getString("ssc_score"));
                                    editor.putString("ssc_pass_yr",jsonObject.getString("ssc_pass_yr"));
                                    editor.putString("hsc_score",jsonObject.getString("hsc_score"));
                                    editor.putString("hsc_stream",jsonObject.getString("hsc_stream"));
                                    editor.putString("hsc_pass_yr",jsonObject.getString("hsc_pass_yr"));
                                    editor.putString("ug_score",jsonObject.getString("ug_score"));
                                    editor.putString("ug_stream",jsonObject.getString("ug_stram"));
                                    editor.putString("ug_pass_yr",jsonObject.getString("ug_pass_yr"));
                                    editor.putString("pg_score",jsonObject.getString("pg_score"));
                                    editor.putString("pg_stream",jsonObject.getString("pg_stream"));
                                    editor.putString("pg_pass_yr",jsonObject.getString("pg_pass_yr"));
                                    editor.putString("can_update_sem_result",jsonObject.getString("can_update_sem_result"));
                                    editor.putString("can_update_profile",jsonObject.getString("can_update_profile"));
                                    editor.putString("stud_dob",jsonObject.getString("stud_dob"));
                                    editor.apply();
                                    startActivity(new Intent(LoginActivity.this, StudentDashboardActivity.class));
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
                        Log.i(TAG,"Authentication: "+error.getMessage());
                    }
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id",id);
                params.put("password",pass);
                params.put("user_token",USER_TOKEN);
                return params;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(request);
    }

    //clear the all fields
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

    //if account is not created the create the account for faculty only
    public void goToCreateAccount(View view) {
        startActivity(new Intent(LoginActivity.this,CreateOfficerActivity.class));
    }

    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(!task.isSuccessful()){
                    Log.i(TAG, "onComplete: Fail to get Token");
                }
                USER_TOKEN = task.getResult();
                Log.i(TAG, "onComplete: "+USER_TOKEN);
            }
        });
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "firebaseNotificationChannel";
            String description = "Receive Firebase notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}