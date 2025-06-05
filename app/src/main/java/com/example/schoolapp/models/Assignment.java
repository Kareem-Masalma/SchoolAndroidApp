package com.example.schoolapp.models;

import java.time.LocalDate;

public class Assignment {
    private Integer assignment_id;
    private Integer subject_id;
    private String title;
    private String details;

    private LocalDate start_date;
    private LocalDate end_date;
    private float percentage_of_grade;

    public Assignment() {}

    public Assignment(Integer assignment_id, Integer subject_id, String title, LocalDate start_date, LocalDate end_date, float percentage_of_grade) {
        this.assignment_id = assignment_id;
        this.subject_id = subject_id;
        this.title = title;
        this.start_date = start_date;
        this.end_date = end_date;
        this.percentage_of_grade = percentage_of_grade;
    }

    public Assignment(Integer assignment_id, Integer subject_id, String title, String details, LocalDate start_date, LocalDate end_date, float percentage_of_grade) {
        this.assignment_id = assignment_id;
        this.subject_id = subject_id;
        this.title = title;
        this.details = details;
        this.start_date = start_date;
        this.end_date = end_date;
        this.percentage_of_grade = percentage_of_grade;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Integer getAssignment_id() {
        return assignment_id;
    }

    public void setAssignment_id(Integer assignment_id) {
        this.assignment_id = assignment_id;
    }

    public Integer getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(Integer subject_id) {
        this.subject_id = subject_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getStart_date() {
        return start_date;
    }

    public void setStart_date(LocalDate start_date) {
        this.start_date = start_date;
    }

    public LocalDate getEnd_date() {
        return end_date;
    }

    public void setEnd_date(LocalDate end_date) {
        this.end_date = end_date;
    }

    public float getPercentage_of_grade() {
        return percentage_of_grade;
    }

    public void setPercentage_of_grade(float percentage_of_grade) {
        this.percentage_of_grade = percentage_of_grade;
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "assignment_id=" + assignment_id +
                ", subject_id=" + subject_id +
                ", title='" + title + '\'' +
                ", start_date=" + start_date +
                ", end_date=" + end_date +
                ", percentage_of_grade=" + percentage_of_grade +
                '}';
    }
}
