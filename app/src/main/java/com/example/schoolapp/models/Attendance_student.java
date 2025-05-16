package com.example.schoolapp.models;

public class Attendance_student {
    private Integer attendanceId;
    private Integer studentId;
    private Boolean attended;
    private String excuse;

    public Attendance_student() {

    }

    public Attendance_student(Integer attendanceId, Integer studentId, Boolean attended, String excuse) {
        this.attendanceId = attendanceId;
        this.studentId = studentId;
        this.attended = attended;
        this.excuse = excuse;
    }

    public Integer getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Integer attendanceId) {
        this.attendanceId = attendanceId;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
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
                "attendanceId=" + attendanceId +
                ", studentId=" + studentId +
                ", attended=" + attended +
                ", excuse='" + excuse + '\'' +
                '}';
    }
}
