package com.example.schoolapp.data_access;


import com.example.schoolapp.models.Student;

public interface IStudentDA {
    void getStudentById(int id, StudentDA.SingleStudentCallback callback);
    void getAllStudents(StudentDA.StudentListCallback callback);
    void addStudent(Student student, StudentDA.BaseCallback callback);
    void getClassStudents(int class_id, StudentDA.StudentListCallback cb);
    void updateStudent(Student student, StudentDA.BaseCallback callback);
    void deleteStudent(int id, StudentDA.BaseCallback callback);
}