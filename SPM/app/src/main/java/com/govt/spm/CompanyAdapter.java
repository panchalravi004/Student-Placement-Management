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

import java.util.ArrayList;

public class CompanyAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> company_name;

    TextView cname;
    ImageButton btnView;

    public CompanyAdapter(Context context,ArrayList<String> company_name){
        this.context = context;
        this.company_name = company_name;
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
        view = LayoutInflater.from(context).inflate(R.layout.row_company,viewGroup,false);
        cname = view.findViewById(R.id.tvCompanyName);
        btnView = view.findViewById(R.id.btnView);
        cname.setText(company_name.get(i).toString());

        return view;
    }

    private void openDialog() {
        Dialog dialog = new Dialog(context,R.style.DialogStyle);
        dialog.setContentView(R.layout.dialog_details_jobs_post);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.white_all_round);

        dialog.show();

    }
}
