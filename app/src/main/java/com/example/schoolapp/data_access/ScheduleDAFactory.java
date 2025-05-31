package com.example.schoolapp.data_access;

import android.content.Context;

public class ScheduleDAFactory {
    public static ScheduleDA getScheduleDA(Context context) {
        return new ScheduleDA(context);
    }
}
