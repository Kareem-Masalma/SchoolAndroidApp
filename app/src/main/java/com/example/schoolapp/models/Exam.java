package com.example.schoolapp.models;

import java.time.LocalDate;

public class Exam {
    private int examId;
    private String title;
    private String subject;
    private String semester;
    private int year;
    private LocalDate date;
    private int classId;

    public Exam(String title, String subject, String semester, int year, LocalDate date, int classId) {
        this.title = title;
        this.subject = subject;
        this.semester = semester;
        this.year = year;
        this.date = date;
        this.classId = classId;
    }

    public Exam(int examId, String title, String subject, String semester, int year, LocalDate date, int classId) {
        this(title, subject, semester, year, date, classId);
        this.examId = examId;
    }

    public int getExamId() {
        return examId;
    }

    public String getTitle() {
        return title;
    }

    public String getSubject() {
        return subject;
    }

    public String getSemester() {
        return semester;
    }

    public int getYear() {
        return year;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getClassId() {
        return classId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }
}
