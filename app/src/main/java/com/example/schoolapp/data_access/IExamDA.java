package com.example.schoolapp.data_access;

import com.example.schoolapp.models.Exam;
import com.example.schoolapp.models.StudentExamResult;
import org.json.JSONObject;
import java.util.List;

public interface IExamDA {

    void getAllExams(int studentId, ExamCallback callback);

    void sendExam(Exam exam, List<StudentExamResult> results, ExamCallback callback);

    void findExamById(int examId, ExamCallback callback);

    void updateExam(Exam exam, ExamCallback callback);

    void deleteExam(int examId, ExamCallback callback);

    void publishExamResults(int exam_id, List<StudentExamResult> results, PublishCallback callback);


    interface ExamCallback {
        void onSuccess(List<JSONObject> data);
        void onError(String error);
    }

    interface PublishCallback {
        void onSuccess(String message);
        void onError(String error);
    }
}