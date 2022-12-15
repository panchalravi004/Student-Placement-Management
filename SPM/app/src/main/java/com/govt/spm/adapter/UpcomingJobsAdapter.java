package com.govt.spm.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UpcomingJobsAdapter extends RecyclerView.Adapter<UpcomingJobsAdapter.VHolder> {
    
    private Context context;
    private JSONArray job;
    private TextView tvCompanyName,tvCompanyDomain,tvCompanyAddress,tvHR1Name,tvHR1Email,tvHR2Name,tvHR2Email,tvDescription,tvRole,tvSkill,tvSSC,tvHSC,tvUG,tvPG,tvMinQuali,tvSDate,tvEDate;
    private ImageButton btnClose;
    private Button btnShare,btnApply,btnShowApplicant;
    static final String TAG = "SPM_ERROR";
    
    public UpcomingJobsAdapter(Context context,JSONArray job){
        this.context = context;
        this.job = job;
    }

    @NonNull
    @Override
    public UpcomingJobsAdapter.VHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_upcoming_jobs,parent,false);
        return new VHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UpcomingJobsAdapter.VHolder holder, int position) {
        try {
            JSONObject jo = new JSONObject(job.getString(position));
            holder.rEDate.setText("Ends on : "+jo.getString("reg_end_date"));
            holder.clgName.setText(jo.getString("min_qualification").toUpperCase(Locale.ROOT));
            StringRequest request = new StringRequest(
                    Request.Method.POST,
                    Constants.GET_COMPANY_PROFILE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i(TAG, "Fetch Company Profile: "+response);
                            try {
                                holder.jsonArrayCompanyProfile = new JSONArray(response);
                                if(new JSONObject(holder.jsonArrayCompanyProfile.getString(0)).getString("COMPANY_NAME").length()>20){
                                    holder.cName.setText(new JSONObject(holder.jsonArrayCompanyProfile.getString(0)).getString("COMPANY_NAME").substring(0,25)+"...");
                                }else{
                                    holder.cName.setText(new JSONObject(holder.jsonArrayCompanyProfile.getString(0)).getString("COMPANY_NAME"));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i(TAG, "Fetch Company Profile: "+error.getMessage());
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
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDialog(jo,holder.jsonArrayCompanyProfile);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return job.length();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class VHolder extends RecyclerView.ViewHolder {
        TextView cName,clgName,rEDate;
        JSONArray jsonArrayCompanyProfile;
        public VHolder(@NonNull View itemView) {
            super(itemView);
            cName = itemView.findViewById(R.id.tvUJACompanyName);
            clgName = itemView.findViewById(R.id.tvUJACollegeName);
            rEDate = itemView.findViewById(R.id.tvUJARegisterEndDate);
            jsonArrayCompanyProfile = new JSONArray();
        }
    }

    //open dialog to show details of job post and company
    private void openDialog(JSONObject jo,JSONArray jsonArrayCompanyProfile) {

        Dialog dialog = new Dialog(context,R.style.DialogStyle);
        dialog.setContentView(R.layout.dialog_details_jobs_post);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.white_all_round);

        btnClose = dialog.findViewById(R.id.btnClose);
        btnShare = dialog.findViewById(R.id.btnJobPostShare);
        btnApply = dialog.findViewById(R.id.btnJobPostApply);
        btnShowApplicant = dialog.findViewById(R.id.btnJobPostViewAplicant);
        btnShowApplicant.setVisibility(View.GONE);
        btnShare.setVisibility(View.GONE);
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
        SharedPreferences userPref = context.getSharedPreferences("user",Context.MODE_PRIVATE);

        if(userPref.getString("role","role").equals("FACULTY")){
            btnApply.setVisibility(View.GONE);
        }

        //apply on job post
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    applyInJob(jo.getString("job_id"),userPref.getString("stud_id","stud_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        //set company details
        try {
            tvCompanyName.setText(new JSONObject(jsonArrayCompanyProfile.getString(0)).getString("COMPANY_NAME"));
            tvCompanyDomain.setText(new JSONObject(jsonArrayCompanyProfile.getString(0)).getString("WEB_DOMAIN"));
            tvCompanyAddress.setText(new JSONObject(jsonArrayCompanyProfile.getString(0)).getString("ADDRESS"));
            tvHR1Name.setText(new JSONObject(jsonArrayCompanyProfile.getString(0)).getString("HR1_NAME"));
            tvHR1Email.setText(new JSONObject(jsonArrayCompanyProfile.getString(0)).getString("HR1_EMAIL"));
            tvHR2Name.setText(new JSONObject(jsonArrayCompanyProfile.getString(0)).getString("HR2_NAME"));
            tvHR2Email.setText(new JSONObject(jsonArrayCompanyProfile.getString(0)).getString("HR2_EMAIL"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //fetch job details
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.GET_JOB_DETAIL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "Fetch Job Detail: "+response);
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
                        Log.i(TAG, "Fetch Job Detail: "+error.getMessage());
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

        //close dialog box
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
    //apply in job post
    private void applyInJob(String job_id, String stud_id) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.APPLY_FOR_JOB,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "applyInJob: "+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(jsonObject.getBoolean("error")){
                                Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "applyInJob: "+error.getMessage());
                    }
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();

                param.put("job_id",job_id);
                param.put("stud_id",stud_id);

                return param;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);

    }
}
