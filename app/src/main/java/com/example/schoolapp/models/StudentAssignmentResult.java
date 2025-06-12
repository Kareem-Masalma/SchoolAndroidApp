package com.example.schoolapp.models;

public class StudentAssignmentResult {
    private Integer student_id;
    private Integer assignment_id;
    private float mark = 0;

    public StudentAssignmentResult(Integer student_id, Integer assignment_id, float mark) {
        this.student_id = student_id;
        this.assignment_id = assignment_id;
        this.mark = mark;
    }

    public Integer getStudent_id() {
        return student_id;
    }

    public void setStudent_id(Integer student_id) {
        this.student_id = student_id;
    }

    public Integer getAssignment_id() {
        return assignment_id;
    }

    public void setAssignment_id(Integer assignment_id) {
        this.assignment_id = assignment_id;
    }

    public float getMark() {
        return mark;
    }

    public void setMark(float mark) {
        this.mark = mark;
    }

    @Override
    public String toString() {
        return "student_assignment_result{" +
                "student_id=" + student_id +
                ", assignment_id=" + assignment_id +
                ", mark=" + mark +
                '}';
    }
}
