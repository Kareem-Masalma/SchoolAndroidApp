package com.example.schoolapp.models;

public class Schedule {
    private Integer id;

    public Schedule() {

    }

    public Schedule(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "id=" + id +
                '}';
    }
}
