package com.govt.spm.officer;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class AddCompanyActivity extends AppCompatActivity {

    private static final String TAG = "SPM_ERROR";
    private Button btnAdd, btnUpdate;
    private EditText etName, etHRName, etHREmail, etHR2Name, etHR2Email, etAbout, etDomain, etAdd;
    private Spinner spCountry, spState, spCity;
    private SharedPreferences userPref;
    private ProgressDialog dialog;
    private ImageView img;
    private ImageButton btnSelect, btnUpload;
    private LinearLayout companyBrowserUpload;
    private TextView tvAddCompanyTitle;

    private JSONArray jsonCountry;
    private JSONArray jsonState;
    private JSONArray jsonCity;
    private Intent myIntent;

    private final int PICK_IMAGE_REQUEST = 22;
    private Uri filePath;
    private Bitmap bitmap;
    private String cmp_id;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_company);
        userPref = getSharedPreferences("user", MODE_PRIVATE);
        etName = (EditText) findViewById(R.id.etAddCompanyName);
        etHRName = (EditText) findViewById(R.id.etAddCompanyHR1Name);
        etHREmail = (EditText) findViewById(R.id.etAddCompanyHR1Email);
        etHR2Name = (EditText) findViewById(R.id.etAddCompanyHR2Name);
        etHR2Email = (EditText) findViewById(R.id.etAddCompanyHR2Email);
        etAbout = (EditText) findViewById(R.id.etAddCompanyAbout);
        etDomain = (EditText) findViewById(R.id.etAddCompanyDomain);
        etAdd = (EditText) findViewById(R.id.etAddCompanyAddress);
        spCountry = (Spinner) findViewById(R.id.spAddCompanyCountry);
        spState = (Spinner) findViewById(R.id.spAddCompanyState);
        spCity = (Spinner) findViewById(R.id.spAddCompanyCity);
        btnAdd = (Button) findViewById(R.id.btnAddCompanyAdd);
        btnUpdate = (Button) findViewById(R.id.btnAddCompanyUpdate);
        companyBrowserUpload = (LinearLayout) findViewById(R.id.companyBrowserUpload);
        tvAddCompanyTitle = (TextView) findViewById(R.id.tvAddCompanyTitle);
        img = (ImageView) findViewById(R.id.ivAddCompanyImage);
        btnSelect = (ImageButton) findViewById(R.id.btnAddCompanySelect);
        btnUpload = (ImageButton) findViewById(R.id.btnAddCompanyUploadImage);

        dialog = new ProgressDialog(AddCompanyActivity.this);

        myIntent = getIntent();
        if (myIntent.getStringExtra("ACTION").equals("ADD")) {
            btnAdd.setVisibility(View.VISIBLE);
            btnUpdate.setVisibility(View.GONE);
            companyBrowserUpload.setVisibility(View.GONE);
            tvAddCompanyTitle.setText("Add Company");
        } else if (myIntent.getStringExtra("ACTION").equals("UPDATE")) {
            setProfile(myIntent.getStringExtra("COMPANY_ID"));
            btnAdd.setVisibility(View.GONE);
            btnUpdate.setVisibility(View.VISIBLE);
            companyBrowserUpload.setVisibility(View.VISIBLE);
            tvAddCompanyTitle.setText("Update Company");
        }

        jsonCountry = new JSONArray();
        jsonState = new JSONArray();
        jsonCity = new JSONArray();

        //fetch all country
        fetchCountry();
        spCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    JSONObject jo = new JSONObject(jsonCountry.getString(spCountry.getSelectedItemPosition()));
                    fetchState(jo.getString("country_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    JSONObject jo = new JSONObject(jsonState.getString(spState.getSelectedItemPosition()));
                    fetchCity(jo.getString("state_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    addUpdateCompany("UPDATE", myIntent.getStringExtra("COMPANY_ID"));
                }
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    addUpdateCompany("CREATE", null);
                }
            }
        });

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

    }

    //upload company browser when ACTION = UPDATE
    private void uploadImage() {
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
                            Toast.makeText(AddCompanyActivity.this, "File Uploaded Successfully", Toast.LENGTH_SHORT).show();
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
                    params.put("file_name", cmp_id+"_BROWSER.jpeg");
                    params.put("upload_type", Constants.UPLOAD_TYPE_BROWSER);
                    params.put("uploaded_file", getBitmapString(bitmap));
                    return params;
                }
            };
            DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            request.setRetryPolicy(retryPolicy);
            request.setShouldCache(false);
            RequestQueue requestQueue = Volley.newRequestQueue(AddCompanyActivity.this);
            requestQueue.add(request);
        }
    }

    //get bitmap to string
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getBitmapString(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        byte[] imageStyle = bos.toByteArray();
        String encode = Base64.getEncoder().encodeToString(imageStyle);
        Log.i(TAG, "getBitmapString: " + encode);
        return encode;
    }

    //select image from phone
    private void selectImage() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(i, "Select Image From Here..."),
                PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                img.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //set company profile when ACTION = UPDATE
    private void setProfile(String company_id) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.GET_COMPANY_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "setProfile: " + response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            etName.setText(new JSONObject(jsonArray.getString(0)).getString("COMPANY_NAME"));
                            etDomain.setText(new JSONObject(jsonArray.getString(0)).getString("WEB_DOMAIN"));
                            etAdd.setText(new JSONObject(jsonArray.getString(0)).getString("ADDRESS"));
                            etHRName.setText(new JSONObject(jsonArray.getString(0)).getString("HR1_NAME"));
                            etHREmail.setText(new JSONObject(jsonArray.getString(0)).getString("HR1_EMAIL"));
                            etHR2Name.setText(new JSONObject(jsonArray.getString(0)).getString("HR2_NAME"));
                            etHR2Email.setText(new JSONObject(jsonArray.getString(0)).getString("HR2_EMAIL"));
                            etAbout.setText(new JSONObject(jsonArray.getString(0)).getString("ABOUT"));
                            cmp_id = new JSONObject(jsonArray.getString(0)).getString("COMPANY_ID");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "setProfile: " + error.getMessage());
                    }
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("company_id", company_id);
                return param;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(AddCompanyActivity.this);
        requestQueue.add(request);
    }

    //add or update company as per command
    private void addUpdateCompany(String COMMAND, String company_id) {
        dialog.setMessage("Adding...");
        dialog.show();

        String Name = etName.getText().toString();
        String HRName = etHRName.getText().toString();
        String HREmail = etHREmail.getText().toString();
        String HR2Name = etHR2Name.getText().toString();
        String HR2Email = etHR2Email.getText().toString();
        String About = etAbout.getText().toString();
        String Domain = etDomain.getText().toString();
        String Addr = etAdd.getText().toString();
        String city = null;
        String creator = userPref.getString("user_id", "user_id");

        try {
            JSONObject jo = new JSONObject(jsonCity.getString(spCity.getSelectedItemPosition()));
            city = jo.getString("city_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String finalCity = city;

        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.MANAGE_COMPANY_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        clearField();
                        Log.i(TAG, "addUpdateCompany: " + response);
                        try {
                            JSONObject jo = new JSONObject(response);
                            if (jo.getBoolean("error")) {
                                Toast.makeText(AddCompanyActivity.this, jo.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AddCompanyActivity.this, jo.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "addUpdateCompany: " + error.getMessage());
                    }
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                if (COMMAND.equals("UPDATE")) {
                    params.put("cmp_id", company_id);
                }
                params.put("command", COMMAND);
                params.put("cmp_name", Name);
                params.put("hr_name", HRName);
                params.put("hr_email", HREmail);
                params.put("hr2_name", HR2Name);
                params.put("hr2_email", HR2Email);
                params.put("cmp_about", About);
                params.put("web_domain", Domain);
                params.put("cmp_addr", Addr);
                params.put("cmp_city", finalCity);
                params.put("creator_id", creator);

                return params;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(AddCompanyActivity.this);
        requestQueue.add(request);
    }

    //fetch all country list and set on spinner
    private void fetchCountry() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.GET_COUNTRY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "fetchCountry: "+response);
                        try {
                            ArrayList<String> country = new ArrayList<>();
                            jsonCountry = new JSONArray(response);

                            for (int i = 0; i < jsonCountry.length(); i++) {
                                JSONObject jo = new JSONObject(jsonCountry.getString(i));
                                if (myIntent.getStringExtra("ACTION").equals("UPDATE")) {
//                                    if (myIntent.getStringExtra("COUNTRY_ID").equals(jo.getString("country_id"))) {
//                                        country.add(0, jo.getString("country_name"));
//                                        JSONObject temp = new JSONObject(jsonCountry.getString(i));
//                                        jsonCountry.remove(i);
//                                        jsonCountry.put(0, temp);
//
//                                    } else {
                                        country.add(jo.getString("country_name"));
//                                    }
                                } else {
                                    country.add(jo.getString("country_name"));
                                }
//                                Log.i(TAG, "onResponse: "+jo.getString("univ_name"));
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddCompanyActivity.this, android.R.layout.simple_spinner_dropdown_item, country);
                            spCountry.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "fetchCountry: " + error.getMessage());
                    }
                });
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(AddCompanyActivity.this);
        requestQueue.add(request);
    }

    //fetch state list as per country
    private void fetchState(String country_id) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.GET_STATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "fetchState: "+response);
                        try {
                            ArrayList<String> state = new ArrayList<>();
                            jsonState = new JSONArray(response);

                            for (int i = 0; i < jsonState.length(); i++) {
                                JSONObject jo = new JSONObject(jsonState.getString(i));
                                if (myIntent.getStringExtra("ACTION").equals("UPDATE")) {
//                                    if (myIntent.getStringExtra("STATE_ID").equals(jo.getString("state_id"))) {
//                                        state.add(0, jo.getString("state_name"));
//                                        JSONObject temp = new JSONObject(jsonState.getString(i));
//                                        jsonState.remove(i);
//                                        jsonState.put(0, temp);
//                                    } else {
                                        state.add(jo.getString("state_name"));
//                                    }
                                } else {
                                    state.add(jo.getString("state_name"));
                                }

                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddCompanyActivity.this, android.R.layout.simple_spinner_dropdown_item, state);
                            spState.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "fetchState: " + error.getMessage());
                    }
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("country_id", country_id);
                return params;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(AddCompanyActivity.this);
        requestQueue.add(request);
    }

    //fetch city as per state
    private void fetchCity(String state_id) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.GET_CITY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "fetchCity: "+response);
                        try {
                            ArrayList<String> city = new ArrayList<>();
                            jsonCity = new JSONArray(response);

                            for (int i = 0; i < jsonCity.length(); i++) {
                                JSONObject jo = new JSONObject(jsonCity.getString(i));
                                if (myIntent.getStringExtra("ACTION").equals("UPDATE")) {
//                                    if (myIntent.getStringExtra("CITY_ID").equals(jo.getString("city_id"))) {
//                                        city.add(0, jo.getString("city_name"));
//                                        JSONObject temp = new JSONObject(jsonCity.getString(i));
//                                        jsonCity.remove(i);
//                                        jsonCity.put(0, temp);
//
//                                    } else {
                                        city.add(jo.getString("city_name"));
//                                    }
                                } else {
                                    city.add(jo.getString("city_name"));
                                }
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddCompanyActivity.this, android.R.layout.simple_spinner_dropdown_item, city);
                            spCity.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "fetchCity: " + error.getMessage());
                    }
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("state_id", state_id);
                return params;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(AddCompanyActivity.this);
        requestQueue.add(request);
    }

    private boolean validate() {
        String Name = etName.getText().toString();
        String HRName = etHRName.getText().toString();
        String HREmail = etHREmail.getText().toString();
        String About = etAbout.getText().toString();
        String Domain = etDomain.getText().toString();
        String Addr = etAdd.getText().toString();

        if (Name.equals("")) {
            Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (HRName.equals("")) {
            Toast.makeText(this, "Enter HRName", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (HREmail.equals("")) {
            Toast.makeText(this, "Enter HREmail", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (About.equals("")) {
            Toast.makeText(this, "Enter About", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (Domain.equals("")) {
            Toast.makeText(this, "Enter Domain", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (Addr.equals("")) {
            Toast.makeText(this, "Enter Address", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void clearField() {
        etName.setText("");
        etHRName.setText("");
        etHREmail.setText("");
        etHR2Name.setText("");
        etHR2Email.setText("");
        etAbout.setText("");
        etDomain.setText("");
        etAdd.setText("");
    }

    public void goToDashboard(View view) {
        finish();
    }
}
