package com.example.schoolapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.schoolapp.R;
import com.example.schoolapp.models.Assignment;
import java.util.List;
import java.util.Map;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder> {

    public interface OnAssignmentClickListener {
        void onAssignmentClick(Assignment assignment);
    }

    private final List<Assignment> assignments;
    private final Map<Assignment, String> subjectTitleMap;

    private final OnAssignmentClickListener listener;

    public AssignmentAdapter(List<Assignment> assignments, Map<Assignment, String> subjectTitleMap, OnAssignmentClickListener listener) {
        this.assignments = assignments;
        this.subjectTitleMap = subjectTitleMap;
        this.listener = listener;
    }
    


    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_assignment, parent, false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        Assignment assignment = assignments.get(position);
        holder.textTitle.setText(assignment.getTitle());
        holder.textDeadline.setText("Deadline: " + assignment.getEnd_date());
        String subjectTitle = subjectTitleMap != null && subjectTitleMap.containsKey(assignment)
                ? subjectTitleMap.get(assignment)
                : "Unknown";
        holder.textSubject.setText("Subject: " + subjectTitle);
        holder.itemView.setOnClickListener(v -> listener.onAssignmentClick(assignment));
    }

    @Override
    public int getItemCount() {
        return assignments.size();
    }

    static class AssignmentViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textDeadline, textSubject;

        public AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textAssignmentTitle);
            textSubject = itemView.findViewById(R.id.textAssignmentSubject);
            textDeadline = itemView.findViewById(R.id.textAssignmentDeadline);
        }
    }
}
