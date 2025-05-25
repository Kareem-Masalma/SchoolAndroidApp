package com.example.schoolapp.data_access;

import android.content.Context;

public class SubjectDAFactory {

     public static ISubjectDA getSubjectDA(Context context) {
        return new SubjectDA(context);
    }
}
