package com.govt.spm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

public class ManageCompanyActivity extends AppCompatActivity {
    EditText etSearch;
    ImageButton btnSearch;
    Spinner spFilterOne,spFilterTwo;

    ListView company_list;
    CompanyAdapter ca;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_company);

        btnSearch = (ImageButton) findViewById(R.id.btnManageCompanySearch);
        etSearch = (EditText) findViewById(R.id.etManageCompanySearch);
        spFilterOne = (Spinner) findViewById(R.id.spManageCompanyFilterOne);
        spFilterTwo = (Spinner) findViewById(R.id.spManageCompanyFilterTwo);

        company_list = (ListView) findViewById(R.id.list_manage_company_company);

        ArrayList<String> cname = new ArrayList<String>();
        cname.add("Info it Solution One");
        cname.add("Info it Solution Two");

        ca = new CompanyAdapter(ManageCompanyActivity.this,cname);
        company_list.setAdapter(ca);

    }

    public void goToDashboard(View view) {
        finish();
    }

    public void goToAddCompany(View view) {
        startActivity(new Intent(ManageCompanyActivity.this,AddCompanyActivity.class));
    }
}