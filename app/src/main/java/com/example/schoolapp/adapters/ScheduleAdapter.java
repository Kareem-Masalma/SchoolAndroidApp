package com.example.schoolapp.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolapp.R;
import com.example.schoolapp.models.ScheduleSubject;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private List<ScheduleSubject> scheduleList;

    public ScheduleAdapter(List<ScheduleSubject> scheduleList) {
        this.scheduleList = scheduleList;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<ScheduleSubject> newList) {
        this.scheduleList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule_row, parent, false);
        return new ScheduleViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        ScheduleSubject schedule = scheduleList.get(position);

        holder.tvSubjectTitle.setText(schedule.getSubject());
        holder.tvDayTime.setText(schedule.getDay() + " - " + schedule.getStartTime() + " to " + schedule.getEndTime());
        holder.tvClass.setText("Class: " + schedule.getClassName());
        holder.tvSemesterYear.setText("Semester: " + schedule.getSemester() + " " + schedule.getYear());
    }

    @Override
    public int getItemCount() {
        return scheduleList != null ? scheduleList.size() : 0;
    }

    public static class ScheduleViewHolder extends RecyclerView.ViewHolder {

        TextView tvSubjectTitle, tvDayTime, tvClass, tvSemesterYear;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubjectTitle = itemView.findViewById(R.id.tvSubjectTitle);
            tvDayTime = itemView.findViewById(R.id.tvDayTime);
            tvClass = itemView.findViewById(R.id.tvClass);
            tvSemesterYear = itemView.findViewById(R.id.tvSemesterYear);
        }
    }
}
