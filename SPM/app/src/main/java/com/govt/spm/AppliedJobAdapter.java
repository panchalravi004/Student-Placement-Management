package com.govt.spm;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AppliedJobAdapter extends RecyclerView.Adapter<AppliedJobAdapter.VHolder> {
    
    private Context context;
    private JSONArray job;
    private TextView tvCompanyName,tvCompanyDomain,tvCompanyAddress,tvHR1Name,tvHR1Email,tvHR2Name,tvHR2Email,tvDescription,tvRole,tvSkill,tvSSC,tvHSC,tvUG,tvPG,tvMinQuali,tvSDate,tvEDate;
    private ImageButton btnClose;
    private Button btnShare,btnApply,btnShowApplicant;
    private static final String TAG = "SPM_ERROR";
    
    public AppliedJobAdapter(Context context,JSONArray job){
        this.context = context;
        this.job = job;
    }

    @NonNull
    @Override
    public AppliedJobAdapter.VHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_applied_job,parent,false);
        return new VHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VHolder holder, int position) {
        try {
            JSONObject jo = new JSONObject(job.getString(position));
            holder.jobStatus.setText(jo.getString("AppStat"));
            holder.clgName.setText(jo.getString("MIN_QUALIFICATION").toUpperCase(Locale.ROOT));
            if(jo.getString("COMPANY_NAME").length()>20){
                holder.cName.setText(jo.getString("COMPANY_NAME").substring(0,25)+"...");
            }else{
                holder.cName.setText(jo.getString("COMPANY_NAME"));
            }
            holder.btnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDialog(jo);
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

    public class VHolder extends RecyclerView.ViewHolder {
        TextView cName,clgName,jobStatus;
        ImageButton btnView;
        public VHolder(@NonNull View itemView) {
            super(itemView);
            cName = itemView.findViewById(R.id.tvAppliedJobCompanyName);
            clgName = itemView.findViewById(R.id.tvAppliedJobCollegeName);
            jobStatus = itemView.findViewById(R.id.tvAppliedJobStatus);
            btnView = itemView.findViewById(R.id.btnAppliedJobView);

        }
    }

    //open dialog for company and job post details
    private void openDialog(JSONObject jo) {

        Dialog dialog = new Dialog(context,R.style.DialogStyle);
        dialog.setContentView(R.layout.dialog_details_jobs_post);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.white_all_round);

        btnClose = dialog.findViewById(R.id.btnClose);
        btnShare = dialog.findViewById(R.id.btnJobPostShare);
        btnApply = dialog.findViewById(R.id.btnJobPostApply);
        btnShowApplicant = dialog.findViewById(R.id.btnJobPostViewAplicant);
        btnShare.setVisibility(View.GONE);
        btnApply.setVisibility(View.GONE);
        btnShowApplicant.setVisibility(View.GONE);
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

        try {
            tvDescription.setText(jo.getString("JOB_DESC"));
            tvRole.setText(jo.getString("ROLE"));
            tvSkill.setText(jo.getString("SKILLS"));
            tvSSC.setText(jo.getString("REQ_SSC_SCORE"));
            tvHSC.setText(jo.getString("REQ_HSC_SCORE"));
            tvUG.setText(jo.getString("REQ_UG_SCORE"));
            tvPG.setText(jo.getString("REQ_PG_SCORE"));
            tvMinQuali.setText(jo.getString("MIN_QUALIFICATION"));
            tvSDate.setText(jo.getString("REG_START_DATE"));
            tvEDate.setText(jo.getString("REG_END_DATE"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        //fetch the company profile data and set it
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.GET_COMPANY_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "Fetch Company Profile: "+response);
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
                        Log.i(TAG, "Fetch Company Profile: "+error.getMessage());
                    }
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                try {
                    param.put("company_id",jo.getString("COMPANY_ID"));
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

        //close the dialog
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

}
