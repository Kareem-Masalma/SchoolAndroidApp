package com.example.schoolapp.models;

import java.time.LocalDate;

public class Attendance {
    private Integer attendance_id;
    private LocalDate date;
    private Integer classId;

    public Attendance() {

    }

    public Attendance(Integer attendance_id, LocalDate date, Integer classId) {
        this.attendance_id = attendance_id;
        this.date = date;
        this.classId = classId;
    }

    public Integer getAttendance_id() {
        return attendance_id;
    }

    public void setAttendance_id(Integer attendance_id) {
        this.attendance_id = attendance_id;
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
                "id=" + attendance_id +
                ", date=" + date +
                ", classId=" + classId +
                '}';
    }
}
