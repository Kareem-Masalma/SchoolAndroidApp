package com.example.schoolapp.data_access;

import com.example.schoolapp.models.Subject;

public interface ISubjectDA {
    void findSubjectById(int id, SubjectDA.SingleSubjectCallback callback);
    void getAllSubjects(SubjectDA.SubjectListCallback callback);
    void addSubject(Subject subject, SubjectDA.BaseCallback callback);
    void updateSubject(Subject subject, SubjectDA.BaseCallback callback);
    void deleteSubject(int id, SubjectDA.BaseCallback callback);
}
