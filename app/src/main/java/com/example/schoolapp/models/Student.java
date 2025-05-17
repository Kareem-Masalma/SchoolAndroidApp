package com.example.schoolapp.models;

import java.time.LocalDate;

public class Student extends User {
    private Integer student_id;
    private Integer class_id;

    public Student() {

    }

    public Student(Integer student_id, String firstName, String lastName, LocalDate birthDate, String address, String phone, Role role, Integer user_id, Integer class_id) {
        super(user_id, firstName, lastName, birthDate, address, phone, role);
        this.student_id = student_id;
        this.class_id = class_id;
    }

    public Integer getStudent_id() {
        return student_id;
    }

    public void setStudent_id(Integer student_id) {
        this.student_id = student_id;
    }


    public Integer getClass_id() {
        return class_id;
    }

    public void setClass_id(Integer class_id) {
        this.class_id = class_id;
    }

    @Override
    public String toString() {
        return "Student{" +
                "student_id=" + student_id +
                ", user_id=" + super.getUser_id() +
                ", classId=" + class_id +
                '}';
    }
}
