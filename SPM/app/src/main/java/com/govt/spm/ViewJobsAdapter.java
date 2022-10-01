package com.govt.spm;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ViewJobsAdapter extends RecyclerView.Adapter<ViewJobsAdapter.VHolder> {
    Context context;
    ArrayList<String> company_name;
    ArrayList<String> college_name;
    ArrayList<String> register_end_date;

    TextView cName,clgName,rEDate;
    ImageButton btnClose,btnView;
    public ViewJobsAdapter(Context context,ArrayList<String> company_name,ArrayList<String> college_name,ArrayList<String> register_end_date){
        this.context = context;
        this.company_name = company_name;
        this.college_name = college_name;
        this.register_end_date = register_end_date;
    }

    @NonNull
    @Override
    public ViewJobsAdapter.VHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_view_jobs,parent,false);
        return new VHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewJobsAdapter.VHolder holder, int position) {
        cName.setText(company_name.get(position).toString());
        clgName.setText(college_name.get(position).toString());
        rEDate.setText("Ends On: "+register_end_date.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return company_name.size();
    }

    public class VHolder extends RecyclerView.ViewHolder {
        public VHolder(@NonNull View itemView) {
            super(itemView);
            cName = itemView.findViewById(R.id.tvVJACompanyName);
            clgName = itemView.findViewById(R.id.tvVJACollegeName);
            rEDate = itemView.findViewById(R.id.tvVJARegisterEndDate);
            btnView = itemView.findViewById(R.id.btnVJAView);
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
        dialog.setContentView(R.layout.dialog_details_jobs_post);
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
