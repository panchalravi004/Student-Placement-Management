package com.govt.spm;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
//import org.apache.commons.io.FileUtils;
import org.json.*;

public class JobsAdapter extends RecyclerView.Adapter<JobsAdapter.VHolder> {
    Context context;

    JSONArray jobs;
    SharedPreferences userPref;
    ImageButton btnClose;
    private TextView tvCompanyName,tvCompanyDomain,tvCompanyAddress,tvHR1Name,tvHR1Email,tvHR2Name,tvHR2Email,tvDescription,tvRole,tvSkill,tvSSC,tvHSC,tvUG,tvPG,tvMinQuali,tvSDate,tvEDate;

    private static final String TAG = "SPM_ERROR";
    Button btnShare,btnApply,btnShowAplicant;
    Uri URI = null;
    private static final int PICK_FROM_GALLERY = 101;
    public JobsAdapter(Context context,JSONArray jobs){
        this.context = context;
        this.jobs = jobs;
    }

    @NonNull
    @Override
    public JobsAdapter.VHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_jobs,parent,false);
        return new VHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobsAdapter.VHolder holder, int position) {
        try {
            JSONObject jo = new JSONObject(jobs.getString(position));
            holder.rEDate.setText("Ends on : "+jo.getString("reg_end_date"));
            holder.clgName.setText(jo.getString("min_qualification"));

            if(holder.userPref.getString("user_id","user_id").equals(jo.getString("creator_id"))){
                if(holder.userPref.getString("CAN_UPDATE_COMPANY","CAN_UPDATE_COMPANY").equals("0")){
                    holder.btnEdit.setVisibility(View.GONE);
                }else{
                    holder.btnEdit.setVisibility(View.VISIBLE);
                }
                if(holder.userPref.getString("CAN_REJECT_JOB_APPLICATION","CAN_REJECT_JOB_APPLICATION").equals("0")){
                    holder.btnDelete.setVisibility(View.GONE);
                }else{
                    holder.btnDelete.setVisibility(View.VISIBLE);
                }
            }else{
                holder.btnEdit.setVisibility(View.GONE);
                holder.btnDelete.setVisibility(View.GONE);
            }

            StringRequest request = new StringRequest(
                    Request.Method.POST,
                    Constants.GET_COMPANY_PROFILE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i(TAG, "onResponse: "+response);
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                if(new JSONObject(jsonArray.getString(0)).getString("COMPANY_NAME").length()>20){
                                    holder.cName.setText(new JSONObject(jsonArray.getString(0)).getString("COMPANY_NAME").substring(0,25)+"...");
                                }else{
                                    holder.cName.setText(new JSONObject(jsonArray.getString(0)).getString("COMPANY_NAME"));
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
                    Map<String, String> param = new HashMap<>();
                    try {
                        param.put("company_id",jo.getString("company_id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return param;
                }
            };
            DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            request.setRetryPolicy(retryPolicy);
            request.setShouldCache(false);
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(request);

            holder.btnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDialog(jo);
                }
            });
            holder.btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context,AddJobsActivity.class);
                    i.putExtra("ACTION","UPDATE");
                    try {
                        i.putExtra("job_id",jo.getString("job_id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    context.startActivity(i);
                }
            });
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        deleteJobPost(jo.getString("job_id"),jo.getString("creator_id"),"DELETE");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return jobs.length();
    }

    public class VHolder extends RecyclerView.ViewHolder{
        TextView cName,clgName,rEDate;
        ImageButton btnView,btnEdit,btnDelete;
        SharedPreferences userPref;
        public VHolder(@NonNull View itemView) {
            super(itemView);
            cName = itemView.findViewById(R.id.tvJobsAdapterCompanyName);
            clgName = itemView.findViewById(R.id.tvJobsAdapterCollegeName);
            rEDate = itemView.findViewById(R.id.tvJobsAdapterRegisterEndDate);
            btnView = itemView.findViewById(R.id.btnJobsAdapterView);
            btnEdit = itemView.findViewById(R.id.btnJobsAdapterEdit);
            btnDelete = itemView.findViewById(R.id.btnJobsAdapterDelete);
            userPref = context.getSharedPreferences("user",Context.MODE_PRIVATE);

        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    private void openDialog(JSONObject jo) {
        Dialog dialog = new Dialog(context,R.style.DialogStyle);
        dialog.setContentView(R.layout.dialog_details_jobs_post);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.white_all_round);

        btnClose = dialog.findViewById(R.id.btnClose);
        btnShare = dialog.findViewById(R.id.btnJobPostShare);
        btnApply = dialog.findViewById(R.id.btnJobPostApply);
        btnShowAplicant = dialog.findViewById(R.id.btnJobPostViewAplicant);
        btnApply.setVisibility(View.GONE);
        tvCompanyName = (TextView) dialog.findViewById(R.id.tvJobPostCompanyName);
        tvCompanyDomain = (TextView) dialog.findViewById(R.id.tvJobPostCompanyDomain);
        tvCompanyAddress = (TextView) dialog.findViewById(R.id.tvJobPostCompanyAddress);
        tvHR1Name = (TextView) dialog.findViewById(R.id.tvJobPostHR1Name);
        tvHR1Email = (TextView) dialog.findViewById(R.id.tvJobPostHR1Email);
        tvHR2Name = (TextView) dialog.findViewById(R.id.tvJobPostHR2Name);
        tvHR2Email = (TextView) dialog.findViewById(R.id.tvJobPostHR2Email);
        tvDescription = (TextView) dialog.findViewById(R.id.tvJobPostDescription);
        tvRole = (TextView) dialog.findViewById(R.id.tvJobPostRole);
        tvSkill = (TextView) dialog.findViewById(R.id.tvJobPostSkill);
        tvSSC = (TextView) dialog.findViewById(R.id.tvJobPostSSC);
        tvHSC = (TextView) dialog.findViewById(R.id.tvJobPostHSC);
        tvUG = (TextView) dialog.findViewById(R.id.tvJobPostUG);
        tvPG = (TextView) dialog.findViewById(R.id.tvJobPostPG);
        tvMinQuali = (TextView) dialog.findViewById(R.id.tvJobPostMINQuali);
        tvSDate = (TextView) dialog.findViewById(R.id.tvJobPostSDate);
        tvEDate = (TextView) dialog.findViewById(R.id.tvJobPostEDate);

        //show fetch data here
        StringRequest crequest = new StringRequest(
                Request.Method.POST,
                Constants.GET_COMPANY_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: "+response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            tvCompanyName.setText(new JSONObject(jsonArray.getString(0)).getString("COMPANY_NAME"));
                            tvCompanyDomain.setText(new JSONObject(jsonArray.getString(0)).getString("WEB_DOMAIN"));
                            tvCompanyAddress.setText(new JSONObject(jsonArray.getString(0)).getString("ADDRESS"));
                            tvHR1Name.setText(new JSONObject(jsonArray.getString(0)).getString("HR1_NAME"));
                            tvHR1Email.setText(new JSONObject(jsonArray.getString(0)).getString("HR1_EMAIL"));
                            tvHR2Name.setText(new JSONObject(jsonArray.getString(0)).getString("HR2_NAME"));
                            tvHR2Email.setText(new JSONObject(jsonArray.getString(0)).getString("HR2_EMAIL"));

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
                try {
                    param.put("company_id",jo.getString("company_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return param;
            }
        };
        DefaultRetryPolicy cretryPolicy = new DefaultRetryPolicy(6000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        crequest.setRetryPolicy(cretryPolicy);
        crequest.setShouldCache(false);
        RequestQueue crequestQueue = Volley.newRequestQueue(context);
        crequestQueue.add(crequest);

        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.GET_JOB_DETAIL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: "+response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            tvDescription.setText(new JSONObject(jsonArray.getString(0)).getString("JOB_DESC"));
                            tvRole.setText(new JSONObject(jsonArray.getString(0)).getString("ROLE"));
                            tvSkill.setText(new JSONObject(jsonArray.getString(0)).getString("SKILLS"));
                            tvSSC.setText(new JSONObject(jsonArray.getString(0)).getString("REQ_SSC_SCORE"));
                            tvHSC.setText(new JSONObject(jsonArray.getString(0)).getString("REQ_HSC_SCORE"));
                            tvUG.setText(new JSONObject(jsonArray.getString(0)).getString("REQ_UG_SCORE"));
                            tvPG.setText(new JSONObject(jsonArray.getString(0)).getString("REQ_PG_SCORE"));
                            tvMinQuali.setText(new JSONObject(jsonArray.getString(0)).getString("MIN_QUALIFICATION"));
                            tvSDate.setText(new JSONObject(jsonArray.getString(0)).getString("REG_START_DATE"));
                            tvEDate.setText(new JSONObject(jsonArray.getString(0)).getString("REG_END_DATE"));

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
                try {
                    param.put("job_id",jo.getString("job_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return param;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);

        btnShowAplicant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent i = new Intent(context,ViewAplicantActivity.class);
                try {
                    i.putExtra("JOB_ID",jo.getString("job_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                context.startActivity(i);
            }
        });
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    getExcelData(jo.getString("job_id"),tvCompanyName.getText().toString(),jo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void getExcelData(String job_id,String company_name,JSONObject jo) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.GET_JOB_APLICANT,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: "+response);
//                        File file = new File("/");
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                                ActivityCompat.requestPermissions((Activity) context,new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

                                String filename = company_name+"_"+""+new JSONObject(jsonArray.getString(0)).getString("job_id")+"_"+String.valueOf(java.time.LocalDate.now())+".csv";
                                String csv = CDL.toString(jsonArray);
                                FileOutputStream fos;
                                try {
                                    File myFile = new File("/sdcard/"+filename);
                                    myFile.createNewFile();
                                    fos = new FileOutputStream(myFile);
                                    OutputStreamWriter osw = new OutputStreamWriter(fos);
                                    osw.append(csv);
                                    osw.close();
                                    fos.close();
//                                    fos.write(csv.getBytes());
                                    Toast.makeText(context, "saved "+myFile.getPath(), Toast.LENGTH_SHORT).show();
                                    //send email
                                    Uri ur = Uri.parse(myFile.getPath());
                                    if(!tvHR1Email.getText().toString().equals(null) || !tvHR1Email.getText().toString().equals("null")){
                                        sendEmail(new String[]{tvHR1Email.getText().toString(),tvHR2Email.getText().toString()}, FileProvider.getUriForFile(context,context.getApplicationContext().getPackageName() + ".provider",myFile),jo);
                                    }else{
                                        sendEmail(new String[]{tvHR1Email.getText().toString()}, FileProvider.getUriForFile(context,context.getApplicationContext().getPackageName() + ".provider",myFile),jo);
                                    }
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
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
                        Log.i(TAG, "onErrorResponse: "+error.getMessage());
                    }
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("job_id",job_id);
                return param;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);

    }

    private void sendEmail(String[] hrEmails, Uri uri,JSONObject jo) throws JSONException {
        SharedPreferences userPref = context.getSharedPreferences("user",Context.MODE_PRIVATE);
        String message = "Greeting, We will send a list of applicant with the attachment for the requirement of "
                +jo.getString("role")
                +", Where minimum qualification is "
                +jo.getString("min_qualification")
                +" and Registration Ending on "
                +jo.getString("reg_end_date")+"."
                +"Thank You.";
        try{
            final Intent email = new Intent(Intent.ACTION_SEND);

//            email.setType("plain/text");
            email.setType("application/excel");
            email.putExtra(Intent.EXTRA_EMAIL,hrEmails);
            email.putExtra(Intent.EXTRA_SUBJECT,userPref.getString("univ","univ")+" | Job Applicant");
            email.putExtra(Intent.EXTRA_TEXT,message);
            if(uri != null){
                email.putExtra(Intent.EXTRA_STREAM,uri);
            }
            context.startActivity(Intent.createChooser(email,"Sending Email..."));
            Toast.makeText(context, "Sent", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void deleteJobPost(String job_id,String creator_id,String command){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.MANAGE_JOB_DETAIL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: "+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

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
                params.put("command",command);
                params.put("job_id",job_id);
                params.put("creator_id",creator_id);

                return params;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }


}
