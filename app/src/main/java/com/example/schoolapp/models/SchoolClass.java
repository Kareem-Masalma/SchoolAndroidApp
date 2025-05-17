package com.example.schoolapp.models;

public class SchoolClass { // we cant have a class name called 'Class' in java, that causes issues
    private Integer class_id;
    private String name;
    private Integer manager_id; // teacher.id
    private Integer schedule_id;

    public SchoolClass() {

    }

    public SchoolClass(Integer class_id, String name, Integer manager_id, Integer schedule_id) {
        this.class_id = class_id;
        this.name = name;
        this.manager_id = manager_id;
        this.schedule_id = schedule_id;
    }

    public Integer getClass_id() {
        return class_id;
    }

    public void setClass_id(Integer class_id) {
        this.class_id = class_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getManager_id() {
        return manager_id;
    }

    public void setManager_id(Integer manager_id) {
        this.manager_id = manager_id;
    }

    public Integer getSchedule_id() {
        return schedule_id;
    }

    public void setSchedule_id(Integer schedule_id) {
        this.schedule_id = schedule_id;
    }

    @Override
    public String toString() {
        return "SchoolClass{" +
                "id=" + class_id +
                ", name='" + name + '\'' +
                ", managerId=" + manager_id +
                ", scheduleId=" + schedule_id +
                '}';
    }
}
