package com.example.schoolapp.models;

import java.time.LocalDate;

public class Subject_exam {
    private Integer subject_id;
    private Integer exam_id;
    private LocalDate date;
    private Integer duration; // minutes
    private Float percentageOfGrade;

    public Subject_exam() {

    }

    public Subject_exam(Integer subject_id, Integer exam_id, LocalDate date, Integer duration, Float percentageOfGrade) {
        this.subject_id = subject_id;
        this.exam_id = exam_id;
        this.date = date;
        this.duration = duration;
        this.percentageOfGrade = percentageOfGrade;
    }

    public Integer getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(Integer subject_id) {
        this.subject_id = subject_id;
    }

    public Integer getExam_id() {
        return exam_id;
    }

    public void setExam_id(Integer exam_id) {
        this.exam_id = exam_id;
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
                "subjectId=" + subject_id +
                ", examId=" + exam_id +
                ", date=" + date +
                ", duration=" + duration +
                ", percentageOfGrade=" + percentageOfGrade +
                '}';
    }
}
