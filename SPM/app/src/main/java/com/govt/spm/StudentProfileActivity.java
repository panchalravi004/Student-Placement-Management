package com.govt.spm;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class StudentProfileActivity extends AppCompatActivity {

    private EditText etID,etName,etMobile,etGender,etAddress,etZipCode,etPrimarySkill,etSecondarySkill,etTertiarySkill,etSession,etSSCSCore,etSSCYear,etHSCScore,etHSCYear,etUGScore,etUGYear,etPGScore,etPGYear,etDob;
    private SharedPreferences userPref;
    private Button btnUpdate;
    private static final String TAG = "SPM_ERROR";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);
        etID = (EditText) findViewById(R.id.etStudentProfileID);
        etName = (EditText) findViewById(R.id.etStudentProfileName);
        etMobile = (EditText) findViewById(R.id.etStudentProfileMobile);
        etGender = (EditText) findViewById(R.id.etStudentProfileGender);
        etAddress = (EditText) findViewById(R.id.etStudentProfileAddress);
        etZipCode = (EditText) findViewById(R.id.etStudentProfileZipCode);
        etPrimarySkill = (EditText) findViewById(R.id.etStudentProfilePrimarySkill);
        etSecondarySkill = (EditText) findViewById(R.id.etStudentProfileSecondarySkill);
        etTertiarySkill = (EditText) findViewById(R.id.etStudentProfileTertiarySkill);
        etSession = (EditText) findViewById(R.id.etStudentProfileACDSession);
        etSSCSCore = (EditText) findViewById(R.id.etStudentProfileSSCScore);
        etSSCYear = (EditText) findViewById(R.id.etStudentProfileSSCYear);
        etHSCScore = (EditText) findViewById(R.id.etStudentProfileHSCScore);
        etHSCYear = (EditText) findViewById(R.id.etStudentProfileHSCYear);
        etUGScore = (EditText) findViewById(R.id.etStudentProfileUGScore);
        etUGYear = (EditText) findViewById(R.id.etStudentProfileUGYear);
        etPGScore = (EditText) findViewById(R.id.etStudentProfilePGScore);
        etPGYear = (EditText) findViewById(R.id.etStudentProfilePGYear);
        etDob = (EditText) findViewById(R.id.etStudentProfileDOB);
        btnUpdate = (Button) findViewById(R.id.btnStudentProfileUpdate);
        userPref = getSharedPreferences("user",MODE_PRIVATE);

        //CALL METHOD
        setProfile();

        //LISTENER
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });
    }

    private void updateProfile() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.UPDATE_USER_PROFILE,
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
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("user_role",userPref.getString("role","role"));
                param.put("user_id",userPref.getString("stud_id","stud_id"));
                return param;
            }
        };
    }

    private void setProfile(){
        etID.setText(userPref.getString("stud_id","stud_id"));
        etName.setText(userPref.getString("name","name"));
        etMobile.setText(userPref.getString("mob","mob"));
        etGender.setText(userPref.getString("gender","gender"));
        etAddress.setText(userPref.getString("stud_address","stud_address"));
        etZipCode.setText(userPref.getString("zip_code","zip_code"));
        etPrimarySkill.setText(userPref.getString("primary_skill","primary_skill"));
        etSecondarySkill.setText(userPref.getString("secondary_skill","secondary_skill"));
        etTertiarySkill.setText(userPref.getString("tertiary_skill","tertiary_skill"));
        etSession.setText(userPref.getString("academic_session","academic_session"));
        etSSCSCore.setText(userPref.getString("ssc_score","ssc_score"));
        etSSCYear.setText(userPref.getString("ssc_pass_yr","ssc_pass_yr"));
        etHSCScore.setText(userPref.getString("hsc_score","hsc_score"));
        etHSCYear.setText(userPref.getString("hsc_pass_yr","hsc_pass_yr"));
        etUGScore.setText(userPref.getString("ug_score","ug_score"));
        etUGYear.setText(userPref.getString("ug_pass_yr","ug_pass_yr"));
        etPGScore.setText(userPref.getString("pg_score","pg_score"));
        etPGYear.setText(userPref.getString("pg_pass_yr","pg_pass_yr"));
        etDob.setText(userPref.getString("stud_dob","stud_dob"));

    }

    public void goToDashboard(View view) {
        finish();
    }
}