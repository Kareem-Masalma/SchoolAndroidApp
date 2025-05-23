package com.example.schoolapp.data_access;


import com.example.schoolapp.models.Student;

public interface IStudentDA {
    void getStudentById(int id, StudentDA.SingleStudentCallback callback);
    void getAllStudents(StudentDA.StudentListCallback callback);
    void addStudent(Student student);
    void updateStudent(Student student);
    void deleteStudent(int id);
}
