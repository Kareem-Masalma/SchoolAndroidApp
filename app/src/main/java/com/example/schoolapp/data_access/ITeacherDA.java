package com.example.schoolapp.data_access;

import com.example.schoolapp.models.Teacher;


public interface ITeacherDA {
    void findTeacherById(int id, TeacherDA.SingleTeacherCallback callback);
    void getAllTeachers(TeacherDA.TeacherListCallback callback);
    void addTeacher(Teacher teacher);
    void updateTeacher(Teacher teacher);
    void deleteTeacher(int id, TeacherDA.BaseCallback callback);
}
