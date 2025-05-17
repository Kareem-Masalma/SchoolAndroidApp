package com.example.schoolapp.models;

public class Attendance_student {
    private Integer attendance_id;
    private Integer student_id;
    private Boolean attended;
    private String excuse;

    public Attendance_student() {

    }

    public Attendance_student(Integer attendance_id, Integer student_id, Boolean attended, String excuse) {
        this.attendance_id = attendance_id;
        this.student_id = student_id;
        this.attended = attended;
        this.excuse = excuse;
    }

    public Integer getAttendance_id() {
        return attendance_id;
    }

    public void setAttendance_id(Integer attendance_id) {
        this.attendance_id = attendance_id;
    }

    public Integer getStudent_id() {
        return student_id;
    }

    public void setStudent_id(Integer student_id) {
        this.student_id = student_id;
    }

    public Boolean getAttended() {
        return attended;
    }

    public void setAttended(Boolean attended) {
        this.attended = attended;
    }

    public String getExcuse() {
        return excuse;
    }

    public void setExcuse(String excuse) {
        this.excuse = excuse;
    }

    @Override
    public String toString() {
        return "Attendance_student{" +
                "attendanceId=" + attendance_id +
                ", studentId=" + student_id +
                ", attended=" + attended +
                ", excuse='" + excuse + '\'' +
                '}';
    }
}
