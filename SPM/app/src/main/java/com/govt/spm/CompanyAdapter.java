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

import java.util.ArrayList;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.VHolder> {
    Context context;
    ArrayList<String> company_name;

    TextView cname;
    ImageButton btnView,btnClose;

    public CompanyAdapter(Context context,ArrayList<String> company_name){
        this.context = context;
        this.company_name = company_name;
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
        cname.setText(company_name.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return company_name.size();
    }

    public class VHolder extends RecyclerView.ViewHolder {
        public VHolder(@NonNull View itemView) {
            super(itemView);
            cname = itemView.findViewById(R.id.tvCompanyAdapterCompanyName);
            btnView = itemView.findViewById(R.id.btnCompanyAdapterView);

            btnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDialog();
                }
            });
        }
    }

    private void openDialog() {

        Dialog dialog = new Dialog(context,R.style.DialogStyle);
        dialog.setContentView(R.layout.dialog_details_company);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.white_all_round);
        btnClose = dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }
}
