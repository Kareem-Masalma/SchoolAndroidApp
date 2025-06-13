package com.example.schoolapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolapp.R;
import com.example.schoolapp.models.Teacher;

import java.util.List;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder> {

    private final Context context;
    private final List<Teacher> teacherList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Teacher teacher);
    }

    public TeacherAdapter(Context context, List<Teacher> teacherList, OnItemClickListener listener) {
        this.context = context;
        this.teacherList = teacherList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        return new TeacherViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TeacherViewHolder holder, int position) {
        Teacher teacher = teacherList.get(position);
        String fullName = teacher.getFirstName() + " " + teacher.getLastName();
        holder.tvName.setText(teacher.getUser_id() + ": " + fullName);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(teacher));
    }

    @Override
    public int getItemCount() {
        return teacherList.size();
    }

    public static class TeacherViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;

        public TeacherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
        }
    }
}
