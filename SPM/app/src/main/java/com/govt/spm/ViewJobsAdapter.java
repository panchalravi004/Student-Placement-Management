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

public class ViewJobsAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> company_name;
    ArrayList<String> interview_date;
    ArrayList<String> register_end_date;

    TextView cName,iDate,rEDate;
    ImageButton btnClose,btnView;
    public ViewJobsAdapter(Context context,ArrayList<String> company_name,ArrayList<String> interview_date,ArrayList<String> register_end_date){
        this.context = context;
        this.company_name = company_name;
        this.interview_date = interview_date;
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

        view = LayoutInflater.from(context).inflate(R.layout.row_view_jobs,viewGroup,false);

        cName = view.findViewById(R.id.tvCompanyName);
        iDate = view.findViewById(R.id.tvInterviewDate);
        rEDate = view.findViewById(R.id.tvRegisterEndDate);
        btnView = view.findViewById(R.id.btnView);

        cName.setText(company_name.get(i).toString());
        iDate.setText(interview_date.get(i).toString());
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
