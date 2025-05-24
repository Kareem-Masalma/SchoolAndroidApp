package com.example.schoolapp.models;

public class ScheduleSubject {
    private int scheduleSubjectId;
    private int scheduleId;
    private int classId;
    private int subjectId;
    private String day;
    private String startTime;
    private String endTime;

    public ScheduleSubject() {
    }

    public ScheduleSubject(int scheduleSubjectId, int scheduleId, int classId, int subjectId, String day, String startTime, String endTime) {
        this.scheduleSubjectId = scheduleSubjectId;
        this.scheduleId = scheduleId;
        this.classId = classId;
        this.subjectId = subjectId;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and Setters
    public int getScheduleSubjectId() {
        return scheduleSubjectId;
    }

    public void setScheduleSubjectId(int scheduleSubjectId) {
        this.scheduleSubjectId = scheduleSubjectId;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
