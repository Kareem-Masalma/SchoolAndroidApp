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
import com.example.schoolapp.models.Subject;

import java.util.List;

public class SubAdapter extends RecyclerView.Adapter<SubAdapter.SubjectViewHolder> {

    private final Context context;
    private List<Subject> subjectList;
    private final OnSubjectClickListener listener;


    public SubAdapter(Context context, List<Subject> subjectList,  OnSubjectClickListener listener) {
        this.context = context;
        this.subjectList = subjectList;
        this.listener = listener;

    }

    public interface OnSubjectClickListener {
        void onSubjectClick(Subject subject);
    }

    public void updateData(List<Subject> newList) {
        this.subjectList = newList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_course, parent, false);
        return new SubjectViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        Subject subject = subjectList.get(position);
        holder.tvSubjectTitle.setText(subject.getTitle());
        holder.itemView.setOnClickListener(v -> listener.onSubjectClick(subject));
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public static class SubjectViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubjectTitle, tvSubjectId, tvSubjectClass;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubjectTitle = itemView.findViewById(R.id.subjectNameTV);
        }
    }
}