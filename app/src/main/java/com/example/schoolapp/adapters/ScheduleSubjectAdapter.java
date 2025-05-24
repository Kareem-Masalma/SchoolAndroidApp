package com.example.schoolapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.schoolapp.R;
import com.example.schoolapp.models.ScheduleSubject;
import java.util.List;

public class ScheduleSubjectAdapter extends RecyclerView.Adapter<ScheduleSubjectAdapter.ScheduleViewHolder> {

    private List<ScheduleSubject> scheduleList;

    public ScheduleSubjectAdapter(List<ScheduleSubject> scheduleList) {
        this.scheduleList = scheduleList;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule_subject, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        ScheduleSubject item = scheduleList.get(position);
        holder.tvDay.setText(item.getDay());
        holder.tvStartTime.setText("Start: " + item.getStartTime());
        holder.tvEndTime.setText("End: " + item.getEndTime());
        holder.tvSubjectId.setText("Subject ID: " + item.getSubjectId());
        holder.tvClassId.setText("Class ID: " + item.getClassId());
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    public static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay, tvStartTime, tvEndTime, tvSubjectId, tvClassId;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDay);
            tvStartTime = itemView.findViewById(R.id.tvStartTime);
            tvEndTime = itemView.findViewById(R.id.tvEndTime);
            tvSubjectId = itemView.findViewById(R.id.tvSubjectId);
            tvClassId = itemView.findViewById(R.id.tvGrade);
        }
    }
}
