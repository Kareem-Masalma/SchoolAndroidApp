package com.example.schoolapp.models;

import java.time.LocalDate;

public class Subject_assignment {
    private Integer subjectId;
    private Integer assignmentId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Float percentageOfGrade;

    public Subject_assignment() {

    }

    public Subject_assignment(Integer subjectId, Integer assignmentId, LocalDate startDate, LocalDate endDate, Float percentageOfGrade) {
        this.subjectId = subjectId;
        this.assignmentId = assignmentId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.percentageOfGrade = percentageOfGrade;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Integer assignmentId) {
        this.assignmentId = assignmentId;
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
                "subjectId=" + subjectId +
                ", assignmentId=" + assignmentId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", percentageOfGrade=" + percentageOfGrade +
                '}';
    }
}
