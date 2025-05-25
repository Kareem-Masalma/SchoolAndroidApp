package com.example.schoolapp.data_access;

import java.util.List;
import com.example.schoolapp.models.Attendance_student;

public interface IAttendanceStudentDA {
    void getAttendanceStudent(int attendanceId, int studentId, SingleAttendance_StudentCallback cb);
    void getAllAttendanceStudents(Attendance_StudentListCallback cb);
    void addAttendanceStudent(Attendance_student as, BaseCallback cb);
    void updateAttendanceStudent(Attendance_student as, BaseCallback cb);
    void deleteAttendanceStudent(int attendanceId, int studentId, BaseCallback cb);

    interface SingleAttendance_StudentCallback {
        void onSuccess(Attendance_student as);
        void onError(String error);
    }

    interface Attendance_StudentListCallback {
        void onSuccess(List<Attendance_student> list);
        void onError(String error);
    }

    interface BaseCallback {
        void onSuccess(String message);
        void onError(String error);
    }
}