package com.example.schoolapp.models;

public class Schedule {
    private Integer schedule_id;

    public Schedule() {

    }

    public Schedule(Integer schedule_id) {
        this.schedule_id = schedule_id;
    }

    public Integer getSchedule_id() {
        return schedule_id;
    }

    public void setSchedule_id(Integer schedule_id) {
        this.schedule_id = schedule_id;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "id=" + schedule_id +
                '}';
    }
}
