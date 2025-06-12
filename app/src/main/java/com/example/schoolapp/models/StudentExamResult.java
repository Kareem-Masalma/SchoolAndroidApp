package com.example.schoolapp.models;

public class StudentExamResult {
    private int studentId;
    private String studentName;
    private float mark = 0;
    private Integer exam_id;

    public StudentExamResult(int studentId, String studentName) {
        this.studentId = studentId;
        this.studentName = studentName;
    }

    public StudentExamResult(int studentId, String studentName, float mark) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.mark = mark;
    }

    public StudentExamResult(int studentId, Integer exam_id, float mark) {
        this.studentId = studentId;
        this.exam_id = exam_id;
        this.mark = mark;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public float getMark() {
        return mark;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setMark(float mark) {
        this.mark = mark;
    }

    public Integer getExam_id() {
        return exam_id;
    }

    public void setExam_id(Integer exam_id) {
        this.exam_id = exam_id;
    }

    @Override
    public String toString() {
        return "StudentExamResult{" +
                "studentId=" + studentId +
                ", mark=" + mark +
                ", exam_id=" + exam_id +
                '}';
    }
}
