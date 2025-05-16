package com.example.schoolapp.models;

public class Schedule_subject {
    private Integer id;
    private Integer scheduleId;
    private Integer subjectId;
    private String day;

    public Schedule_subject() {

    }

    public Schedule_subject(Integer id, Integer scheduleId, Integer subjectId, String day) {
        this.id = id;
        this.scheduleId = scheduleId;
        this.subjectId = subjectId;
        this.day = day;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return "Schedule_subject{" +
                "id=" + id +
                ", scheduleId=" + scheduleId +
                ", subjectId=" + subjectId +
                ", day='" + day + '\'' +
                '}';
    }
}
