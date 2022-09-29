package com.govt.spm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

public class ManageCompanyActivity extends AppCompatActivity {
    ListView company_list;
    CompanyAdapter ca;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_company);
        company_list = (ListView) findViewById(R.id.company_list);

        ArrayList<String> cname = new ArrayList<String>();
        cname.add("Info it Solution One");
        cname.add("Info it Solution Two");

        ca = new CompanyAdapter(ManageCompanyActivity.this,cname);
        company_list.setAdapter(ca);

    }

    public void goToDashboard(View view) {
        finish();
    }
}