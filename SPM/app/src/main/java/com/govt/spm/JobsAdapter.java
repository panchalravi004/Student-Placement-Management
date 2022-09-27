package com.govt.spm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class JobsAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> company_name;
    ArrayList<String> interview_date;
    ArrayList<String> register_end_date;

    TextView cName,iDate,rEDate;

    public JobsAdapter(Context context,ArrayList<String> company_name,ArrayList<String> interview_date,ArrayList<String> register_end_date){
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
        view = LayoutInflater.from(context).inflate(R.layout.row_jobs,viewGroup,false);

        cName = view.findViewById(R.id.tvStudentId);
        iDate = view.findViewById(R.id.tvSemester);
        rEDate = view.findViewById(R.id.tvStudentName);

        cName.setText(company_name.get(i).toString());
        iDate.setText(interview_date.get(i).toString());
        rEDate.setText("Ends On: "+register_end_date.get(i).toString());

        return view;
    }
}
