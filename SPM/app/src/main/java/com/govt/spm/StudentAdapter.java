package com.govt.spm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class StudentAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> student_id;
    ArrayList<String> student_name;
    ArrayList<String> stud_sem_year;

    TextView tvName,tvId,tvSem;

    public StudentAdapter(Context context,ArrayList<String> student_id,ArrayList<String> student_name,ArrayList<String> stud_sem_year){
        this.context = context;
        this.student_id = student_id;
        this.student_name = student_name;
        this.stud_sem_year = stud_sem_year;
    }

    @Override
    public int getCount() {
        return student_id.size();
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
        view = LayoutInflater.from(context).inflate(R.layout.row_student,viewGroup,false);

        tvName = view.findViewById(R.id.tvStudentName);
        tvId = view.findViewById(R.id.tvStudentId);
        tvSem = view.findViewById(R.id.tvSemester);

        tvId.setText(student_id.get(i).toString());
        tvName.setText(student_name.get(i).toString());
        tvSem.setText(stud_sem_year.get(i).toString());

        return view;
    }
}
