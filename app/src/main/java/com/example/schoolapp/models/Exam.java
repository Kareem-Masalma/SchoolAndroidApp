package com.example.schoolapp.models;

public class Exam {
    private Integer exam_id;
    private String title;

    public Exam() {

    }

    public Exam(Integer exam_id, String title) {
        this.exam_id = exam_id;
        this.title = title;
    }

    public Integer getExam_id() {
        return exam_id;
    }

    public void setExam_id(Integer exam_id) {
        this.exam_id = exam_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Exam{" +
                "id=" + exam_id +
                ", title='" + title + '\'' +
                '}';
    }
}
