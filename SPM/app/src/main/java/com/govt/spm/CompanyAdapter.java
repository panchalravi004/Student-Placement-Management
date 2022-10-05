package com.govt.spm;

import android.app.Dialog;
import android.content.Context;
import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.VHolder> {
    Context context;
//    ArrayList<HashMap<String,String>> company;
    JSONArray company;
    ImageButton btnClose;
    TextView tvCpName,tvDomain,tvAdd,tvHr1name,tvHr1email,tvHr2name,tvHr2email,tvAbout;

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
                        openDialog(new JSONObject(company.getString(holder.getAdapterPosition())));
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

    private void openDialog(JSONObject info) {

        Dialog dialog = new Dialog(context,R.style.DialogStyle);
        dialog.setContentView(R.layout.dialog_details_company);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.white_all_round);
        
        btnClose = dialog.findViewById(R.id.btnClose);
        tvCpName= dialog.findViewById(R.id.tvCInfoName);
        tvDomain= dialog.findViewById(R.id.tvCInfoDomain);
        tvAdd= dialog.findViewById(R.id.tvCInfoAddress);
        tvHr1name= dialog.findViewById(R.id.tvCInfoHR1Name);
        tvHr1email= dialog.findViewById(R.id.tvCInfoHR1Email);
        tvHr2name= dialog.findViewById(R.id.tvCInfoHR2Name);
        tvHr2email= dialog.findViewById(R.id.tvCInfoHR2Email);
        tvAbout= dialog.findViewById(R.id.tvCInfoAbout);
        try {
            String company_id = info.getString("company_id");
            tvCpName.setText(info.getString("company_name"));
            tvDomain.setText(info.getString("web_domain"));
            tvAdd.setText(info.getString("address"));
            tvHr1name.setText(info.getString("hr1_name"));
            tvHr1email.setText(info.getString("hr1_email"));
            tvHr2name.setText(info.getString("hr2_name"));
            tvHr2email.setText(info.getString("hr2_email"));
            tvAbout.setText(info.getString("about"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
