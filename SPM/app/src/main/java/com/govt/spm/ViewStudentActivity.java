package com.govt.spm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ViewStudentActivity extends AppCompatActivity {

    ListView student_list;
    StudentAdapter sa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_student);
        student_list = (ListView) findViewById(R.id.student_list);

        ArrayList<String> sid = new ArrayList<String>();
        ArrayList<String> sname = new ArrayList<String>();
        ArrayList<String> ssem = new ArrayList<String>();

        sid.add("1234560001");
        sid.add("1234560002");
        sid.add("1234560003");
        sid.add("1234560004");

        sname.add("Ravi Panchal");
        sname.add("Richesh Gaurav");
        sname.add("Priyank Sukhadiya");
        sname.add("Banti Shrivastav");

        ssem.add("MCA SEM 3 2022");
        ssem.add("MCA SEM 3 2022");
        ssem.add("MCA SEM 3 2022");
        ssem.add("MCA SEM 3 2022");

        sa = new StudentAdapter(ViewStudentActivity.this,sid,sname,ssem);
        student_list.setAdapter(sa);
    }

    public void goToDashboard(View view) {
        finish();
    }
}