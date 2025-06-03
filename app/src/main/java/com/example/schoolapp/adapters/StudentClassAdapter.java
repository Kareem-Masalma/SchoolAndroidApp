package com.example.schoolapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.schoolapp.R;
import com.example.schoolapp.models.Student;
import java.util.List;

public class StudentClassAdapter extends RecyclerView.Adapter<StudentClassAdapter.ViewHolder> {

    private final Context context;
    private final List<Student> students;
    private final OnMessageClickListener listener;

    public interface OnMessageClickListener {
        void onMessageClick(Student student);
    }

    public StudentClassAdapter(Context context, List<Student> students, OnMessageClickListener listener) {
        this.context = context;
        this.students = students;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_student_row, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Student student = students.get(position);
        holder.tvStudentName.setText(student.getFirstName() + " " + student.getLastName());
        holder.btnSendMessage.setOnClickListener(v -> listener.onMessageClick(student));
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName;
        Button btnSendMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            btnSendMessage = itemView.findViewById(R.id.btnSendMessage);
        }
    }
}
