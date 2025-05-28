package com.example.schoolapp.models;

import androidx.annotation.NonNull;

import java.time.LocalDate;

public class Student extends User {
    private Integer class_id;

    public Student() {

    }

    public Student(Integer user_id, String firstName, String lastName, LocalDate birthDate, String address, String phone, Role role, Integer class_id) {
        super(user_id, firstName, lastName, birthDate, address, phone, role);
        this.class_id = class_id;
    }

    public Student(Integer user_id, String firstName, String lastName, LocalDate birthDate, String address, String phone, Role role, Integer class_id, String password) {
        super(user_id, firstName, lastName, birthDate, address, phone, role, password);
        this.class_id = class_id;
    }

    public Integer getClass_id() {
        return class_id;
    }

    public void setClass_id(Integer class_id) {
        this.class_id = class_id;
    }

    @NonNull
    @Override
    public String toString() {
        return "Student{" +
                "user_id=" + getUser_id() +
                ", classId=" + class_id +
                '}';
    }
}
