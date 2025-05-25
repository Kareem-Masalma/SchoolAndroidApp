package com.example.schoolapp.models;

public class Subject {
    private Integer subject_id;
    private Integer class_id;
    private String title;

    public Subject() {

    }

    public Subject(Integer subject_id, Integer class_id, String title) {
        this.subject_id = subject_id;
        this.class_id = class_id;
        this.title = title;
    }

    public Integer getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(Integer subject_id) {
        this.subject_id = subject_id;
    }

    public Integer getClass_id() {
        return class_id;
    }

    public void setClass_id(Integer class_id) {
        this.class_id = class_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    @Override
    public String toString() {
        return "Subject{" +
                "subjectId=" + subject_id +
                "classId=" + class_id +
                ", title='" + title + '\'' +
                '}';
    }
}
