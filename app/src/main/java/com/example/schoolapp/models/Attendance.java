package com.example.schoolapp.models;

import java.time.LocalDate;

public class Attendance {
    private Integer id;
    private LocalDate date;
    private Integer classId;

    public Attendance() {

    }

    public Attendance(Integer id, LocalDate date, Integer classId) {
        this.id = id;
        this.date = date;
        this.classId = classId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "id=" + id +
                ", date=" + date +
                ", classId=" + classId +
                '}';
    }
}
