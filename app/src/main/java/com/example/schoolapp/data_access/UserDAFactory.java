package com.example.schoolapp.data_access;

import android.content.Context;

public class UserDAFactory {
    public static IUserDA getUserDA(Context cxt) {
        return new UserDA(cxt);

    }
}
