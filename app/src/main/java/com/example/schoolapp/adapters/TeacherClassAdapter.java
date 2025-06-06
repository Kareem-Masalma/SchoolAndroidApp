package com.example.schoolapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolapp.R;
import com.example.schoolapp.models.SchoolClass;

import java.util.List;

public class TeacherClassAdapter extends RecyclerView.Adapter<TeacherClassAdapter.ViewHolder> {

    private final List<SchoolClass> schoolClassList;
    private final Context context;
    private final OnClassClickListener listener;

    public interface OnClassClickListener {
        void onClassClick(SchoolClass selectedSchoolClass);
    }

    public TeacherClassAdapter(Context context, List<SchoolClass> schoolClassList, OnClassClickListener listener) {
        this.context = context;
        this.schoolClassList = schoolClassList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TeacherClassAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_teacher_class, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TeacherClassAdapter.ViewHolder holder, int position) {
        SchoolClass currentSchoolClass = schoolClassList.get(position);

        holder.tvClassName.setText(currentSchoolClass.getClassName());
        holder.tvClassId.setText("Class ID: " + currentSchoolClass.getClassId());
        holder.tvClassManager.setText("Manager ID: " + currentSchoolClass.getClassManagerId());

        holder.itemView.setOnClickListener(v -> listener.onClassClick(currentSchoolClass));
    }

    @Override
    public int getItemCount() {
        return schoolClassList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvClassName, tvClassId, tvClassManager, tvSemester;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClassName = itemView.findViewById(R.id.tvClassName);
            tvClassId = itemView.findViewById(R.id.tvClassId);
            tvClassManager = itemView.findViewById(R.id.tvClassManager);
        }
    }
}
