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
import com.example.schoolapp.models.Class;

import java.util.List;

public class TeacherClassAdapter extends RecyclerView.Adapter<TeacherClassAdapter.ViewHolder> {

    private final List<Class> classList;
    private final Context context;
    private final OnClassClickListener listener;

    public interface OnClassClickListener {
        void onClassClick(Class selectedClass);
    }

    public TeacherClassAdapter(Context context, List<Class> classList, OnClassClickListener listener) {
        this.context = context;
        this.classList = classList;
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
        Class currentClass = classList.get(position);

        holder.tvClassName.setText(currentClass.getClassName());
        holder.tvClassId.setText("Class ID: " + currentClass.getClassId());
        holder.tvClassManager.setText("Manager ID: " + currentClass.getManager());

        holder.itemView.setOnClickListener(v -> listener.onClassClick(currentClass));
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvClassName, tvClassId, tvClassManager;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClassName = itemView.findViewById(R.id.tvClassName);
            tvClassId = itemView.findViewById(R.id.tvClassId);
            tvClassManager = itemView.findViewById(R.id.tvClassManager);
        }
    }
}
