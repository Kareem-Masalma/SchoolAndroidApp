package com.example.schoolapp.models;

import java.time.LocalDate;

public class Teacher extends User {
    private Integer teacher_id;
    private String speciality;
    private Integer schedule_id;

    public Teacher() {

    }

    public Teacher(Integer user_id, String firstName, String lastName, LocalDate birthDate, String address, String phone, Role role, String speciality, Integer schedule_id) {
        super(user_id, firstName, lastName, birthDate, address, phone, role);

        this.speciality = speciality;
        this.schedule_id = schedule_id;
    }

    public Integer getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(Integer teacher_id) {
        this.teacher_id = teacher_id;
    }


    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public Integer getSchedule_id() {
        return schedule_id;
    }

    public void setSchedule_id(Integer schedule_id) {
        this.schedule_id = schedule_id;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "teacher_id=" + teacher_id +
                ", user_id=" + super.getUser_id() +
                ", speciality='" + speciality + '\'' +
                ", scheduleId=" + schedule_id +
                '}';
    }
}
