package com.example.schoolapp.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolapp.R;
import com.example.schoolapp.TeacherSendMessage1;
import com.example.schoolapp.TeacherSendMessage2;
import com.example.schoolapp.models.Student;
import com.google.gson.Gson;

import java.util.List;

public class TeacherSendMessageAdapter extends RecyclerView.Adapter<TeacherSendMessageAdapter.StudentViewHolder> {

    private List<Student> studentList;

    public TeacherSendMessageAdapter(List<Student> studentList) {
        this.studentList = studentList;
    }


    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_send_message_row, parent, false);
        return new TeacherSendMessageAdapter.StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.tvStudentName2.setText(student.getFirstName() + " " + student.getLastName());
        holder.tvStudentId2.setText(String.valueOf(student.getUser_id()));

        holder.btnSelect2.setOnClickListener(e -> {
            // open the TeacherSendMessage2 activity
            Intent intent = new Intent(holder.itemView.getContext(), TeacherSendMessage2.class);
            Gson gson = new Gson();
            String json = gson.toJson(student);
            intent.putExtra("student", json);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        public TextView tvStudentId2;
        TextView tvStudentName2;
        public Button btnSelect2;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentId2 = itemView.findViewById(R.id.tvStudentId2);
            tvStudentName2 = itemView.findViewById(R.id.tvStudentName2);
            btnSelect2 = itemView.findViewById(R.id.btnSelect2);
        }
    }
}
