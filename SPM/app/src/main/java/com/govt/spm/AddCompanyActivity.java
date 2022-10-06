package com.govt.spm;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddCompanyActivity extends AppCompatActivity {

    private static final String TAG = "SPM_ERROR";
    private Button btnAdd,btnUpdate;
    private EditText etName,etHRName,etHREmail,etHR2Name,etHR2Email,etAbout,etDomain,etAdd;
    private Spinner spCountry,spState,spCity;
    private SharedPreferences userPref;
    private ProgressDialog dialog;

    private JSONArray jsonCountry;
    private JSONArray jsonState;
    private JSONArray jsonCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_company);
        userPref = getSharedPreferences("user",MODE_PRIVATE);
        etName=(EditText) findViewById(R.id.etAddCompanyName);
        etHRName=(EditText) findViewById(R.id.etAddCompanyHR1Name);
        etHREmail=(EditText) findViewById(R.id.etAddCompanyHR1Email);
        etHR2Name=(EditText) findViewById(R.id.etAddCompanyHR2Name);
        etHR2Email=(EditText) findViewById(R.id.etAddCompanyHR2Email);
        etAbout=(EditText) findViewById(R.id.etAddCompanyAbout);
        etDomain=(EditText) findViewById(R.id.etAddCompanyDomain);
        etAdd=(EditText) findViewById(R.id.etAddCompanyAddress);
        spCountry = (Spinner) findViewById(R.id.spAddCompanyCountry);
        spState = (Spinner) findViewById(R.id.spAddCompanyState);
        spCity = (Spinner) findViewById(R.id.spAddCompanyCity);
        btnAdd = (Button) findViewById(R.id.btnAddCompanyAdd);
        btnUpdate = (Button) findViewById(R.id.btnAddCompanyUpdate);

        dialog = new ProgressDialog(AddCompanyActivity.this);

        Intent i = getIntent();
        if(i.getStringExtra("ACTION").equals("ADD")){
            btnAdd.setVisibility(View.VISIBLE);
            btnUpdate.setVisibility(View.GONE);
        }else if(i.getStringExtra("ACTION").equals("UPDATE")){
            setProfile(i.getStringExtra("COMPANY_ID"));
            btnAdd.setVisibility(View.GONE);
            btnUpdate.setVisibility(View.VISIBLE);
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
            public void onNothingSelected(AdapterView<?> adapterView) {}
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
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    addUpdateCompany("UPDATE",i.getStringExtra("COMPANY_ID"));
                }
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    addUpdateCompany("CREATE",null);
                }
            }
        });

    }

    private void setProfile(String company_id){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.GET_COMPANY_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("SPM_ERROR", "onResponse: "+response);
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

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("SPM_ERROR", "onErrorResponse: "+error.getMessage());
                    }
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("company_id",company_id);
                return param;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(AddCompanyActivity.this);
        requestQueue.add(request);
    }

    private void addUpdateCompany(String COMMAND,String company_id) {
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
        String creator = userPref.getString("user_id","user_id");

        try {
            JSONObject jo = new JSONObject(jsonCity.getString(spCity.getSelectedItemPosition()));
            city = jo.getString("city_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String finalCity = city;
        Log.i(TAG, "addCompany: Request Sending..");
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.MANAGE_COMPANY_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        clearField();
                        Log.i(TAG, "onResponse: "+response);
                        try {
                            JSONObject jo = new JSONObject(response);
                            if(jo.getBoolean("error")){
                                Toast.makeText(AddCompanyActivity.this, jo.getString("message"), Toast.LENGTH_SHORT).show();
                            }else{
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
                        Log.i(TAG, "onErrorResponse: "+error.getMessage());
                    }
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                if(COMMAND.equals("UPDATE")){
                    params.put("cmp_id",company_id);
                }
                params.put("command",COMMAND);
                params.put("cmp_name",Name);
                params.put("hr_name",HRName);
                params.put("hr_email",HREmail);
                params.put("hr2_name",HR2Name);
                params.put("hr2_email",HR2Email);
                params.put("cmp_about",About);
                params.put("web_domain",Domain);
                params.put("cmp_addr",Addr);
                params.put("cmp_city", finalCity);
                params.put("creator_id",creator);

                return params;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(AddCompanyActivity.this);
        requestQueue.add(request);
    }

    private void fetchCountry(){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.GET_COUNTRY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: "+response);
                        try {
                            ArrayList<String> country = new ArrayList<>();
                            jsonCountry = new JSONArray(response);

                            for(int i=0;i<jsonCountry.length();i++){
                                JSONObject jo = new JSONObject(jsonCountry.getString(i));
                                country.add(jo.getString("country_name"));
//                                Log.i(TAG, "onResponse: "+jo.getString("univ_name"));
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddCompanyActivity.this, android.R.layout.simple_spinner_dropdown_item,country);
                            spCountry.setAdapter(adapter);
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
        RequestQueue requestQueue = Volley.newRequestQueue(AddCompanyActivity.this);
        requestQueue.add(request);
    }

    private void fetchState(String country_id){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.GET_STATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: "+response);
                        try {
                            ArrayList<String> state = new ArrayList<>();
                            jsonState = new JSONArray(response);

                            for(int i=0;i<jsonState.length();i++){
                                JSONObject jo = new JSONObject(jsonState.getString(i));
                                state.add(jo.getString("state_name"));
//                                Log.i(TAG, "onResponse: "+jo.getString("univ_name"));
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddCompanyActivity.this, android.R.layout.simple_spinner_dropdown_item,state);
                            spState.setAdapter(adapter);
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
                params.put("country_id",country_id);
                return params;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(AddCompanyActivity.this);
        requestQueue.add(request);
    }

    private void fetchCity(String state_id){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.GET_CITY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: "+response);
                        try {
                            ArrayList<String> city = new ArrayList<>();
                            jsonCity = new JSONArray(response);

                            for(int i=0;i<jsonCity.length();i++){
                                JSONObject jo = new JSONObject(jsonCity.getString(i));
                                city.add(jo.getString("city_name"));
//                                Log.i(TAG, "onResponse: "+jo.getString("city_name"));
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddCompanyActivity.this, android.R.layout.simple_spinner_dropdown_item,city);
                            spCity.setAdapter(adapter);
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
                params.put("state_id",state_id);
                return params;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(AddCompanyActivity.this);
        requestQueue.add(request);
    }

    private boolean validate(){
        String Name = etName.getText().toString();
        String HRName = etHRName.getText().toString();
        String HREmail = etHREmail.getText().toString();
        String About = etAbout.getText().toString();
        String Domain = etDomain.getText().toString();
        String Addr = etAdd.getText().toString();

        if(Name.equals("")){
            Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(HRName.equals("")){
            Toast.makeText(this, "Enter HRName", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(HREmail.equals("")){
            Toast.makeText(this, "Enter HREmail", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(About.equals("")){
            Toast.makeText(this, "Enter About", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(Domain.equals("")){
            Toast.makeText(this, "Enter Domain", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(Addr.equals("")){
            Toast.makeText(this, "Enter Address", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void clearField(){
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