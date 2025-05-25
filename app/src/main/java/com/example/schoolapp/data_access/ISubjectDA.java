package com.example.schoolapp.data_access;

import com.example.schoolapp.models.Subject;

public interface ISubjectDA {
    void findSubjectById(int id, SubjectDA.SingleSubjectCallback callback);
    void getAllSubjects(SubjectDA.SubjectListCallback callback);
    void addSubject(Subject subject);
    void updateSubject(Subject subject);
    void deleteSubject(int id);
}
