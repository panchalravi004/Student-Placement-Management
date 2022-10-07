package com.govt.spm;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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
    Context context;
    JSONArray application;

    static final String TAG = "SPM_ERROR";
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

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateStatus(String id,String status) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.UPDATE_JOB_APLICANT_STATUS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: "+response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);


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

}
