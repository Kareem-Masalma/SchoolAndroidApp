package com.example.schoolapp.models;

public class Student {
    private Integer id;
    private Integer userId;
    private Integer classId;

    public Student() {

    }

    public Student(Integer id, Integer userId, Integer classId) {
        this.id = id;
        this.userId = userId;
        this.classId = classId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", userId=" + userId +
                ", classId=" + classId +
                '}';
    }
}
