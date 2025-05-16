package com.example.schoolapp.data_access;

import com.example.schoolapp.models.User;

import java.util.Collections;
import java.util.List;

public class UserDA implements IUserDA {

    @Override
    public User findById(int id) {
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        return Collections.emptyList();
    }

    @Override
    public void save(User user) {

    }

    @Override
    public void update(User user) {

    }

    @Override
    public void delete(int id) {

    }
}
