package com.example.schoolapp.models;

import androidx.annotation.NonNull;

public class SchoolClass {
    private int classId;
    private String className;
    private int classManagerId;
    private int scheduleId;
    private String manager;

    public SchoolClass() {

    }

    public SchoolClass(int classId, String className, int classManagerId, String manager) {
        this.classId = classId;
        this.className = className;
        this.classManagerId = classManagerId;
        this.manager = manager;
    }

    public SchoolClass(int classId, String className, int classManagerId, String manager, int scheduleId) {
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

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    @NonNull
    @Override
    public String toString() {
        return className;
    }
}
