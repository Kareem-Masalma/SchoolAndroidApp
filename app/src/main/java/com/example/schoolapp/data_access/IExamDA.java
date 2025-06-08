package com.example.schoolapp.data_access;

import com.example.schoolapp.models.Exam;
import com.example.schoolapp.models.StudentExamResult;
import org.json.JSONObject;
import java.util.List;

public interface IExamDA {

    void getAllExams(int studentId, ExamDA.ExamCallback callback);

    void sendExam(Exam exam, List<StudentExamResult> results, ExamDA.ExamCallback callback);

    void findExamById(int examId, ExamDA.ExamCallback callback);

    void updateExam(Exam exam, ExamDA.ExamCallback callback);

    void deleteExam(int examId, ExamDA.ExamCallback callback);

    void publishExamResults(Exam exam, List<StudentExamResult> results, ExamCallback callback);

    interface ExamCallback {
        void onSuccess(List<JSONObject> data);
        void onError(String error);
    }

}
