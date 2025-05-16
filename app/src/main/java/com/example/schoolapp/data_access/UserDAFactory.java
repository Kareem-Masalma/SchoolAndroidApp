package com.example.schoolapp.data_access;

public class UserDAFactory {
    public static IUserDA getBookDA(){
        return new UserDA();
    }
}
