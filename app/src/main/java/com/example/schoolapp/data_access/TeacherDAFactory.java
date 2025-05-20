package com.example.schoolapp.data_access;

import android.content.Context;

public class TeacherDAFactory {
    public static ITeacherDA getTeacherDA(Context context) {
        return new TeacherDA(context);
    }
}