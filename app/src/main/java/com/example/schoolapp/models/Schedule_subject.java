package com.example.schoolapp.models;

public class Schedule_subject {
    private Integer schedule_subject_id;
    private Integer schedule_id;
    private Integer subject_id;
    private String day;

    public Schedule_subject() {

    }

    public Schedule_subject(Integer schedule_subject_id, Integer schedule_id, Integer subject_id, String day) {
        this.schedule_subject_id = schedule_subject_id;
        this.schedule_id = schedule_id;
        this.subject_id = subject_id;
        this.day = day;
    }

    public Integer getSchedule_subject_id() {
        return schedule_subject_id;
    }

    public void setSchedule_subject_id(Integer schedule_subject_id) {
        this.schedule_subject_id = schedule_subject_id;
    }

    public Integer getSchedule_id() {
        return schedule_id;
    }

    public void setSchedule_id(Integer schedule_id) {
        this.schedule_id = schedule_id;
    }

    public Integer getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(Integer subject_id) {
        this.subject_id = subject_id;
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
                "id=" + schedule_subject_id +
                ", scheduleId=" + schedule_id +
                ", subjectId=" + subject_id +
                ", day='" + day + '\'' +
                '}';
    }
}
