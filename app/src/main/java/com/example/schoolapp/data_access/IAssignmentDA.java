package com.example.schoolapp.data_access;

import android.net.Uri;

import java.util.List;

public interface IAssignmentDA {
    void sendAssignment(String title, String details, String className, String deadline, float percentage, List<Uri> files, AssignmentDA.BaseCallback callback);
}
