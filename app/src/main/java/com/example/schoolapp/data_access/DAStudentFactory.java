package com.example.schoolapp.data_access;

import android.content.Context;

public class DAStudentFactory {
    public static IStudentDA getStudentDA(Context context) {
        return new StudentDA(context);
    }
}
