package com.example.schoolapp.data_access;

import java.util.List;
import com.example.schoolapp.models.Attendance;

public interface IAttendanceDA {
    void getAttendanceById(int attendanceId, SingleAttendanceCallback cb);
    void getAllAttendance(AttendanceListCallback cb);
    void addAttendance(Attendance a, BaseCallback cb);
    void updateAttendance(Attendance a, BaseCallback cb);
    void deleteAttendance(int attendanceId, BaseCallback cb);

    interface SingleAttendanceCallback {
        void onSuccess(Attendance a);
        void onError(String error);
    }

    interface AttendanceListCallback {
        void onSuccess(List<Attendance> list);
        void onError(String error);
    }

    interface BaseCallback {
        void onSuccess(String message);
        void onError(String error);
    }
}