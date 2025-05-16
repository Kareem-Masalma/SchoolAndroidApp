package com.example.schoolapp.data_access;

import com.example.schoolapp.models.Teacher;
import com.example.schoolapp.models.User;

import java.util.List;

public interface IUserDA {

    User findById(int id);
    List<User> getAllUsers();
    void save(User user);
    void update(User user);
    void delete(int id);
}
