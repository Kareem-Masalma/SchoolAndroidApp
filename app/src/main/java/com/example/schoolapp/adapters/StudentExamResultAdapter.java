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
import com.example.schoolapp.models.Exam;
import com.example.schoolapp.models.StudentExamResult;

import java.util.List;

public class StudentExamResultAdapter extends RecyclerView.Adapter<StudentExamResultAdapter.ViewHolder> {

    private final Context context;
    private final List<StudentExamResult> resultList;
    private final List<Exam> examList;
    private final OnResultClickListener listener;

    public interface OnResultClickListener {
        void onResultClick(StudentExamResult result);
    }

    public StudentExamResultAdapter(Context context,
                                    List<StudentExamResult> resultList,
                                    List<Exam> examList,
                                    OnResultClickListener listener) {
        this.context = context;
        this.resultList = resultList;
        this.examList = examList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mark, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentExamResult result = resultList.get(position);
        String title = getExamTitleById(result.getExam_id());

        holder.markTitleTV.setText(title);
        Exam exam = findExamById(result.getExam_id());
        if (exam != null) {
            holder.markTV.setText(result.getMark() + " / " + exam.getPercentage());
        } else {
            holder.markTV.setText(" " + result.getMark());
        }


        holder.itemView.setOnClickListener(v -> listener.onResultClick(result));
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    private String getExamTitleById(Integer examId) {
        if (examId == null) return "Unknown Exam";

        for (Exam exam : examList) {
            if (exam.getExamId() == examId) {
                return exam.getTitle();
            }
        }
        return "Unknown Exam";
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView markTitleTV, markTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            markTitleTV = itemView.findViewById(R.id.markTitleTV);
            markTV = itemView.findViewById(R.id.markTV);
        }
    }

    private Exam findExamById(Integer id) {
        for (Exam e : examList) {
            if (e.getExamId()== (id)) {
                return e;
            }
        }
        return null;
    }

}
