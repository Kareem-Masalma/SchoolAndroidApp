package com.example.schoolapp.models;

public class Registrar {
    private Integer id;
    private Integer userId;

    public Registrar() {

    }

    public Registrar(Integer id, Integer userId) {
        this.id = id;
        this.userId = userId;
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

    @Override
    public String toString() {
        return "Registrar{" +
                "id=" + id +
                ", userId=" + userId +
                '}';
    }
}
