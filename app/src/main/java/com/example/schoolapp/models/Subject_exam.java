package com.example.schoolapp.models;

import java.time.LocalDate;

public class Subject_exam {
    private Integer subjectId;
    private Integer examId;
    private LocalDate date;
    private Integer duration; // minutes
    private Float percentageOfGrade;

    public Subject_exam() {

    }

    public Subject_exam(Integer subjectId, Integer examId, LocalDate date, Integer duration, Float percentageOfGrade) {
        this.subjectId = subjectId;
        this.examId = examId;
        this.date = date;
        this.duration = duration;
        this.percentageOfGrade = percentageOfGrade;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getExamId() {
        return examId;
    }

    public void setExamId(Integer examId) {
        this.examId = examId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Float getPercentageOfGrade() {
        return percentageOfGrade;
    }

    public void setPercentageOfGrade(Float percentageOfGrade) {
        this.percentageOfGrade = percentageOfGrade;
    }

    @Override
    public String toString() {
        return "Subject_exam{" +
                "subjectId=" + subjectId +
                ", examId=" + examId +
                ", date=" + date +
                ", duration=" + duration +
                ", percentageOfGrade=" + percentageOfGrade +
                '}';
    }
}
