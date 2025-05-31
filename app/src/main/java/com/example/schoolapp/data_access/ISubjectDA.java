package com.example.schoolapp.data_access;

import com.example.schoolapp.models.Subject;


public interface ISubjectDA {

    void getAllSubjects(SubjectDA.SubjectListCallback cb);

    void getSubjectById(int id, SubjectDA.SingleSubjectCallback cb);

    void addSubject(Subject s, SubjectDA.BaseCallback cb);

    void updateSubject(Subject s, SubjectDA.BaseCallback cb);

    void deleteSubject(int id, SubjectDA.BaseCallback cb);

}
