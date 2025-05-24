package com.example.schoolapp.data_access;

import android.content.Context;

public class AttendanceDAFactory {
    public static IAttendanceDA create(Context context) {
        return new AttendanceDA(context);
    }
}
