package com.example.schoolapp.models;

import java.time.LocalDate;

public class ScheduleSubject {
    private int scheduleId;
    private int subjectId;
    private String subject;
    private int classId;
    private String className;
    private String day;
    private String startTime;
    private String endTime;
    private String semester;
    private int year = LocalDate.now().getYear();

    public ScheduleSubject() {
    }

    public ScheduleSubject(int scheduleId, int subjectId, int classId, String subject, String className, String day, String startTime, String endTime) {
        this.scheduleId = scheduleId;
        this.subjectId = subjectId;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.classId = classId;
        this.className = className;
        this.subject = subject;
    }


    public ScheduleSubject(int scheduleId, int subjectId, int classId, String subject, String className, String day, String startTime, String endTime, String semester, int year) {
        this.scheduleId = scheduleId;
        this.subjectId = subjectId;
        this.subject = subject;
        this.classId = classId;
        this.className = className;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.semester = semester;
        this.year = year;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
