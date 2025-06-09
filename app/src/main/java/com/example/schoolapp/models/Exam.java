package com.example.schoolapp.models;

import java.time.LocalDate;

public class Exam {
    private int examId;
    private String title;
    private int subject;
    private LocalDate date;
    private int duration;
    private int percentage;

    public Exam(String title, int subject, LocalDate date, int duration, int percentage) {
        this.title = title;
        this.subject = subject;
        this.date = date;
        this.duration = duration;
        this.percentage = percentage;
    }


    public Exam(int examId, String title, int subjectId, LocalDate date, int duration, int percentage) {
        this.examId = examId;
        this.title = title;
        this.subject = subjectId;
        this.date = date;
        this.duration = duration;
        this.percentage = percentage;
    }

        public int getExamId() {
        return examId;
    }

    public String getTitle() {
        return title;
    }

    public int getSubject() {
        return subject;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubject(int subject) {
        this.subject = subject;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }
}
