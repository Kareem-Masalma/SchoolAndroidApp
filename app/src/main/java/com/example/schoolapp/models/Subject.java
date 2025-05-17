package com.example.schoolapp.models;

import java.time.LocalTime;

public class Subject {
    private Integer subject_id;
    private String title;
    private LocalTime startTime;
    private LocalTime endTime;

    public Subject() {

    }

    public Subject(Integer subject_id, String title, LocalTime startTime, LocalTime endTime) {
        this.subject_id = subject_id;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
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

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "id=" + subject_id +
                ", title='" + title + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
