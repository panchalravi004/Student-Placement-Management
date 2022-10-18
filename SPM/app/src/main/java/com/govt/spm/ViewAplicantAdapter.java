package com.govt.spm;

import android.app.Dialog;
import android.content.Context;
import android.media.Image;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ViewAplicantAdapter extends RecyclerView.Adapter<ViewAplicantAdapter.VHolder> {

    private Context context;
    private JSONArray application;
    private TextView tvName,tvEnrollNo,tvEmail,tvMobile,tvAdd,tvPrimarySkill,tvSecondarySkill,tvTertiarySkill,tvAcadamicYear,tvSSCPer,tvSSCYear,tvHSCPer,tvHSCYear,tvUGPer,tvUGYear,tvPGPer,tvPGYear,tvHSCStream,tvUGStream,tvPGStream,tvCurrentSem;
    private static final String TAG = "SPM_ERROR";

    public ViewAplicantAdapter(Context context,JSONArray application){
        this.context = context;
        this.application = application;
    }

    @NonNull
    @Override
    public ViewAplicantAdapter.VHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_aplicant_view,parent,false);
        return new VHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VHolder holder, int position) {
        try {
            JSONObject jo = new JSONObject(application.getString(position));
            holder.sname.setText(jo.getString("stud_name"));
            holder.status.setText(jo.getString("application_stat"));
            holder.btnApprove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        updateStatus(jo.getString("id"),"APPROVED");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            holder.btnReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        updateStatus(jo.getString("id"),"REJECTED");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
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
        return application.length();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public class VHolder extends RecyclerView.ViewHolder {
        TextView sname,status;
        ImageButton btnApprove,btnReject;
        public VHolder(@NonNull View itemView) {
            super(itemView);
            sname = itemView.findViewById(R.id.tvAplicantStudentName);
            status = itemView.findViewById(R.id.tvAplicantStudentStatus);
            btnApprove = itemView.findViewById(R.id.btnAplicantStudentApprove);
            btnReject = itemView.findViewById(R.id.btnAplicantStudentReject);
        }
    }

    //update job application status
    private void updateStatus(String id,String status) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.UPDATE_JOB_APLICANT_STATUS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "updateStatus: "+response);
                        try {
                            JSONObject jsonArray = new JSONObject(response);
                            Toast.makeText(context, jsonArray.getString("message"), Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "updateStatus: "+error.getMessage());
                    }
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("app_id",id);
                param.put("app_stat",status);
                return param;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    //student detail dialog
    private void openDialog(JSONObject info) {
        Dialog dialog = new Dialog(context,R.style.DialogStyle);
        dialog.setContentView(R.layout.dialog_details_student);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.white_all_round);
        tvName = dialog.findViewById(R.id.tvStudentInfoName);
        tvEnrollNo = dialog.findViewById(R.id.tvStudentInfoEnrollNo);
        tvEmail = dialog.findViewById(R.id.tvStudentInfoEmail);
        tvMobile = dialog.findViewById(R.id.tvStudentInfoMobileNo);
        tvAdd = dialog.findViewById(R.id.tvStudentInfoAdd);
        tvPrimarySkill = dialog.findViewById(R.id.tvStudentInfoPrimarySkill);
        tvSecondarySkill = dialog.findViewById(R.id.tvStudentInfoSecondarySkill);
        tvTertiarySkill = dialog.findViewById(R.id.tvStudentInfoTertiarySkill);
        tvAcadamicYear = dialog.findViewById(R.id.tvStudentInfoAcadamicYear);
        tvSSCPer = dialog.findViewById(R.id.tvStudentInfoSSCPer);
        tvSSCYear = dialog.findViewById(R.id.tvStudentInfoSSCYear);
        tvHSCPer = dialog.findViewById(R.id.tvStudentInfoHSCPer);
        tvHSCYear = dialog.findViewById(R.id.tvStudentInfoHSCYear);
        tvUGPer = dialog.findViewById(R.id.tvStudentInfoUGPer);
        tvUGYear = dialog.findViewById(R.id.tvStudentInfoUGYear);
        tvPGPer = dialog.findViewById(R.id.tvStudentInfoPGPer);
        tvPGYear = dialog.findViewById(R.id.tvStudentInfoPGYear);
        tvHSCStream = dialog.findViewById(R.id.tvStudentInfoHSCStream);
        tvUGStream = dialog.findViewById(R.id.tvStudentInfoUGStream);
        tvPGStream = dialog.findViewById(R.id.tvStudentInfoPGStream);
        tvCurrentSem = dialog.findViewById(R.id.tvStudentInfoCurrentSem);

        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.GET_STUDENT_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: "+response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            tvName.setText(new JSONObject(jsonArray.getString(0)).getString("stud_name"));
                            tvEnrollNo.setText(new JSONObject(jsonArray.getString(0)).getString("stud_id"));
                            tvMobile.setText(new JSONObject(jsonArray.getString(0)).getString("stud_mob"));
                            tvAdd.setText(new JSONObject(jsonArray.getString(0)).getString("stud_address"));
                            tvPrimarySkill.setText(new JSONObject(jsonArray.getString(0)).getString("primary_skill"));
                            tvSecondarySkill.setText(new JSONObject(jsonArray.getString(0)).getString("secondary_skill"));
                            tvTertiarySkill.setText(new JSONObject(jsonArray.getString(0)).getString("tertiary_skill"));
                            tvAcadamicYear.setText(new JSONObject(jsonArray.getString(0)).getString("academic_session"));
                            tvSSCPer.setText(new JSONObject(jsonArray.getString(0)).getString("ssc_score"));
                            tvSSCYear.setText(new JSONObject(jsonArray.getString(0)).getString("ssc_pass_yr"));
                            tvHSCPer.setText(new JSONObject(jsonArray.getString(0)).getString("hsc_score"));
                            tvHSCYear.setText(new JSONObject(jsonArray.getString(0)).getString("hsc_pass_yr"));
                            tvUGPer.setText(new JSONObject(jsonArray.getString(0)).getString("ug_score"));
                            tvUGYear.setText(new JSONObject(jsonArray.getString(0)).getString("ug_pass_yr"));
                            tvPGPer.setText(new JSONObject(jsonArray.getString(0)).getString("pg_score"));
                            tvPGYear.setText(new JSONObject(jsonArray.getString(0)).getString("pg_pass_yr"));
                            tvHSCStream.setText(new JSONObject(jsonArray.getString(0)).getString("hsc_stream"));
                            tvUGStream.setText(new JSONObject(jsonArray.getString(0)).getString("ug_stram"));
                            tvPGStream.setText(new JSONObject(jsonArray.getString(0)).getString("pg_stream"));
                            tvCurrentSem.setText(new JSONObject(jsonArray.getString(0)).getString("sem"));

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
                    param.put("stud_id",info.getString("stud_id"));
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


        dialog.show();
    }

}
