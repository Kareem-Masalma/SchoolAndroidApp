package com.example.schoolapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolapp.R;
import com.example.schoolapp.models.Exam;

import java.util.List;
import java.util.Map;

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.ExamViewHolder> {

    public interface OnExamClickListener {
        void onExamClick(Exam exam);
    }

    private final List<Exam> examList;
    private final Map<Exam, String> subjectTitleMap;
    private final Map<Exam, String> classTitleMap;
    private final OnExamClickListener listener;

    public ExamAdapter(List<Exam> examList, Map<Exam, String> subjectTitleMap,
                       Map<Exam, String> classTitleMap, OnExamClickListener listener) {
        this.examList = examList;
        this.subjectTitleMap = subjectTitleMap;
        this.classTitleMap = classTitleMap;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exam, parent, false);
        return new ExamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamViewHolder holder, int position) {
        Exam exam = examList.get(position);
        holder.textTitle.setText(exam.getTitle());
        holder.textDate.setText("Date: " + exam.getDate().toString());

        String subject = subjectTitleMap.getOrDefault(exam, "Unknown");
        holder.textSubject.setText("Subject: " + subject);

        String classTitle = classTitleMap.getOrDefault(exam, "Unknown");
        holder.textClass.setText("Class: " + classTitle);

        holder.itemView.setOnClickListener(v -> listener.onExamClick(exam));
    }

    @Override
    public int getItemCount() {
        return examList.size();
    }

    public void updateData(List<Exam> updatedList) {
        examList.clear();
        examList.addAll(updatedList);
        notifyDataSetChanged();
    }

    static class ExamViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textDate, textSubject, textClass;

        public ExamViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textExamTitle);
            textDate = itemView.findViewById(R.id.textExamDate);
            textSubject = itemView.findViewById(R.id.textExamSubject);
            textClass = itemView.findViewById(R.id.textExamClass);
        }
    }
}
