package com.example.schoolapp.models;

public class Teacher {
    private Integer id;
    private Integer userId;
    private String speciality;
    private Integer scheduleId;

    public Teacher() {

    }

    public Teacher(Integer id, Integer userId, String speciality, Integer scheduleId) {
        this.id = id;
        this.userId = userId;
        this.speciality = speciality;
        this.scheduleId = scheduleId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public Integer getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "id=" + id +
                ", userId=" + userId +
                ", speciality='" + speciality + '\'' +
                ", scheduleId=" + scheduleId +
                '}';
    }
}
