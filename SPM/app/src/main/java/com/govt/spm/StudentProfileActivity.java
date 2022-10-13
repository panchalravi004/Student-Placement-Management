package com.govt.spm;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class StudentProfileActivity extends AppCompatActivity {

    private EditText etID,etName,etMobile,etGender,etAddress,etZipCode,etPrimarySkill,etSecondarySkill,etTertiarySkill,etSession,etSSCSCore,etSSCYear,etHSCScore,etHSCYear,etUGScore,etUGYear,etPGScore,etPGYear,etDob;
    private EditText etCountry,etState,etCity,etUniv,etCollege,etDept;
    private SharedPreferences userPref;
    private Button btnUpdate;
    private ImageButton btnSelectProfileImage,btnUploadProfileImage,btnStudentProfileUploadResume,btnStudentProfileSelectResume;
    private ImageView ivProfileImage;
    private static final String TAG = "SPM_ERROR";
    private ProgressDialog dialog;

    private final int PICK_IMAGE_REQUEST = 22;
    private final int PICK_FILE_REQUEST = 23;
    private Uri filePath;
    private Bitmap bitmap;

    @SuppressLint("MissingInflatedId")
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

        etCountry = (EditText) findViewById(R.id.etStudentProfileCountry);
        etState = (EditText) findViewById(R.id.etStudentProfileState);
        etCity = (EditText) findViewById(R.id.etStudentProfileCity);
        etUniv = (EditText) findViewById(R.id.etStudentProfileUniversity);
        etCollege = (EditText) findViewById(R.id.etStudentProfileCollege);
        etDept = (EditText) findViewById(R.id.etStudentProfileDept);

        btnSelectProfileImage = (ImageButton) findViewById(R.id.btnStudentProfileSelect);
        btnUploadProfileImage = (ImageButton) findViewById(R.id.btnStudentProfileUploadImage);
        btnStudentProfileSelectResume = (ImageButton) findViewById(R.id.btnStudentProfileSelectResume);
        btnStudentProfileUploadResume = (ImageButton) findViewById(R.id.btnStudentProfileUploadResume);
        ivProfileImage = (ImageView) findViewById(R.id.ivStudentProfileImage);

        dialog = new ProgressDialog(StudentProfileActivity.this);

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
        btnSelectProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        btnStudentProfileSelectResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectResume();
            }
        });
        btnUploadProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage("PROFILE_IMAGE");;
            }
        });
        btnStudentProfileUploadResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage("RESUME");
            }
        });
    }

    private void uploadImage(String command) {
        if (filePath != null) {
            ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage("Uploading...");
            pd.show();
            StringRequest request = new StringRequest(
                    Request.Method.POST,
                    Constants.UPLOAD_IMAGE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            pd.dismiss();
                            Log.i(TAG, "onResponse: " + response);
                            Toast.makeText(StudentProfileActivity.this, "File Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i(TAG, "onErrorResponse: " + error.getMessage());
                        }
                    }) {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    if(command.equals("RESUME")){
                        params.put("file_name", userPref.getString("stud_id","stud_id")+"_RESUME_"+etName.getText().toString().trim()+".pdf");
                        params.put("upload_type", Constants.UPLOAD_TYPE_RESUME);
                        try {
                            params.put("uploaded_file", getPDFString(filePath));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }else if(command.equals("PROFILE_IMAGE")){
                        params.put("file_name", userPref.getString("stud_id","stud_id")+"_PROFILE_"+etName.getText().toString().trim()+".jpeg");
                        params.put("upload_type", Constants.UPLOAD_TYPE_PROFILE);
                        params.put("uploaded_file", getBitmapString(bitmap));
                    }

                    return params;
                }
            };
            DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            request.setRetryPolicy(retryPolicy);
            request.setShouldCache(false);
            RequestQueue requestQueue = Volley.newRequestQueue(StudentProfileActivity.this);
            requestQueue.add(request);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getBitmapString(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        byte[] imageStyle = bos.toByteArray();
        String encode = Base64.getEncoder().encodeToString(imageStyle);
        return encode;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getPDFString(Uri path) throws IOException {
        byte[] pdf = Base64.getEncoder().encode(Files.readAllBytes(Paths.get(String.valueOf(path))));
        String encode = Base64.getEncoder().encodeToString(pdf);
        return encode;
    }

    private void selectImage() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(i, "Select Image From Here..."),
                PICK_IMAGE_REQUEST);
    }
    private void selectResume() {
        Intent i = new Intent();
        i.setType("*/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(i, "Select File From Here..."),
                PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ivProfileImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
        }

    }


    private void updateProfile() {
        dialog.setMessage("Updating...");
        dialog.show();
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.UPDATE_USER_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: "+response);
                        dialog.dismiss();
                        try {
                            JSONObject jo = new JSONObject(response);
                            Toast.makeText(StudentProfileActivity.this, jo.getString("message"), Toast.LENGTH_SHORT).show();
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
                param.put("user_role",userPref.getString("role","role"));
                param.put("user_id",userPref.getString("stud_id","stud_id"));
                param.put("user_mob",userPref.getString("mob","mob"));
                param.put("stud_address",userPref.getString("stud_address","stud_address"));
                param.put("primary_skill",userPref.getString("primary_skill","primary_skill"));
                param.put("secondary_skill",userPref.getString("secondary_skill","secondary_skill"));
                param.put("tertiary_skill",userPref.getString("tertiary_skill","tertiary_skill"));
                return param;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(StudentProfileActivity.this);
        requestQueue.add(request);
        Log.i(TAG, "updateProfile: "+request);
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

        etCountry.setText(userPref.getString("country","country"));
        etState.setText(userPref.getString("state","state"));
        etCity.setText(userPref.getString("city","city"));
        etUniv.setText(userPref.getString("university","university"));
        etCollege.setText(userPref.getString("college","college"));
        etDept.setText(userPref.getString("dept","dept"));

    }

    public void goToDashboard(View view) {
        finish();
    }
}