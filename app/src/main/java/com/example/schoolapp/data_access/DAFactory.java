package com.example.schoolapp.data_access;

import android.content.Context;

public class DAFactory {
    public static ITeacherDA getTeacherDA() {
        return new TeacherDA();
    }
}