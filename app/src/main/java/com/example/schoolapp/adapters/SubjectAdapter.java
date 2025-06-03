package com.example.schoolapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolapp.models.Subject;

import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {

    public interface OnSubjectClickListener {
        void onSubjectClick(Subject subject);
    }

    private final List<Subject> subjectList;
    private final SubjectAdapter.OnSubjectClickListener listener;

    public SubjectAdapter(List<Subject> subjectList, SubjectAdapter.OnSubjectClickListener listener) {
        this.subjectList = subjectList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectAdapter.SubjectViewHolder holder, int position) {
        Subject subject = subjectList.get(position);
        holder.nameText.setText(subject.getTitle());

        holder.itemView.setOnClickListener(v -> {
            listener.onSubjectClick(subject);
        });
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    static class SubjectViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(android.R.id.text1);
        }
    }
}
