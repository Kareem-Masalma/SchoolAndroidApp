package com.example.schoolapp.models;

import java.time.LocalDate;

public class Subject_assignment {
    private Integer subject_id;
    private Integer assignment_id;
    private LocalDate startDate;
    private LocalDate endDate;
    private Float percentageOfGrade;

    public Subject_assignment() {

    }

    public Subject_assignment(Integer subject_id, Integer assignment_id, LocalDate startDate, LocalDate endDate, Float percentageOfGrade) {
        this.subject_id = subject_id;
        this.assignment_id = assignment_id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.percentageOfGrade = percentageOfGrade;
    }

    public Integer getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(Integer subject_id) {
        this.subject_id = subject_id;
    }

    public Integer getAssignment_id() {
        return assignment_id;
    }

    public void setAssignment_id(Integer assignment_id) {
        this.assignment_id = assignment_id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Float getPercentageOfGrade() {
        return percentageOfGrade;
    }

    public void setPercentageOfGrade(Float percentageOfGrade) {
        this.percentageOfGrade = percentageOfGrade;
    }

    @Override
    public String toString() {
        return "Subject_assignment{" +
                "subjectId=" + subject_id +
                ", assignmentId=" + assignment_id +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", percentageOfGrade=" + percentageOfGrade +
                '}';
    }
}
