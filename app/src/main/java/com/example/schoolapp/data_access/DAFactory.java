package com.example.schoolapp.data_access;

import android.content.Context;

public class DAFactory {
    public static ITeacherDA getTeacherDA(Context context) {
        return new TeacherDA(context);
    }

    public static IStudentDA getStudentDA(Context context) {
        return new StudentDA(context);
    }
}