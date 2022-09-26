package com.govt.spm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class UpcomingCompanyAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> company_name;
    ArrayList<String> interview_date;
    ArrayList<String> register_end_date;

    TextView cName,iDate,rEDate;

    public UpcomingCompanyAdapter(Context context,ArrayList<String> company_name,ArrayList<String> interview_date,ArrayList<String> register_end_date){
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
        view = LayoutInflater.from(context).inflate(R.layout.row_upcoming_company,viewGroup,false);

        cName = view.findViewById(R.id.tvCompanyName);
        iDate = view.findViewById(R.id.tvInterviewDate);
        rEDate = view.findViewById(R.id.tvRegisterEndDate);

        cName.setText(company_name.get(i).toString());
        iDate.setText(interview_date.get(i).toString());
        rEDate.setText("Registration Ends On: "+register_end_date.get(i).toString());

        return view;
    }
}
