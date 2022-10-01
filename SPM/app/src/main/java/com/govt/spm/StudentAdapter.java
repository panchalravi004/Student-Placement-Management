package com.govt.spm;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.VHolder> {
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

    @NonNull
    @Override
    public StudentAdapter.VHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_student,parent,false);
        return new VHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentAdapter.VHolder holder, int position) {
        tvId.setText(student_id.get(position));
        tvName.setText(student_name.get(position));
        tvSem.setText(stud_sem_year.get(position));

    }

    @Override
    public int getItemCount() {
        return student_id.size();
    }

    public class VHolder extends RecyclerView.ViewHolder {
        public VHolder(@NonNull View itemView) {
            super(itemView);
            tvName=(TextView) itemView.findViewById(R.id.tvStudentAdapterName);
            tvId=(TextView) itemView.findViewById(R.id.tvStudentAdapterRollNo);
            tvSem=(TextView) itemView.findViewById(R.id.tvStudentAdapterCourseSemYear);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDialog();
                }
            });
        }
    }

    private void openDialog() {
        Dialog dialog = new Dialog(context,R.style.DialogStyle);
        dialog.setContentView(R.layout.dialog_details_student);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.white_all_round);
        dialog.show();
    }
}
