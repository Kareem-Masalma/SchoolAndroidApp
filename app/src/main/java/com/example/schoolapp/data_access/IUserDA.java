package com.example.schoolapp.data_access;


import com.example.schoolapp.models.Attendance;
import com.example.schoolapp.models.Student;
import com.example.schoolapp.models.User;

import java.util.List;

public interface IUserDA {
    void getUserById(int id, SingleUserCallback callback);
    void getAllUsers(UserListCallback callback);
    void addUser(User user, UserDA.BaseCallback callback);
    void updateUser(User student, UserDA.BaseCallback callback);
    void deleteUser(int id, UserDA.BaseCallback callback);


    interface SingleUserCallback {
        void onSuccess(User a);
        void onError(String error);
    }

    interface UserListCallback {
        void onSuccess(List<User> list);
        void onError(String error);
    }

    interface BaseCallback {
        void onSuccess(String message);
        void onError(String error);
    }
}