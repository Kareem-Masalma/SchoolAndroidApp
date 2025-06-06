package com.example.schoolapp.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolapp.R;
import com.example.schoolapp.models.StudentExamResult;

import java.util.List;

public class StudentMarksAdapter extends RecyclerView.Adapter<StudentMarksAdapter.ViewHolder> {

    private final List<StudentExamResult> studentList;

    public StudentMarksAdapter(List<StudentExamResult> studentList) {
        this.studentList = studentList;
    }

    public List<StudentExamResult> getStudentMarks() {
        return studentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_student_mark, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentExamResult student = studentList.get(position);
        holder.tvStudentName.setText(student.getStudentName());

        holder.etMark.setText(String.valueOf(student.getMark()));
        holder.etMark.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    float mark = Float.parseFloat(s.toString());
                    if (mark >= 0 && mark <= 100) {
                        student.setMark(mark);
                    } else {
                        holder.etMark.setError("Mark must be 0-100");
                    }
                } catch (NumberFormatException e) {
                    student.setMark(0);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName;
        EditText etMark;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            etMark = itemView.findViewById(R.id.etMark);
        }
    }
}
