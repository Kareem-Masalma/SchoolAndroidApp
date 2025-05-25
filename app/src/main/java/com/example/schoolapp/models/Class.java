package com.example.schoolapp.models;

public class Class {
    private int classId;
    private String className;
    private int classManagerId;
    private int scheduleId;
    private String manager;

    public Class() {

    }

    public Class(int classId, String className, int classManagerId, String manager) {
        this.classId = classId;
        this.className = className;
        this.classManagerId = classManagerId;
        this.manager = manager;
    }

    public Class(int classId, String className, int classManagerId, String manager, int scheduleId) {
        this(classId, className, classManagerId, manager);
        this.scheduleId = scheduleId;
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

    public int getClassManagerId() {
        return classManagerId;
    }

    public void setClassManagerId(int classManagerId) {
        this.classManagerId = classManagerId;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }
}
