package com.govt.spm;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import java.util.Map;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.VHolder> {
    Context context;
//    ArrayList<HashMap<String,String>> company;
    JSONArray company;
    ImageButton btnClose,btnEdit,btnDelete;
    TextView tvCpName,tvDomain,tvAdd,tvHr1name,tvHr1email,tvHr2name,tvHr2email,tvAbout;

    private static final String TAG = "SPM_ERROR";

    public CompanyAdapter(Context context,JSONArray company){
        this.context = context;
        this.company = company;
    }

    @NonNull
    @Override
    public CompanyAdapter.VHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_company,parent,false);
        return new VHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyAdapter.VHolder holder, int position) {
        try {
            JSONObject jo = new JSONObject(company.getString(position));
            holder.cname.setText(jo.getString("company_name"));
            holder.domain.setText(jo.getString("web_domain"));
            holder.btnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        openDialog(new JSONObject(company.getString(holder.getAdapterPosition())).getString("company_id"),new JSONObject(company.getString(holder.getAdapterPosition())).getString("creator_id"));
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
        return company.length();
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
            TextView cname,domain;
            ImageButton btnView;
        public VHolder(@NonNull View itemView) {
                super(itemView);
                cname = itemView.findViewById(R.id.tvCompanyAdapterCompanyName);
                domain = itemView.findViewById(R.id.tvCompanyAdapterCompanyDomain);
                btnView = itemView.findViewById(R.id.btnCompanyAdapterView);

        }
    }

    private void openDialog(String company_id,String creator_id) {

        Dialog dialog = new Dialog(context,R.style.DialogStyle);
        dialog.setContentView(R.layout.dialog_details_company);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.white_all_round);

        btnClose = dialog.findViewById(R.id.btnClose);
        btnEdit = dialog.findViewById(R.id.btnEdit);
        btnDelete = dialog.findViewById(R.id.btnDelete);
        tvCpName= dialog.findViewById(R.id.tvCInfoName);
        tvDomain= dialog.findViewById(R.id.tvCInfoDomain);
        tvAdd= dialog.findViewById(R.id.tvCInfoAddress);
        tvHr1name= dialog.findViewById(R.id.tvCInfoHR1Name);
        tvHr1email= dialog.findViewById(R.id.tvCInfoHR1Email);
        tvHr2name= dialog.findViewById(R.id.tvCInfoHR2Name);
        tvHr2email= dialog.findViewById(R.id.tvCInfoHR2Email);
        tvAbout= dialog.findViewById(R.id.tvCInfoAbout);

        SharedPreferences userPref = context.getSharedPreferences("user",Context.MODE_PRIVATE);
        if(userPref.getString("user_id","user_id").equals(creator_id)){
            btnEdit.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
        }else{
            btnEdit.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
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

                            tvCpName.setText(new JSONObject(jsonArray.getString(0)).getString("COMPANY_NAME"));
                            tvDomain.setText(new JSONObject(jsonArray.getString(0)).getString("WEB_DOMAIN"));
                            tvAdd.setText(new JSONObject(jsonArray.getString(0)).getString("ADDRESS"));
                            tvHr1name.setText(new JSONObject(jsonArray.getString(0)).getString("HR1_NAME"));
                            tvHr1email.setText(new JSONObject(jsonArray.getString(0)).getString("HR1_EMAIL"));
                            tvHr2name.setText(new JSONObject(jsonArray.getString(0)).getString("HR2_NAME"));
                            tvHr2email.setText(new JSONObject(jsonArray.getString(0)).getString("HR2_EMAIL"));
                            tvAbout.setText(new JSONObject(jsonArray.getString(0)).getString("ABOUT"));

                            btnClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });
                            btnEdit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent i = new Intent(context,AddCompanyActivity.class);
                                    i.putExtra("ACTION","UPDATE");
                                    i.putExtra("COMPANY_ID",company_id);
                                    dialog.dismiss();
                                    context.startActivity(i);
                                }
                            });
                            btnDelete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    deleteCompany(company_id,creator_id,"DELETE");
                                }
                            });

                            dialog.show();

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
                param.put("company_id",company_id);
                return param;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(6000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);

    }

    private void deleteCompany(String company_id,String creator_id,String command){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.MANAGE_COMPANY_PROFILE,
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
                params.put("cmp_id",company_id);
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
