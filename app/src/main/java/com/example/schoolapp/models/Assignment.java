package com.example.schoolapp.models;

public class Assignment {
    private Integer id;
    private String title;

    public Assignment() {

    }

    public Assignment(Integer id, String title) {
        this.id = id;
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }
}
