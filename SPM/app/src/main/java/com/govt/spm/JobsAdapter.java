package com.govt.spm;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class JobsAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> company_name;
    ArrayList<String> college_name;
    ArrayList<String> register_end_date;

    TextView cName,clgName,rEDate;
    ImageButton btnView,btnEdit,btnDelete;
    ImageButton btnClose;

    public JobsAdapter(Context context,ArrayList<String> company_name,ArrayList<String> college_name,ArrayList<String> register_end_date){
        this.context = context;
        this.company_name = company_name;
        this.college_name = college_name;
        this.register_end_date = register_end_date;
    }
    @Override
    public int getCount() {
        return company_name.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.row_jobs,viewGroup,false);

        cName = view.findViewById(R.id.tvJobsAdapterCompanyName);
        clgName = view.findViewById(R.id.tvJobsAdapterCollegeName);
        rEDate = view.findViewById(R.id.tvJobsAdapterRegisterEndDate);
        btnView = view.findViewById(R.id.btnJobsAdapterView);
        btnEdit = view.findViewById(R.id.btnJobsAdapterEdit);
        btnDelete = view.findViewById(R.id.btnJobsAdapterDelete);


        cName.setText(company_name.get(i).toString());
        clgName.setText(college_name.get(i).toString());
        rEDate.setText("Ends On: "+register_end_date.get(i).toString());

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        return view;
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
