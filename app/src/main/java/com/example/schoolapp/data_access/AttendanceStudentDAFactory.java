package com.example.schoolapp.data_access;

import android.content.Context;

public class AttendanceStudentDAFactory {
    public static IAttendanceStudentDA create(Context context) {
        return new AttendanceStudentDA(context);
    }
}
