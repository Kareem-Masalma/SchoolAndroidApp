package com.example.schoolapp.models;

import java.time.LocalDate;

public class Registrar extends User {
    private Integer registrar_id;

    public Registrar() {

    }

    public Registrar(Integer user_id, String firstName, String lastName, LocalDate birthDate, String address, String phone, Role role, Integer registrar_id) {
        super(user_id, firstName, lastName, birthDate, address, phone, role);
        this.registrar_id = registrar_id;
    }

    public Integer getRegistrar_id() {
        return registrar_id;
    }

    public void setRegistrar_id(Integer registrar_id) {
        this.registrar_id = registrar_id;
    }


    @Override
    public String toString() {
        return "Registrar{" +
                "registrar_id=" + registrar_id +
                ", user_id=" + super.getUser_id() +
                '}';
    }
}
