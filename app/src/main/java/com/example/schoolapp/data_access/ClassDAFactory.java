package com.example.schoolapp.data_access;

import android.content.Context;

public class ClassDAFactory {
    public static ClassDA getClassDA(Context context) {
        return new ClassDA(context);
    }
}
