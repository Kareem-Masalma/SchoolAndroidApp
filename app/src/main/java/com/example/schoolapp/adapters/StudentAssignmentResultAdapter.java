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
import com.example.schoolapp.models.Assignment;
import com.example.schoolapp.models.StudentAssignmentResult;

import java.util.List;

public class StudentAssignmentResultAdapter extends RecyclerView.Adapter<StudentAssignmentResultAdapter.ViewHolder> {

    private final Context context;
    private final List<StudentAssignmentResult> resultList;
    private final List<Assignment> assignmentList;
    private final OnResultClickListener listener;

    public interface OnResultClickListener {
        void onResultClick(StudentAssignmentResult result);
    }

    public StudentAssignmentResultAdapter(Context context,
                                          List<StudentAssignmentResult> resultList,
                                          List<Assignment> assignmentList,
                                          OnResultClickListener listener) {
        this.context = context;
        this.resultList = resultList;
        this.assignmentList = assignmentList;
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
        StudentAssignmentResult result = resultList.get(position);
        String title = getAssignmentTitleById(result.getAssignment_id());

        holder.markTitleTV.setText(title);

        Assignment assignment = findAssignmentById(result.getAssignment_id());
        if (assignment != null) {
            holder.markTV.setText(result.getMark() + " / " + assignment.getPercentage_of_grade());
        } else {
            holder.markTV.setText(" " + result.getMark());
        }


        holder.itemView.setOnClickListener(v -> listener.onResultClick(result));
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    private String getAssignmentTitleById(int assignmentId) {
        for (Assignment assignment : assignmentList) {
            if (assignment.getAssignment_id().equals(assignmentId)) {
                return assignment.getTitle();
            }
        }
        return "Unknown Assignment";
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView markTitleTV, markTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            markTitleTV = itemView.findViewById(R.id.markTitleTV);
            markTV = itemView.findViewById(R.id.markTV);
        }
    }

    private Assignment findAssignmentById(int id) {
        for (Assignment a : assignmentList) {
            if (a.getAssignment_id().equals(id)) {
                return a;
            }
        }
        return null;
    }

}
