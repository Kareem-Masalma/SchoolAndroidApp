package com.example.schoolapp.data_access;

import com.example.schoolapp.models.Exam;
import com.example.schoolapp.models.StudentExamResult;

import java.util.List;
import java.util.Map;

public interface IExamDA {

    void getAllExams(int studentId, ExamCallback callback);
    void addExam(Exam exam, CallBack callBack);
    void sendExam(Exam exam, List<StudentExamResult> results, ExamCallback callback);

    void findExamById(int examId, ExamCallback callback);

    void updateExam(Exam exam, ExamCallback callback);

    void deleteExam(int examId, ExamCallback callback);

    void publishExamResults(int exam_id, List<StudentExamResult> results, PublishCallback callback);

    interface CallBack {
        void onSuccess(String message);

        void onError(String message);
    }

    interface ExamCallback {
        void onSuccess(List<Exam> data);

        void onError(String error);
    }

    // ✅ Place the method here
    interface ExamListWithTitleCallback extends ExamCallback {
        void onSuccessWithTitles(List<Exam> exams, Map<Exam, String> subjectTitleMap, Map<Exam, String> classTitleMap);
    }

    interface PublishCallback {
        void onSuccess(String message);

        void onError(String error);
    }
}