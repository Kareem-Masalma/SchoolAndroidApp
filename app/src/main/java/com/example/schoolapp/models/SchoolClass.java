package com.example.schoolapp.models;

public class SchoolClass { // we cant have a class name called 'Class' in java, that causes issues
    private Integer id;
    private String name;
    private Integer managerId; // teacher.id
    private Integer scheduleId;

    public SchoolClass() {

    }

    public SchoolClass(Integer id, String name, Integer managerId, Integer scheduleId) {
        this.id = id;
        this.name = name;
        this.managerId = managerId;
        this.scheduleId = scheduleId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public Integer getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }

    @Override
    public String toString() {
        return "SchoolClass{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", managerId=" + managerId +
                ", scheduleId=" + scheduleId +
                '}';
    }
}
