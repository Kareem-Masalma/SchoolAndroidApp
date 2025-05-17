package com.example.schoolapp.models;

public class Assignment {
    private Integer assignment_id;
    private String title;

    public Assignment() {

    }

    public Assignment(Integer assignment_id, String title) {
        this.assignment_id = assignment_id;
        this.title = title;
    }

    public Integer getAssignment_id() {
        return assignment_id;
    }

    public void setAssignment_id(Integer assignment_id) {
        this.assignment_id = assignment_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "id=" + assignment_id +
                ", title='" + title + '\'' +
                '}';
    }
}
