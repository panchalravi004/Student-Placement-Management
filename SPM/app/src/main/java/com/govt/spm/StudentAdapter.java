package com.govt.spm;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.VHolder> {
    Context context;
    JSONArray student;
    TextView tvName,tvEnrollNo,tvEmail,tvMobile,tvAdd,tvPrimarySkill,tvSecondarySkill,tvTertiarySkill,tvDept,tvAcadamicYear,tvSSCPer,tvSSCYear,tvHSCPer,tvHSCYear,tvUGPer,tvUGYear,tvPGPer,tvPGYear,tvHSCStream,tvUGStream,tvPGStream,tvCurrentSem;

    public StudentAdapter(Context context,JSONArray student){
        this.context = context;
        this.student = student;
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
        try {
            JSONObject jo = new JSONObject(student.getString(position));
            holder.tvId.setText(jo.getString("stud_id"));
            holder.tvName.setText(jo.getString("stud_name"));
            holder.tvSem.setText(jo.getString("academic_session")+" SEM "+jo.getString("sem"));
            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDialog(jo);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return student.length();
    }

    public class VHolder extends RecyclerView.ViewHolder {
        TextView tvName,tvId,tvSem;
        View card;
        public VHolder(@NonNull View itemView) {
            super(itemView);
            tvName=(TextView) itemView.findViewById(R.id.tvStudentAdapterName);
            tvId=(TextView) itemView.findViewById(R.id.tvStudentAdapterRollNo);
            tvSem=(TextView) itemView.findViewById(R.id.tvStudentAdapterCourseSemYear);
            card = itemView;

        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    private void openDialog(JSONObject info) {
        Dialog dialog = new Dialog(context,R.style.DialogStyle);
        dialog.setContentView(R.layout.dialog_details_student);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.white_all_round);
        tvName = dialog.findViewById(R.id.tvStudentInfoName);
        tvEnrollNo = dialog.findViewById(R.id.tvStudentInfoEnrollNo);
        tvEmail = dialog.findViewById(R.id.tvStudentInfoEmail);
        tvMobile = dialog.findViewById(R.id.tvStudentInfoMobileNo);
        tvAdd = dialog.findViewById(R.id.tvStudentInfoAdd);
        tvPrimarySkill = dialog.findViewById(R.id.tvStudentInfoPrimarySkill);
        tvSecondarySkill = dialog.findViewById(R.id.tvStudentInfoSecondarySkill);
        tvTertiarySkill = dialog.findViewById(R.id.tvStudentInfoTertiarySkill);
        tvDept = dialog.findViewById(R.id.tvStudentInfoDept);
        tvAcadamicYear = dialog.findViewById(R.id.tvStudentInfoAcadamicYear);
        tvSSCPer = dialog.findViewById(R.id.tvStudentInfoSSCPer);
        tvSSCYear = dialog.findViewById(R.id.tvStudentInfoSSCYear);
        tvHSCPer = dialog.findViewById(R.id.tvStudentInfoHSCPer);
        tvHSCYear = dialog.findViewById(R.id.tvStudentInfoHSCYear);
        tvUGPer = dialog.findViewById(R.id.tvStudentInfoUGPer);
        tvUGYear = dialog.findViewById(R.id.tvStudentInfoUGYear);
        tvPGPer = dialog.findViewById(R.id.tvStudentInfoPGPer);
        tvPGYear = dialog.findViewById(R.id.tvStudentInfoPGYear);
        tvHSCStream = dialog.findViewById(R.id.tvStudentInfoHSCStream);
        tvUGStream = dialog.findViewById(R.id.tvStudentInfoUGStream);
        tvPGStream = dialog.findViewById(R.id.tvStudentInfoPGStream);
        tvCurrentSem = dialog.findViewById(R.id.tvStudentInfoCurrentSem);

        try {
            tvName.setText(info.getString("stud_name"));
            tvEnrollNo.setText(info.getString("stud_id"));
            tvMobile.setText(info.getString("stud_mob"));
            tvAdd.setText(info.getString("stud_address"));
            tvPrimarySkill.setText(info.getString("primary_skill"));
            tvSecondarySkill.setText(info.getString("secondary_skill"));
            tvTertiarySkill.setText(info.getString("tertiary_skill"));
            tvDept.setText(info.getString("dept_id"));
            tvAcadamicYear.setText(info.getString("academic_session"));
            tvSSCPer.setText(info.getString("ssc_score"));
            tvSSCYear.setText(info.getString("ssc_pass_yr"));
            tvHSCPer.setText(info.getString("hsc_score"));
            tvHSCYear.setText(info.getString("hsc_pass_yr"));
            tvUGPer.setText(info.getString("ug_score"));
            tvUGYear.setText(info.getString("ug_pass_yr"));
            tvPGPer.setText(info.getString("pg_score"));
            tvPGYear.setText(info.getString("pg_pass_yr"));
            tvHSCStream.setText(info.getString("hsc_stream"));
            tvUGStream.setText(info.getString("ug_stram"));
            tvPGStream.setText(info.getString("pg_stream"));
            tvCurrentSem.setText(info.getString("sem"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        dialog.show();
    }
}
