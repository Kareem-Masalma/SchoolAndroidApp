package com.example.schoolapp.models;

import androidx.annotation.NonNull;

public class Subject {
    private Integer subjectId;
    private String title;
    private Integer classId;
    private String classTitle;
    private Integer totalPercentage;

    public Subject() {

    }

    public Subject(Integer subjectId, String title, Integer classId, String classTitle) {
        this.subjectId = subjectId;
        this.title = title;
        this.classId = classId;
        this.classTitle = classTitle;
    }

    public Subject(Integer subjectId, String title, Integer classId, String classTitle, Integer totalPercentage) {
        this.subjectId = subjectId;
        this.title = title;
        this.classId = classId;
        this.classTitle = classTitle;
        this.totalPercentage = totalPercentage;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public String getClassTitle() {
        return classTitle;
    }

    public void setClassTitle(String classTitle) {
        this.classTitle = classTitle;
    }

    public Integer getTotalPercentage() {
        return totalPercentage;
    }

    public void setTotalPercentage(Integer totalPercentage) {
        this.totalPercentage = totalPercentage;
    }

    @NonNull
    @Override
    public String toString() {
        return title;
    }
}
